package com.github.zmigueel.radinho.command.impl

import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.github.zmigueel.radinho.command.command
import com.github.zmigueel.radinho.config
import com.github.zmigueel.radinho.coroutineScope
import com.github.zmigueel.radinho.util.getGuildEmoji
import com.google.gson.Gson
import dev.kord.core.behavior.interaction.followUp
import dev.kord.core.behavior.interaction.followUpEphemeral
import dev.kord.rest.builder.message.create.actionRow
import kotlinx.coroutines.launch

data class Response(
    val code: String
)

suspend fun youtuberTogetherCommand() = command("youtube", "Cria uma sessão do youtube together!") {
    println("boa noite")
    val member = this.user.asMember(this.data.guildId.value!!)

    val voiceChannelId = member.getVoiceStateOrNull()?.channelId
    if (voiceChannelId == null) {
        this.acknowledgeEphemeral().followUpEphemeral {
            content = "${getGuildEmoji("error")} | Você precisa estar em um canal de voz."
        }
        return@command
    }

    "https://discord.com/api/v8/channels/${voiceChannelId.asString}/invites"
        .httpPost()
        .jsonBody("{\"max_age\": 86400, \"max_uses\": 0,\"target_application_id\": \"755600276941176913\",\"target_type\": 2,\"temporary\": false,\"validate\": null }")
        .header(
            mapOf(
                "Authorization" to "Bot ${config.discord.token}",
                "Content-Type" to "application/json"
            )
        ).responseString { request, response, result ->
            println(request)
            println(response)
            val invite = Gson().fromJson(result.get(), Response::class.java)
            coroutineScope.launch {
                this@command.acknowledgePublic().followUp {
                    content = "${getGuildEmoji("correct")} | Sessão criada, clique no botão abaixo."
                    actionRow {
                        linkButton("https://discord.com/invite/${invite.code}") {
                            label = "Clique aqui"
                        }
                    }
                }
            }
        }
}