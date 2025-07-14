package site.remlit.blueb.residential.util

import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.model.FireworkType

class FireworkUtil {
    companion object {
        val cosmeticFireworkKey = NamespacedKey(Residential.instance, "firework.cosmetic")

        fun spawnAt(location: Location, type: FireworkType) {
            val firework = Residential.instance.server.getWorld(location.world.uid)?.spawnEntity(location, EntityType.FIREWORK) as? Firework
            if (firework == null) throw Exception("Firework is null")

            firework.persistentDataContainer.set(cosmeticFireworkKey, PersistentDataType.BOOLEAN, true)

            when (type) {
                FireworkType.NEW_TOWN -> firework.fireworkMeta =
                    firework.fireworkMeta.apply {
                        this.power = 1
                        this.addEffect(
                            FireworkEffect.builder()
                                .withColor(Color.fromRGB(85, 79, 255))
                                .withColor(Color.fromRGB(77, 255, 88))
                                .with(FireworkEffect.Type.BALL_LARGE)
                                .withFlicker()
                                .withTrail()
                                .build()
                        )
                    }

                FireworkType.NEW_DAY -> firework.fireworkMeta =
                    firework.fireworkMeta.apply {
                        this.power = 1
                        this.addEffect(
                            FireworkEffect.builder()
                                .withColor(Color.fromRGB(255, 172, 94))
                                .withColor(Color.fromRGB(255, 124, 0))
                                .with(FireworkEffect.Type.BURST)
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