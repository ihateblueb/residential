package me.blueb.residential

import me.blueb.residential.listeners.PlayerJoinListener
import me.blueb.residential.listeners.ResidentialTownCreationListener

class ResidentialListeners {
    init { throw AssertionError("This class is not intended to be initialized.") }
    companion object {
        fun register() {
            Residential.instance.server.pluginManager.registerEvents(PlayerJoinListener(), Residential.instance)

            Residential.instance.server.pluginManager.registerEvents(ResidentialTownCreationListener(), Residential.instance)
        }
    }
}