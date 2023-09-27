plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        url = uri("https://nexus.devel.wallapop.com/repository/releases/")
        name = "Wallapop Private Repository"
        content { includeGroup("com.wallapop") }
        mavenContent { releasesOnly() }
    }

    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("software.amazon.glue:schema-registry-kafkastreams-serde:1.1.16")
    implementation("software.amazon.msk:aws-msk-iam-auth:1.1.9")
    implementation("org.apache.avro:avro-compiler:1.11.3")
    implementation("org.apache.kafka:kafka-clients:3.5.1")
    implementation("com.wallapop:data-product:0.0.39")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}
