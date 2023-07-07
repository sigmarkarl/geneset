plugins {
    id("java")
    id("org.openjfx.javafxplugin")
    id("maven-publish")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.simmi.geneset"
            artifactId = "javafasta"
            version = "1.0.0"

            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri(layout.projectDirectory.dir("../repo"))
        }
    }
}

javafx {
    version = "20.0.1"
    modules("javafx.base", "javafx.graphics", "javafx.controls", "javafx.fxml", "javafx.swing", "javafx.web")
}

dependencies {
    implementation(project(":Treedraw"))
    implementation(group = "org.apache.poi", name = "poi", version = "5.2.3")
    implementation(group = "org.apache.poi", name = "poi-ooxml", version = "5.2.3")
    implementation(group = "org.ejml", name = "ejml-all", version = "0.41")
    implementation(group = "com.github.samtools", name = "htsjdk", version = "3.0.2")
    implementation(group = "com.googlecode.json-simple", name = "json-simple", version = "1.1")
    implementation(group = "org.json", name = "json", version = "20190722")
    implementation(group = "org.apache.spark", name = "spark-core_2.13", version = "3.4.1")
    implementation(group = "org.apache.spark", name = "spark-mllib_2.13", version = "3.4.1")
    implementation(group = "org.apache.spark", name = "spark-kubernetes_2.13", version = "3.4.1")
    //implementation(group = "org.scalanlp", name = "breeze_2.13", version = "1.1")

    implementation("com.fasterxml.jackson:jackson-bom:2.14.0-rc3")
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
}