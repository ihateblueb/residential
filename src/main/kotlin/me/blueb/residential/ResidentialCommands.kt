package me.blueb.residential

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import me.blueb.residential.commands.AdminCommand
import me.blueb.residential.commands.TownCommand

@Suppress("UnstableApiUsage", "SameReturnValue")
class ResidentialCommands {
    init { throw AssertionError("This class is not intended to be initialized.") }
    companion object {
        val admin = AdminCommand.create()
        val town = TownCommand.create()

        val root: LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("residential")
            .executes { ctx ->
                println("Root command")
                return@executes Command.SINGLE_SUCCESS
            }
            .then(admin)
            .then(town)

        fun register() {
            Residential.instance.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { commands ->
                commands.registrar().register(admin.build(), listOf("resadmin", "rda"))
                commands.registrar().register(town.build(), listOf("town", "rdt", "t"))
                commands.registrar().register(root.build(), listOf("residential", "res", "rd"))
            }
        }
    }
}