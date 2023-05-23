package org.simmi.distann.spark;

import org.apache.spark.sql.connector.read.InputPartition;

import java.io.Serializable;

public class GbkInputPartition implements InputPartition, Serializable {
    public String path;

    public GbkInputPartition(String path) {
        this.path = path;
    }

}
