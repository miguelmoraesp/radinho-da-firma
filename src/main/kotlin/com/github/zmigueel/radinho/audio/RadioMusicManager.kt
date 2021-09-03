package com.github.zmigueel.radinho.audio

import com.github.zmigueel.radinho.coroutineScope
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.interaction.edit
import dev.kord.core.entity.interaction.PublicFollowupMessage
import kotlinx.coroutines.*
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
    suspend fun loadAndPlay(
        message: PublicFollowupMessage,
        user: UserBehavior,
        player: Player,
        search: String
    ) {
        playerManager.loadItem(search, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack?) {
                coroutineScope.launch {
                    message.edit {
                        content = "Adicionado na fila: ${track?.info?.title}."
                    }

                    player.scheduler.queue(track!!)
                }
            }

            override fun playlistLoaded(playlist: AudioPlaylist?) {
                coroutineScope.launch {
                    if (playlist == null) return@launch

                    if (playlist.isSearchResult) {
                        val tracks = playlist.tracks
                        onSearch(message, user, player, tracks)
                        return@launch
                    }

                    playlist.tracks.forEach { player.scheduler.queue(it) }
                    message.edit {
                        content = "Foi adicionado ${playlist.tracks.size} da playlist ${playlist.name}."
                    }
                }
            }

            override fun noMatches() {
                coroutineScope.launch {
                    message.edit {
                        content = "Não encontrei nenhum resultado para: `$search`."
                    }
                }
            }

            override fun loadFailed(exception: FriendlyException?) {
                coroutineScope.launch {
                    message.edit {
                        content = "Houve uma falha ao buscar por sua música."
                    }
                }
            }

        })
    }

}