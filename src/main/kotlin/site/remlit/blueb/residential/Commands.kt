package site.remlit.blueb.residential

import co.aikar.commands.PaperCommandManager
import site.remlit.blueb.residential.command.*
import site.remlit.blueb.residential.command.condition.*
import site.remlit.blueb.residential.command.town.*

class Commands {
    init { throw AssertionError("This class is not intended to be initialized.") }
    companion object {
        val commandManager = PaperCommandManager(Residential.instance)

        fun register() {
            Residential.commandManager = commandManager

            PlayerConditions.register()
            TownConditions.register()

            commandManager.registerCommand(ResidentialCommand())
            commandManager.registerCommand(AdminCommand())
            commandManager.registerCommand(DebugCommand())
            commandManager.registerCommand(InboxCommand())

            commandManager.registerCommand(TownBankCommand())
            commandManager.registerCommand(TownBuyCommand())
            commandManager.registerCommand(TownCommand())
            commandManager.registerCommand(TownRoleCommand())
            commandManager.registerCommand(TownSetCommand())
            commandManager.registerCommand(TownWarpCommand())
        }
    }
}