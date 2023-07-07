package org.simmi.geneset.spark;

import org.apache.spark.sql.connector.metric.CustomMetric;
import org.apache.spark.sql.connector.read.Batch;
import org.apache.spark.sql.connector.read.InputPartition;
import org.apache.spark.sql.connector.read.Scan;
import org.apache.spark.sql.connector.read.streaming.ContinuousStream;
import org.apache.spark.sql.connector.read.streaming.MicroBatchStream;
import org.apache.spark.sql.types.StructType;

import java.nio.file.Path;

public class GbkScan implements Scan, Batch {

    String path;

    public GbkScan(String path) {
        this.path = path;
    }

    @Override
    public InputPartition[] planInputPartitions() {
        return new InputPartition[] {
                new GbkInputPartition(path)
        };
    }

    @Override
    public GbkReaderFactory createReaderFactory() {
        return new GbkReaderFactory();
    }

    @Override
    public StructType readSchema() {
        return GbkDataSource.schema;
    }

    @Override
    public String description() {
        return Scan.super.description();
    }

    @Override
    public Batch toBatch() {
        return this;
    }

    @Override
    public MicroBatchStream toMicroBatchStream(String checkpointLocation) {
        return Scan.super.toMicroBatchStream(checkpointLocation);
    }

    @Override
    public ContinuousStream toContinuousStream(String checkpointLocation) {
        return Scan.super.toContinuousStream(checkpointLocation);
    }

    @Override
    public CustomMetric[] supportedCustomMetrics() {
        return Scan.super.supportedCustomMetrics();
    }
}
