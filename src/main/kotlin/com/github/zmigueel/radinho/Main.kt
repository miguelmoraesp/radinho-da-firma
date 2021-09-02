package com.github.zmigueel.radinho

import com.github.zmigueel.radinho.audio.RadioMusicManager
import com.github.zmigueel.radinho.command.loadCommands
import com.github.zmigueel.radinho.command.slashCommands
import com.github.zmigueel.radinho.config.loadConfig
import dev.kord.core.Kord
import dev.kord.core.entity.interaction.CommandInteraction
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.interaction.ChatInputCommandCreateEvent
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

lateinit var kord: Kord

val config by lazy {
    loadConfig()
}

val musicManager = RadioMusicManager()

val coroutineScope = CoroutineScope(
    Executors.newCachedThreadPool().asCoroutineDispatcher()
)

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    kord = Kord(config.discord.token) {
        intents = Intents.all
    }

    kord.on<ReadyEvent> {
        println("evento")
    }

    kord.on<ChatInputCommandInteractionCreateEvent> {
        val command = slashCommands[interaction.name] ?: return@on

        command.action.invoke(interaction)
    }

    loadCommands()

    kord.login {
        playing("sim")
    }
}
