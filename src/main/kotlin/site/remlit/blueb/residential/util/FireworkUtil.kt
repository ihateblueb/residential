package site.remlit.blueb.residential.util

import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.model.FireworkType

class FireworkUtil {
    companion object {
        fun spawnAt(player: Player, type: FireworkType) {
            val firework = Residential.instance.server.getWorld(player.world.uid)?.spawnEntity(player.location, EntityType.FIREWORK) as? Firework
            if (firework == null) throw Exception("Firework is null")

            when (type) {
                FireworkType.NEW_TOWN -> {
                    firework.fireworkMeta
                        .addEffect(
                            FireworkEffect.builder()
                                .withColor(Color.fromRGB(85, 79, 255))
                                .withColor(Color.fromRGB(77, 255, 88))
                                .withFlicker()
                                .withTrail()
                                .build()
                        )
                }
            }

            firework.detonate()
        }
    }
}