package site.remlit.blueb.residential.listener

import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.event.TownCreationEvent
import site.remlit.blueb.residential.service.TownService
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import site.remlit.blueb.residential.model.FireworkType
import site.remlit.blueb.residential.util.FireworkUtil

class TownCreationListener : Listener {
    @EventHandler
    fun onTownCreation(event: TownCreationEvent) {
        val town = TownService.get(event.town)
        if (town == null) return

        val player = Residential.instance.server.getPlayer(town.founder)
        if (player == null) return

        Residential.instance.server.sendMessage(
            MiniMessage.miniMessage().deserialize("<yellow>The town of <gold>${town.name}</gold> has been founded by <gold>${player.name}</gold>!")
        )
        FireworkUtil.spawnAt(player.location, FireworkType.NEW_TOWN)
    }
}