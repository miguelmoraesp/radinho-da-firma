package com.github.zmigueel.radinho.command

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.entity.interaction.CommandInteraction

data class Command(
    val name: String,
    val description: String,
    val action: suspend ChatInputCommandInteraction.() -> Unit
)
