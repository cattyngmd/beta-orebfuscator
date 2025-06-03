plugins {
    id("java")
}

group = "lishid"
version = "1.0.0"

val poseidon_version: String by project

repositories {
    maven {
        name = "johnymuffin-nexus-releases"
        url = uri("https://repository.johnymuffin.com/repository/maven-public/")
    }
    mavenCentral()
}

dependencies {
    compileOnly("com.legacyminecraft.poseidon:poseidon-craftbukkit:${poseidon_version}")
}

tasks.test {
    useJUnitPlatform()
}