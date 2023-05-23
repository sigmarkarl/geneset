package org.simmi.distann.spark;

import org.apache.spark.sql.connector.read.Scan;
import org.apache.spark.sql.connector.read.ScanBuilder;

import java.nio.file.Path;

public class GbkScanBuilder implements ScanBuilder {

    String path;

    public GbkScanBuilder(String path) {
        this.path = path;
    }

    @Override
    public Scan build() {
        return new GbkScan(path);
    }
}
