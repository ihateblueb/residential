package site.remlit.blueb.residential.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import site.remlit.blueb.residential.event.NewDayEvent
import site.remlit.blueb.residential.service.TaxService

class NewDayListener : Listener {
    @EventHandler
    fun onNewDay(event: NewDayEvent) {
        TaxService.collectTaxes()
    }
}