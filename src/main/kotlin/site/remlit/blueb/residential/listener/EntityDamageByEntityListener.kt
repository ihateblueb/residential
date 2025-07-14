package site.remlit.blueb.residential.listener

import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.persistence.PersistentDataType
import site.remlit.blueb.residential.util.FireworkUtil

class EntityDamageByEntityListener : Listener {
    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.entity !is Player) return
        if (event.damager !is Firework) return

        if (event.damager.persistentDataContainer.get(FireworkUtil.cosmeticFireworkKey, PersistentDataType.BOOLEAN) ?: false) {
            event.isCancelled = true
        }
    }
}