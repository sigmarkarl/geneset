package org.simmi.distann.spark;

import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.connector.catalog.Table;
import org.apache.spark.sql.connector.expressions.Transform;
import org.apache.spark.sql.execution.datasources.FileFormat;
import org.apache.spark.sql.execution.datasources.v2.FileDataSourceV2;
import org.apache.spark.sql.sources.BaseRelation;
import org.apache.spark.sql.sources.RelationProvider;
import org.apache.spark.sql.sources.SchemaRelationProvider;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;
import scala.collection.immutable.Seq;

import java.util.Map;

public class GbkDataSource implements RelationProvider, SchemaRelationProvider, FileDataSourceV2 {
    @Override
    public Class<? extends FileFormat> fallbackFileFormat() {
        return null;
    }

    @Override
    public void eq(String s) {
        FileDataSourceV2.super
    }

    @Override
    public Seq<String> getPaths(CaseInsensitiveStringMap map) {
        return FileDataSourceV2.super.getPaths(map);
    }

    @Override
    public CaseInsensitiveStringMap getOptionsWithoutPaths(CaseInsensitiveStringMap map) {
        return FileDataSourceV2.super.getOptionsWithoutPaths(map);
    }

    @Override
    public String getTableName(CaseInsensitiveStringMap map, Seq<String> paths) {
        return FileDataSourceV2.super.getTableName(map, paths);
    }

    @Override
    public Table getTable(CaseInsensitiveStringMap options) {
        return null;
    }

    @Override
    public Table getTable(CaseInsensitiveStringMap options, StructType schema) {
        return FileDataSourceV2.super.getTable(options, schema);
    }

    @Override
    public boolean supportsExternalMetadata() {
        return FileDataSourceV2.super.supportsExternalMetadata();
    }

    @Override
    public StructType inferSchema(CaseInsensitiveStringMap options) {
        return FileDataSourceV2.super.inferSchema(options);
    }

    @Override
    public Transform[] inferPartitioning(CaseInsensitiveStringMap options) {
        return FileDataSourceV2.super.inferPartitioning(options);
    }

    @Override
    public Table getTable(StructType schema, Transform[] partitioning, Map<String, String> properties) {
        return FileDataSourceV2.super.getTable(schema, partitioning, properties);
    }

    @Override
    public SparkSession sparkSession() {
        return null;
    }

    @Override
    public String shortName() {
        return "gbff";
    }

    @Override
    public BaseRelation createRelation(SQLContext sqlContext, scala.collection.immutable.Map<String, String> parameters) {
        return null;
    }

    @Override
    public BaseRelation createRelation(SQLContext sqlContext, scala.collection.immutable.Map<String, String> parameters, StructType schema) {
        return null;
    }

    public static void main(String[] args) {
        var spark = SparkSession.builder().master("local").getOrCreate();
        var df = spark.read().format("org.simmi.distann.spark.GbkDataSource").load("/Users/sigmar/Thermus_thermophilus_HB8.gbff");
        System.err.println(df.schema());
        System.err.println(df.count());
    }
}
