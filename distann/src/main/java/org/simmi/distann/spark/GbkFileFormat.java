package org.simmi.distann.spark;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.execution.datasources.OutputWriterFactory;
import org.apache.spark.sql.execution.datasources.TextBasedFileFormat;
import org.apache.spark.sql.sources.DataSourceRegister;
import org.apache.spark.sql.types.StructType;
import scala.Option;
import scala.collection.immutable.Map;
import scala.collection.immutable.Seq;

public class GbkFileFormat extends TextBasedFileFormat implements DataSourceRegister {
    static StructType schema = StructType.fromDDL("name string, seq string, tag string, value string");

    @Override
    public Option<StructType> inferSchema(SparkSession sparkSession, Map<String, String> options, Seq<FileStatus> files) {
        return Option.apply(schema);
    }

    @Override
    public OutputWriterFactory prepareWrite(SparkSession sparkSession, Job job, Map<String, String> options, StructType dataSchema) {
        return null;
    }

    @Override
    public String shortName() {
        return "gbk";
    }

    public static void main(String[] args) {
        var spark = SparkSession.builder().master("local").getOrCreate();
        var df = spark.read().format("gbk").load("/Users/sigmar/newthermus");
        System.err.println(df.schema());
        System.err.println(df.count());
    }
}
