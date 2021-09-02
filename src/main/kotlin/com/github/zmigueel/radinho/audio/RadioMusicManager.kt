package com.github.zmigueel.radinho.audio

import com.github.zmigueel.radinho.coroutineScope
import com.github.zmigueel.radinho.kord
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.tools.io.MessageInput
import com.sedmelluq.discord.lavaplayer.tools.io.MessageOutput
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.followUp
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.live.live
import dev.kord.core.live.on
import dev.kord.core.on
import dev.kord.rest.builder.message.create.actionRow
import dev.kord.rest.builder.message.create.embed
import kotlinx.coroutines.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class RadioMusicManager {

    val playerManager = DefaultAudioPlayerManager()

    init {
        playerManager.registerSourceManager(YoutubeAudioSourceManager(true))
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault())
        playerManager.registerSourceManager(BandcampAudioSourceManager())
        playerManager.registerSourceManager(VimeoAudioSourceManager())
        playerManager.registerSourceManager(TwitchStreamAudioSourceManager())
        playerManager.registerSourceManager(BeamAudioSourceManager())

        AudioSourceManagers.registerRemoteSources(playerManager)
        AudioSourceManagers.registerLocalSource(playerManager)
    }

    @OptIn(ExperimentalTime::class)
    suspend fun loadAndPlay(user: UserBehavior, player: Player, search: String, channel: MessageChannelBehavior) {
        playerManager.loadItem(search, object: AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack?) {
                coroutineScope.launch {
                    channel.createEmbed {
                        title = "${track?.info?.title} adicionado na fila"
                    }

                    player.scheduler.queue(track!!)
                }
            }

            override fun playlistLoaded(playlist: AudioPlaylist?) {
                coroutineScope.launch {
                    if (playlist == null) return@launch

                    if (playlist.isSearchResult) {
                        val tracks = playlist.tracks
                        onSearch(user, player, tracks, channel)
                        return@launch
                    }

                    playlist.tracks.forEach { player.scheduler.queue(it) }
                    channel.createMessage {
                        embed {
                            title = "seguinte parcero foi adicionado ${playlist.tracks.size} da playlist ${playlist.name}"
                        }
                    }
                }
            }

            override fun noMatches() {
                coroutineScope.launch {
                    channel.createEmbed {
                        title = "n acho"
                    }
                }
            }

            override fun loadFailed(exception: FriendlyException?) {
                coroutineScope.launch {
                    channel.createEmbed {
                        title = "falhou"
                    }
                }
            }

        })
    }

}