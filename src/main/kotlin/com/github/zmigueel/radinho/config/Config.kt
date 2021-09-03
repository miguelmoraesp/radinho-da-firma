package com.github.zmigueel.radinho.config

import com.typesafe.config.ConfigFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import java.io.File

@Serializable
data class Config(
    val discord: Discord
) {
    @Serializable
    data class Discord(
        val token: String,
        val ownerId: String,

        val emojiGuild: String
    )
}

fun loadConfig(): Config =
    Hocon.decodeFromConfig(ConfigFactory.parseFile(File("./config.conf")))
