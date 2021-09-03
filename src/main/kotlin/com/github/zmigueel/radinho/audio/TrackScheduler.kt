package com.github.zmigueel.radinho.audio

import com.github.zmigueel.radinho.coroutineScope
import com.github.zmigueel.radinho.util.getGuildEmoji
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull.content
import java.util.*

class TrackScheduler(val player: Player): AudioEventAdapter() {

    val queue = LinkedList<AudioTrack>()

    var nowPlayingMessage: Message? = null

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
        coroutineScope.launch {
            nowPlayingMessage = this@TrackScheduler.player.channel.createMessage {
                content = "${getGuildEmoji("disco")} | Tocando agora: `${track?.info?.title}`."
            }
        }
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        coroutineScope.launch {
            nextTrack()
        }
    }

}