plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"

    id("com.github.johnrengelman.shadow") version "7.0.0"

    application
}

group = "com.github.zmigueel"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.github.zmigueel.radinho.MainKt")
}

repositories {
    mavenCentral()

    maven("https://jcenter.bintray.com/")
    maven("https://m2.dv8tion.net/releases")
    maven("https://maven.kotlindiscord.com/repository/maven-public/")
    maven("https://schlaubi.jfrog.io/artifactory/lavakord/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")

    implementation("dev.kord:kord-core:0.8.0-M5")
    implementation("dev.kord.x:emoji:0.5.0")
    implementation("com.sedmelluq:lavaplayer:1.3.77")

    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.google.code.gson:gson:2.8.8")

    implementation("com.typesafe:config:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.0.0-RC")
}
