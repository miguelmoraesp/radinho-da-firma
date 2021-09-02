package com.github.zmigueel.radinho.command.impl

import com.github.zmigueel.radinho.audio.*
import com.github.zmigueel.radinho.command.command
import com.github.zmigueel.radinho.command.getGuild
import com.github.zmigueel.radinho.musicManager
import dev.kord.core.behavior.interaction.followUpEphemeral
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.entity.interaction.string
import dev.kord.rest.builder.interaction.string
import dev.kord.voice.AudioFrame
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, dev.kord.common.annotation.KordVoice::class)
suspend fun playCommand() = command("play", "música", {
    string("input", "nome da musica") {
        required = true
    }
}) {
    val guild = this.getGuild()
    val member = this.user.asMember(guild?.id!!)

    val voiceChannelId = member.getVoiceStateOrNull()?.channelId
    if (voiceChannelId == null) {
        this.acknowledgeEphemeral().followUpEphemeral {
            content = "Você precisa estar em um canal de voz."
        }
        return@command
    }

    val voiceChannel = kord.getChannelOf<VoiceChannel>(voiceChannelId)
    val player = players[guild] ?: musicManager.playerManager.createPlayer(guild, this.channel)
    val self = kord.getSelf().asMember(guild.id)

    if (self.getVoiceStateOrNull()?.channelId == null) {
        voiceChannel?.connect {
            selfDeaf = true
            audioProvider {
                AudioFrame.fromData(player.provide()?.data)
            }
        }
    }

    val option = this.command.options["input"]!!.string()
    val input = if (option.startsWith("http")) {
        option
    } else {
        "ytsearch:$option"
    }

    musicManager.loadAndPlay(this.user, player, input, this.channel)


}