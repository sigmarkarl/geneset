package org.simmi.distann.spark;

import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.connector.read.InputPartition;
import org.apache.spark.sql.connector.read.PartitionReader;
import org.apache.spark.sql.connector.read.PartitionReaderFactory;
import org.apache.spark.sql.vectorized.ColumnarBatch;

import java.io.IOException;

public class GbkReaderFactory implements PartitionReaderFactory {
    @Override
    public PartitionReader<InternalRow> createReader(InputPartition partition) {
        try {
            return new GbkPartitionReader(((GbkInputPartition) partition).path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PartitionReader<ColumnarBatch> createColumnarReader(InputPartition partition) {
        return PartitionReaderFactory.super.createColumnarReader(partition);
    }
}
