package me.blueb.residential

import me.blueb.residential.listeners.PlayerJoinListener

class ResidentialListeners {
    companion object {
        fun register(instance: Residential) {
            instance.server.pluginManager.registerEvents(PlayerJoinListener(), instance)
        }
    }
}