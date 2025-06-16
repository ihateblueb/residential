package me.blueb.residential

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import me.blueb.residential.commands.DebugCommand
import me.blueb.residential.commands.TownCommand

@Suppress("UnstableApiUsage")
class ResidentialCommands {
    init { throw AssertionError("This class is not intended to be initialized.") }
    companion object {
        val root: LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("residential")
            .executes { ctx ->
                println("Root command")
                return@executes Command.SINGLE_SUCCESS
            }
            .then(DebugCommand.create())

        val town = TownCommand.create()

        fun register(instance: Residential) {
            instance.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { commands ->
                commands.registrar().register(root.build(), listOf("residential", "res", "rd"))
                commands.registrar().register(town.build(), listOf("town", "t"))
            }
        }
    }
}