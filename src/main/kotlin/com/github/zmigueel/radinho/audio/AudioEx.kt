package com.github.zmigueel.radinho.audio

import com.github.zmigueel.radinho.kord
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.followUp
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.message.create.actionRow
import dev.kord.rest.builder.message.create.embed
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
suspend fun onSearch(user: UserBehavior, player: Player, tracks: List<AudioTrack>, channel: MessageChannelBehavior) {
    val message = channel.createMessage {
        embed {
            title = "cu"
        }

        actionRow {
            selectMenu("on-select") {
                this.placeholder = "Seleciona"
                for (i in 1..15) {
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

        message.delete()

        this.interaction.acknowledgePublic()
            .followUp {
                embed {
                    title = "${first.info.title} adicionada na fila"
                }
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