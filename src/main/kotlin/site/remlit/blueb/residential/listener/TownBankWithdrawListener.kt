package site.remlit.blueb.residential.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.event.TownBankWithdrawEvent
import site.remlit.blueb.residential.service.TownService

class TownBankWithdrawListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: TownBankWithdrawEvent) {
        val town = TownService.get(event.town)
        if (town == null) return

        TownService.broadcastToResidents(town.uuid, "<yellow><gold>${Residential.economy.format(event.amount)}</gold> withdrawn from the town bank.")
    }
}