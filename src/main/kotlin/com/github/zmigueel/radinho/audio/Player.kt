package com.github.zmigueel.radinho.audio

import com.github.zmigueel.radinho.coroutineScope
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.channel.TextChannel
import dev.kord.voice.VoiceConnection
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class Player(val audioPlayer: AudioPlayer, val channel: MessageChannelBehavior): AudioPlayer by audioPlayer {
    var scheduler: TrackScheduler = TrackScheduler(this)

    var data: ByteArray? = null
    var leaveTask: Job? = null
    var connection: VoiceConnection? = null

    init {
        addListener(this.scheduler)
    }

    override fun destroy() {
        removeListener(scheduler)
        scheduler.queue.clear()

        coroutineScope.launch {
            connection?.disconnect()
            val textChannel = this@Player.channel.asChannel() as TextChannel

            players.remove(textChannel.guild)
        }

        stopTrack()
        leaveTask?.cancel()
    }

}