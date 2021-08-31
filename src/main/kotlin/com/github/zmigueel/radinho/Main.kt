package com.github.zmigueel.radinho

import com.github.zmigueel.radinho.command.CommandListener
import com.github.zmigueel.radinho.command.command
import com.github.zmigueel.radinho.command.loadCommands
import com.github.zmigueel.radinho.config.loadConfig
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.jda.buildWithLavakord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import java.util.concurrent.Executors

lateinit var jda: JDA
lateinit var lavaKord: LavaKord

val config by lazy {
    loadConfig()
}

val coroutineScope = CoroutineScope(
    Executors.newCachedThreadPool().asCoroutineDispatcher()
)

suspend fun main() {
    val build = JDABuilder.createDefault(config.discord.token)
        .enableIntents(
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_PRESENCES
        )
        .addEventListeners(CommandListener)
        .buildWithLavakord(coroutineScope.coroutineContext)

    jda = build.jda
    lavaKord = build.lavakord

    config.lavalink.nodes.forEach {
        lavaKord.addNode(it, config.lavalink.password)
    }

    loadCommands()
}
