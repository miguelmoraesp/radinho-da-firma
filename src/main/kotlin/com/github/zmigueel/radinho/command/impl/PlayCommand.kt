package com.github.zmigueel.radinho.command.impl

import com.github.zmigueel.radinho.audio.*
import com.github.zmigueel.radinho.command.command
import com.github.zmigueel.radinho.coroutineScope
import dev.schlaubi.lavakord.audio.TrackEndEvent
import dev.schlaubi.lavakord.audio.TrackStartEvent
import dev.schlaubi.lavakord.audio.on
import dev.schlaubi.lavakord.rest.TrackResponse
import dev.schlaubi.lavakord.rest.TrackResponse.LoadType
import dev.schlaubi.lavakord.rest.loadItem
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.interactions.commands.OptionType

suspend fun playCommand() = command("play", "Musica", {
    addOption(OptionType.STRING, "input", "Nome ou link da música", true)
}) {
    val member = this.member ?: return@command
    val input = this.getOption("input")?.asString ?: return@command

    val search = if (input.startsWith("http")) {
        input
    } else {
        "ytsearch:$input"
    }

    val voiceChannel = member.voiceState?.channel ?: return@command reply("Você precisa estar em um canal de voz burro")
        .setEphemeral(true)
        .queue()

    link.connectAudio(voiceChannel.idLong)

    val result = link.loadItem(search)

    player.channel = this.textChannel

    when (result.loadType) {
        LoadType.SEARCH_RESULT -> player.queue(result.tracks.firstOrNull())
        LoadType.TRACK_LOADED -> player.queue(result.tracks.firstOrNull())
        LoadType.PLAYLIST_LOADED -> {
            result.tracks.forEach { player.queue(it) }
        }
        LoadType.NO_MATCHES -> reply("Não encontrei nada")
            .setEphemeral(true)
            .queue()
        LoadType.LOAD_FAILED -> reply("Falhou")
            .setEphemeral(true)
            .queue()
    }
}