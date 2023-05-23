package org.simmi.distann.spark;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.Expression;
import org.apache.spark.sql.connector.catalog.*;
import org.apache.spark.sql.connector.metric.CustomMetric;
import org.apache.spark.sql.connector.read.Batch;
import org.apache.spark.sql.connector.read.PartitionReaderFactory;
import org.apache.spark.sql.connector.read.Scan;
import org.apache.spark.sql.connector.read.ScanBuilder;
import org.apache.spark.sql.connector.read.streaming.ContinuousStream;
import org.apache.spark.sql.connector.read.streaming.MicroBatchStream;
import org.apache.spark.sql.connector.write.LogicalWriteInfo;
import org.apache.spark.sql.connector.write.SupportsTruncate;
import org.apache.spark.sql.connector.write.WriteBuilder;
import org.apache.spark.sql.execution.datasources.PartitioningAwareFileIndex;
import org.apache.spark.sql.execution.datasources.v2.TextBasedFileScan;
import org.apache.spark.sql.sources.Filter;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;
import scala.collection.immutable.Seq;

import java.nio.file.Path;
import java.util.Set;

public class GbkTable implements Table, SupportsRead, SupportsWrite, SupportsDelete {

    @Override
    public void deleteWhere(Filter[] filters) {

    }

    @Override
    public ScanBuilder newScanBuilder(CaseInsensitiveStringMap options) {
        var path = options.get("path");
        return new GbkScanBuilder(path);
    }

    @Override
    public WriteBuilder newWriteBuilder(LogicalWriteInfo info) {
        return null;
    }

    @Override
    public String name() {
        return "gbk";
    }

    @Override
    public StructType schema() {
        return GbkDataSource.schema;
    }

    @Override
    public Set<TableCapability> capabilities() {
        return Set.of(TableCapability.BATCH_READ);
    }
}
