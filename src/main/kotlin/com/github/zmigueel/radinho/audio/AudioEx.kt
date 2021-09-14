package com.github.zmigueel.radinho.audio

import com.github.zmigueel.radinho.coroutineScope
import com.github.zmigueel.radinho.kord
import com.github.zmigueel.radinho.util.getGuildEmoji
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.edit
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.message.modify.actionRow
import dev.kord.x.emoji.Emojis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

val players = mutableMapOf<GuildBehavior, Player>()

val emojis = mapOf<Int, String>(
    1 to Emojis.one.unicode,
    2 to Emojis.two.unicode,
    3 to Emojis.three.unicode,
    4 to Emojis.four.unicode,
    5 to Emojis.five.unicode,
    6 to Emojis.six.unicode,
    7 to Emojis.seven.unicode,
    8 to Emojis.eight.unicode,
    9 to Emojis.nine.unicode,
    10 to Emojis.`1234`.unicode
)

fun TrackScheduler.queue(track: AudioTrack?) {
    if (!player.startTrack(track!!, true)) {
        queue.offer(track)
    }
}

suspend fun TrackScheduler.nextTrack() {
    nowPlayingMessage?.delete()

    val track = queue.poll()
    if (track == null) {
        player.leaveTask = coroutineScope.launch {
            delay(120000)
            if (player.playingTrack != null) return@launch

            this@nextTrack.player.channel.createMessage {
                content = "\uD83D\uDE34 | A música acabou..."
            }

            this@nextTrack.player.destroy()
        }
        return
    }

    player.startTrack(track, false)
}

@OptIn(ExperimentalTime::class)
suspend fun onSearch(
    message: PublicFollowupMessage,
    user: UserBehavior,
    player: Player,
    tracks: List<AudioTrack>
) {
    message.edit {
        content = "${getGuildEmoji("correct")} | Encontrei alguns resultados, escolha-o abaixo ou clique no botão para cancelar."

        actionRow {
            selectMenu("on-select") {
                this.placeholder = "Selecione sua música"
                for (i in 0..9) {
                    val track = tracks.getOrNull(i) ?: continue

                    option(track.info.title, track.identifier) {
                        emoji = DiscordPartialEmoji(
                            name = emojis[i + 1]
                        )
                    }
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
            content = "${getGuildEmoji("music_notes")} | Adicionado na fila: `${first.info.title}`."
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