package com.github.zmigueel.radinho.util

import com.github.zmigueel.radinho.config
import com.github.zmigueel.radinho.kord
import dev.kord.common.entity.Snowflake
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull

suspend fun getGuildEmoji(name: String): String {
    val guild = kord.getGuild(Snowflake(config.discord.emojiGuild)) ?: return ""

    return guild.emojis.filter { it.name == name }.firstOrNull()?.mention ?: ""
}