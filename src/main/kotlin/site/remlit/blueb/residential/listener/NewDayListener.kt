package site.remlit.blueb.residential.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import site.remlit.blueb.residential.event.NewDayEvent
import site.remlit.blueb.residential.model.FireworkType
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.ChunkUtil
import site.remlit.blueb.residential.util.FireworkUtil
import site.remlit.blueb.residential.util.LocationUtil

class NewDayListener : Listener {
    @EventHandler
    fun onNewDay(event: NewDayEvent) {
        val towns = TownService.getAll()

        for (town in towns) {
            val location = LocationUtil.stringToLocation(town.spawn, ChunkUtil.stringToChunk(town.homeChunk)!!.world.name)
            FireworkUtil.spawnAt(
                location,
                FireworkType.NEW_DAY
            )
        }
    }
}