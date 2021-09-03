package com.github.zmigueel.radinho.command.impl

import com.github.zmigueel.radinho.audio.players
import com.github.zmigueel.radinho.command.command
import com.github.zmigueel.radinho.command.getGuild
import com.github.zmigueel.radinho.util.getGuildEmoji
import dev.kord.core.behavior.interaction.followUp
import dev.kord.core.behavior.interaction.followUpEphemeral

suspend fun skipCommand() = command("skip", "Vá para a próxima música da fila.") {
    val guild = this.getGuild() ?: return@command

    val player = players[guild]
        ?: return@command this.acknowledgeEphemeral().followUpEphemeral {
            content = "${getGuildEmoji("error")} | Não estou tocando nada no momento."
        }.let {}

    this.acknowledgePublic().followUp {
        content = "${getGuildEmoji("correct")} | Indo para a próxima música..."
    }
    player.stopTrack()
}