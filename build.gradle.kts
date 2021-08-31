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
    maven("https://schlaubi.jfrog.io/artifactory/lavakord/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")

    implementation("net.dv8tion", "JDA", "4.3.0_313")
    implementation("dev.schlaubi.lavakord", "jda", "2.0.0")

    implementation("com.typesafe:config:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.0.0-RC")
}
