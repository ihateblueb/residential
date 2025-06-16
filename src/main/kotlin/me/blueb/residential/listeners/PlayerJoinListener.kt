package me.blueb.residential.listeners

import me.blueb.residential.services.ResidentService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        val resident = ResidentService.get(player.uniqueId)

        if (resident == null) {
            ResidentService.register(player.uniqueId)
        }
    }
}