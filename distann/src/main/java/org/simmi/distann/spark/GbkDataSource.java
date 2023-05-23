package org.simmi.distann.spark;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.mapreduce.Job;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.connector.catalog.Table;
import org.apache.spark.sql.connector.catalog.TableProvider;
import org.apache.spark.sql.connector.expressions.Expression;
import org.apache.spark.sql.connector.expressions.NamedReference;
import org.apache.spark.sql.connector.expressions.Transform;
import org.apache.spark.sql.execution.datasources.OutputWriterFactory;
import org.apache.spark.sql.execution.datasources.TextBasedFileFormat;
import org.apache.spark.sql.sources.BaseRelation;
import org.apache.spark.sql.sources.DataSourceRegister;
import org.apache.spark.sql.sources.RelationProvider;
import org.apache.spark.sql.sources.SchemaRelationProvider;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;
import scala.Option;
import scala.collection.immutable.Seq;

import java.util.Map;

public class GbkDataSource extends TextBasedFileFormat implements RelationProvider, SchemaRelationProvider, TableProvider, DataSourceRegister {

    static StructType schema = StructType.fromDDL("name string, seq string, tag string, value string");

    public GbkDataSource() {
        super();
    }

    @Override
    public boolean supportsExternalMetadata() {
        return true;
    }

    @Override
    public StructType inferSchema(CaseInsensitiveStringMap options) {
        return schema;
    }

    @Override
    public Transform[] inferPartitioning(CaseInsensitiveStringMap options) {
        return new Transform[] {
                new Transform() {
                    @Override
                    public String name() {
                        return "gbk";
                    }

                    @Override
                    public Expression[] arguments() {
                        return new Expression[] {
                                new Expression() {
                                    @Override
                                    public String describe() {
                                        return Expression.super.describe();
                                    }

                                    @Override
                                    public Expression[] children() {
                                        return new Expression[0];
                                    }

                                    @Override
                                    public NamedReference[] references() {
                                        return Expression.super.references();
                                    }
                                }
                        };
                    }
                }
        };
    }

    @Override
    public Table getTable(StructType schema, Transform[] partitioning, Map<String, String> properties) {
        return new GbkTable();
    }

    @Override
    public String shortName() {
        return "gbff";
    }

    @Override
    public BaseRelation createRelation(SQLContext sqlContext, scala.collection.immutable.Map<String, String> parameters) {
        return new BaseRelation() {
            @Override
            public SQLContext sqlContext() {
                return sqlContext;
            }

            @Override
            public StructType schema() {
                return schema;
            }
        };
    }

    @Override
    public BaseRelation createRelation(SQLContext sqlContext, scala.collection.immutable.Map<String, String> parameters, StructType schema) {
        return null;
    }

    @Override
    public Option<StructType> inferSchema(SparkSession sparkSession, scala.collection.immutable.Map<String, String> options, Seq<FileStatus> files) {
        return Option.apply(schema);
    }

    @Override
    public OutputWriterFactory prepareWrite(SparkSession sparkSession, Job job, scala.collection.immutable.Map<String, String> options, StructType dataSchema) {
        return null;
    }

    public static void main(String[] args) {
        var spark = SparkSession.builder().master("local").getOrCreate();
        var df = spark.read().format("org.simmi.distann.spark.GbkDataSource").load("/Users/sigmar/thermus/Thermus_aquaticus_YT-1.gbff");
        System.out.println(df.schema());
        System.out.println(df.count());
        df.collectAsList().forEach(System.out::println);
    }
}