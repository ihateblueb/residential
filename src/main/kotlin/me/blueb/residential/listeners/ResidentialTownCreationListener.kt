package me.blueb.residential.listeners

import me.blueb.residential.Residential
import me.blueb.residential.events.ResidentialTownCreationEvent
import me.blueb.residential.services.TownService
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ResidentialTownCreationListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: ResidentialTownCreationEvent) {
        val town = TownService.get(event.town)
        if (town == null) return

        val player = Residential.instance.server.getPlayer(town.founder)
        if (player == null) return

        Residential.instance.server.sendMessage(
            MiniMessage.miniMessage().deserialize("<yellow>The town of <gold>${town.name}</gold> has been founded by <gold>${player.name}</gold>!")
        )
    }
}