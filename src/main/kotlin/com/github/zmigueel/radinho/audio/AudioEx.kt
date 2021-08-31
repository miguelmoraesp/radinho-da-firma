package com.github.zmigueel.radinho.audio

import com.github.zmigueel.radinho.lavaKord
import dev.schlaubi.lavakord.audio.player.Player
import dev.schlaubi.lavakord.audio.player.Track
import dev.schlaubi.lavakord.rest.TrackResponse
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import java.util.concurrent.ConcurrentLinkedQueue

var Player.channel: TextChannel?
    get() = null
    set(value) {}

val Player.queue
    get() = ConcurrentLinkedQueue<Track>()

val SlashCommandEvent.link
    get() = lavaKord.getLink(this.guild?.id!!)

val SlashCommandEvent.player
    get() = link.player

suspend fun Player.queue(track: TrackResponse.PartialTrack?) {
    if (playingTrack == null) {
        playTrack(track?.toTrack()!!)
        return
    }

    queue.offer(track?.toTrack()!!)
}

suspend fun Player.nextTrack() {
    val next = queue.poll() ?: return

    playTrack(next)
}