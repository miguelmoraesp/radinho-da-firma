package com.github.zmigueel.radinho.command

import com.github.zmigueel.radinho.command.impl.playCommand
import com.github.zmigueel.radinho.command.impl.skipCommand
import com.github.zmigueel.radinho.coroutineScope
import com.github.zmigueel.radinho.kord
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.ChatInputCommandBehavior
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import kotlinx.coroutines.async

val slashCommands = mutableMapOf<String, Command>()

suspend fun ChatInputCommandInteraction.getGuild() =
    kord.getGuild(this.data.guildId.value!!)

suspend fun command(name: String, description: String, action: suspend ChatInputCommandInteraction.() -> Unit) {
    slashCommands[name] = Command(name, description, action)
    kord.createGuildChatInputCommand(Snowflake(839898721315061800), name, description)
}

suspend fun command(
    name: String, description: String,
    builder: ChatInputCreateBuilder.() -> Unit,
    action: suspend ChatInputCommandInteraction.() -> Unit
) {
    slashCommands[name] = Command(name, description, action)
    kord.createGuildChatInputCommand(Snowflake(839898721315061800), name, description, builder)
}

suspend fun loadCommands() {
    playCommand()
    skipCommand()
}