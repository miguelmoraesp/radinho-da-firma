plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("kapt") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.21"
}

group = "com.github.zmigueel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://m2.dv8tion.net/releases")
    maven("https://maven.kotlindiscord.com/repository/maven-public/")
    maven("https://schlaubi.jfrog.io/artifactory/lavakord/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")

    implementation("dev.kord:kord-core:0.8.0-M5")
    implementation("com.sedmelluq:lavaplayer:1.3.77")

    implementation("com.typesafe:config:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.0.0-RC")
}
