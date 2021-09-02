package com.github.zmigueel.radinho.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import dev.kord.core.behavior.channel.MessageChannelBehavior

data class Player(val audioPlayer: AudioPlayer, val channel: MessageChannelBehavior): AudioPlayer by audioPlayer {
    var scheduler: TrackScheduler = TrackScheduler(this)

    init {
        addListener(this.scheduler)
    }

    override fun destroy() {
        removeListener(scheduler)
        scheduler.queue.clear()

        stopTrack()
    }

}