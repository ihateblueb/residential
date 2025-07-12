package site.remlit.blueb.residential

import co.aikar.commands.PaperCommandManager
import site.remlit.blueb.residential.command.town.TownCommand
import site.remlit.blueb.residential.command.town.TownWarpCommand

class Commands {
    init { throw AssertionError("This class is not intended to be initialized.") }
    companion object {
        val commandManager = PaperCommandManager(Residential.instance)

        fun register() {
            Residential.commandManager = commandManager

            commandManager.registerCommand(TownCommand())
            commandManager.registerCommand(TownWarpCommand())
        }
    }
}