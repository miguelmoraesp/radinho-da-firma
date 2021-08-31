package com.github.zmigueel.radinho.command

import com.github.zmigueel.radinho.command.impl.playCommand
import com.github.zmigueel.radinho.coroutineScope
import com.github.zmigueel.radinho.jda
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.util.concurrent.ConcurrentHashMap

val slashCommands = ConcurrentHashMap<String, CustomCommand>()

suspend fun command(
    name: String, description: String,
    data: CommandData.() -> Unit,
    action: suspend SlashCommandEvent.() -> Unit
) {
    val command = CustomCommand(
        CommandData(name, description).also(data),
        action
    )
    slashCommands[name] = command

    jda.upsertCommand(command.data).queue()
}

suspend fun command(
    name: String, description: String,
    action: suspend SlashCommandEvent.() -> Unit
) {
    val command = CustomCommand(
        CommandData(name, description),
        action
    )
    slashCommands[name] = command

    jda.upsertCommand(command.data).queue()
}

suspend fun loadCommands() {
    playCommand()
}

object CommandListener: ListenerAdapter() {
    override fun onSlashCommand(event: SlashCommandEvent) {
        if (event.guild == null) return

        val command = slashCommands[event.name] ?: return

        coroutineScope.launch {
            command.action.invoke(event)
        }
    }
}