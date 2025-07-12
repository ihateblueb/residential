package site.remlit.blueb.residential

import co.aikar.commands.PaperCommandManager
import site.remlit.blueb.residential.command.ResidentialCommand
import site.remlit.blueb.residential.command.town.TownCommand
import site.remlit.blueb.residential.command.town.TownSetCommand
import site.remlit.blueb.residential.command.town.TownWarpCommand

class Commands {
    init { throw AssertionError("This class is not intended to be initialized.") }
    companion object {
        val commandManager = PaperCommandManager(Residential.instance)

        fun register() {
            Residential.commandManager = commandManager

            commandManager.registerCommand(ResidentialCommand())

            commandManager.registerCommand(TownCommand())
            commandManager.registerCommand(TownSetCommand())
            commandManager.registerCommand(TownWarpCommand())
        }
    }
}