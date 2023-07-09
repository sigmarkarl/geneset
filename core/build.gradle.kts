plugins {
    id("java")
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation (group = "org.apache.spark", name = "spark-core_2.13", version = "3.4.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation (group = "org.apache.spark", name = "spark-mllib_2.13", version = "3.4.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation (group = "org.apache.spark", name = "spark-kubernetes_2.13", version = "3.4.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation (group = "org.apache.spark", name = "spark-graphx_2.13", version = "3.4.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation (group = "org.apache.spark", name = "spark-sql_2.13", version = "3.4.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation (group = "org.apache.spark", name = "spark-connect_2.13", version = "3.4.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation (group = "org.apache.spark", name = "spark-hive_2.13", version = "3.4.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation (group = "org.apache.spark", name = "spark-hive-thriftserver_2.13", version = "3.4.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}