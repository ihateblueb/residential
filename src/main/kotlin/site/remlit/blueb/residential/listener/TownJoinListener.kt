package site.remlit.blueb.residential.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import site.remlit.blueb.residential.event.TownJoinEvent
import site.remlit.blueb.residential.service.ResidentService
import site.remlit.blueb.residential.service.TownService

class TownJoinListener : Listener {
    @EventHandler
    fun onTownJoin(event: TownJoinEvent) {
        val resident = ResidentService.get(event.resident)
        if (resident == null) return

        val town = TownService.get(event.town)
        if (town == null) return

        if (resident.uuid == town.founder) return

        TownService.broadcastToResidents(town.uuid, "<yellow><gold>${resident.getPlayer().name}</gold> joined the towns.")
    }
}