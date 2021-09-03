package com.github.zmigueel.radinho.audio

import com.github.zmigueel.radinho.kord
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.interaction.edit
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.message.modify.actionRow
import kotlin.time.ExperimentalTime

val players = mutableMapOf<GuildBehavior, Player>()

fun TrackScheduler.queue(track: AudioTrack?) {
    if (!player.startTrack(track!!, true)) {
        queue.offer(track)
    }
}

suspend fun TrackScheduler.nextTrack() {
    val track = queue.poll()

    player.startTrack(track, false)

    nowPlayingMessage?.delete()
}

@OptIn(ExperimentalTime::class)
suspend fun onSearch(
    message: PublicFollowupMessage,
    user: UserBehavior,
    player: Player,
    tracks: List<AudioTrack>
) {
    message.edit {
        content = "Encontrei alguns resultados, escolha-o abaixo ou clique no botão para cancelar."

        actionRow {
            selectMenu("on-select") {
                this.placeholder = "Selecione sua música"
                for (i in 0..9) {
                    val track = tracks.getOrNull(i) ?: continue

                    option(track.info.title, track.identifier)
                }
            }
        }

        actionRow {
            interactionButton(ButtonStyle.Danger, "cancel") {
                this.label = "Cancelar"
            }
        }
    }

    kord.on<SelectMenuInteractionCreateEvent> {
        if (this.interaction.user != user) return@on
        val identifier = this.interaction.values.first()

        val first = tracks.first { it.identifier == identifier }

        message.edit {
            components = mutableListOf()
            content = "Adicionado na fila: ${first.info.title}."
        }
        player.scheduler.queue(first)
    }

    kord.on<ButtonInteractionCreateEvent> {
        if (this.interaction.user != user) return@on
        this.interaction.acknowledgePublicDeferredMessageUpdate()

        message.delete()
    }
}

fun AudioPlayerManager.createPlayer(guild: GuildBehavior, channel: MessageChannelBehavior): Player {
    val player = Player(createPlayer(), channel)
    players[guild] = player

    return player
}