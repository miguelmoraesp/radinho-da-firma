package com.github.zmigueel.radinho.command

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

data class CustomCommand(val data: CommandData, val action: suspend SlashCommandEvent.() -> Unit);