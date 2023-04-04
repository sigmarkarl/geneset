plugins {
    id("java")
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.simmi.geneset"
            artifactId = "spilling"
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

dependencies {
    implementation(project(":Treedraw"))
    //compile group: 'org.apache.poi', name: 'poi', version: '3.17'
    //compile group: 'org.apache.poi', name: 'poi-ooxml', version: '3.17'
}