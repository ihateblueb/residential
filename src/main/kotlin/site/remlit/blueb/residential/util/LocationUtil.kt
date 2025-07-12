package site.remlit.blueb.residential.util

import site.remlit.blueb.residential.Residential
import org.bukkit.Location

class LocationUtil {
    companion object {
        fun locationToString(location: Location): String {
            return "${location.x},${location.y},${location.z},${location.yaw},${location.pitch}"
        }
        fun stringToLocation(string: String, world: String): Location {
            val split = string.split(",")
            return Location(Residential.instance.server.getWorld(world), split[0].toDouble(), split[1].toDouble(), split[2].toDouble(), split[3].toFloat(), split[4].toFloat())
        }
    }
}