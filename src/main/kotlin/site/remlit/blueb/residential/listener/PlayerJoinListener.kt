package site.remlit.blueb.residential.listener

import site.remlit.blueb.residential.service.ResidentService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import site.remlit.blueb.residential.service.InboxService
import site.remlit.blueb.residential.util.MessageUtil
import site.remlit.blueb.residential.util.inline.scheduled

class PlayerJoinListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        val resident = ResidentService.get(player.uniqueId)

        if (resident == null) {
            ResidentService.register(player.uniqueId)
        }

        val messages = InboxService.getInbox(player.uniqueId)

        // post-join
        scheduled(1) {
            MessageUtil.send(player, "<yellow>You have <gold>${messages.size}</gold> messages in your inbox.")
        }
    }
}