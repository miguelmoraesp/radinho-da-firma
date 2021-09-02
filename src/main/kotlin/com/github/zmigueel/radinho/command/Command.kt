package com.github.zmigueel.radinho.command

import dev.kord.core.entity.interaction.ChatInputCommandInteraction

data class Command(
    val name: String,
    val description: String,
    val action: suspend ChatInputCommandInteraction.() -> Unit
)
