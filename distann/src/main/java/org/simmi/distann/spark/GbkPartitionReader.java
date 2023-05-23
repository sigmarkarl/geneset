package org.simmi.distann.spark;

import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.catalyst.expressions.GenericInternalRow;
import org.apache.spark.sql.connector.read.PartitionReader;
import org.apache.spark.unsafe.types.UTF8String;
import org.simmi.javafasta.shared.Annotation;
import org.simmi.javafasta.shared.GBK2AminoFasta;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class GbkPartitionReader implements PartitionReader<InternalRow> {

    Iterator<Annotation> subit;

    GbkPartitionReader(String gbkFile) throws IOException {
        Map<String,Path> annoset = new HashMap<>();
        annoset.put("CDS", null);
        annoset.put("tRNA", null);
        annoset.put("rRNA", null);
        annoset.put("mRNA", null);

        var path = Path.of(gbkFile);
        Map<String, Stream<String>> filetextmap = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String fileName = path.getFileName().toString();
            filetextmap.put(fileName, br.lines());

            var seqmap = GBK2AminoFasta.handleText(filetextmap, annoset, null, null, null, false);
            subit = seqmap.entrySet().stream().flatMap(e -> e.getValue().stream()).filter(s -> s.annset != null).flatMap(s -> s.annset.stream()).iterator();
        }
    }

    @Override
    public boolean next() throws IOException {
        return subit != null && subit.hasNext();
    }

    @Override
    public InternalRow get() {
        var n = subit.next();
        return new GenericInternalRow(new Object[]{UTF8String.fromString(n.getName()), UTF8String.fromString(n.getProteinSequence().getSequenceString()), UTF8String.fromString(n.getTag()), UTF8String.fromString(n.getId())});
    }

    @Override
    public void close() throws IOException {

    }
}
