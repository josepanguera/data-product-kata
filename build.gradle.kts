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
