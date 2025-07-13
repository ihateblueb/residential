package site.remlit.blueb.residential.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.event.TownBankDepositEvent
import site.remlit.blueb.residential.service.TownService

class TownBankDepositListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: TownBankDepositEvent) {
        val town = TownService.get(event.town)
        if (town == null) return

        TownService.broadcastToResidents(town.uuid, "<yellow><gold>${Residential.economy.format(event.amount)}</gold> deposited into the town bank.")
    }
}