package site.remlit.blueb.residential.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import site.remlit.blueb.residential.Logger
import site.remlit.blueb.residential.event.NewDayEvent
import site.remlit.blueb.residential.model.FireworkType
import site.remlit.blueb.residential.service.ResidentService
import site.remlit.blueb.residential.service.TaxService
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.ChunkUtil
import site.remlit.blueb.residential.util.FireworkUtil
import site.remlit.blueb.residential.util.LocationUtil

class NewDayListener : Listener {
    @EventHandler
    fun onNewDay(event: NewDayEvent) {
        val towns = TownService.getAll()
        val residents = ResidentService.getAll()

        Logger.info("Resident tax")
        for (resident in residents) {
            val owed = TaxService.resident.calculateTax(resident.uuid)
            println("${resident.getPlayer()!!.name} owes $owed")
        }

        Logger.info("Town tax")
        for (town in towns) {
            val owed = TaxService.town.calculateTax(town.uuid)
            println("${town.name} owes $owed")

            val location = LocationUtil.stringToLocation(town.spawn, ChunkUtil.stringToChunk(town.homeChunk)!!.world.name)
            FireworkUtil.spawnAt(
                location,
                FireworkType.NEW_DAY
            )
        }
    }
}