package site.remlit.blueb.residential.listener

import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.service.ChunkService
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.ChunkUtil
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveListener : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (event.from.chunk == event.to.chunk) return
        if (!Configuration.config.worlds!!.contains(event.to.world.name)) return

        val fromChunk = ChunkService.get(ChunkUtil.chunkToString(event.from.chunk), event.from.world.name)
        val toChunk = ChunkService.get(ChunkUtil.chunkToString(event.to.chunk), event.to.world.name)
        val toTown = if (toChunk?.town != null) TownService.get(toChunk.town) else null

        if (fromChunk == null && toChunk != null && toTown != null) {
            event.player.sendActionBar(MiniMessage.miniMessage().deserialize("<gold>${toTown.name}"))
        } else if (fromChunk != null && toChunk == null) {
            event.player.sendActionBar(MiniMessage.miniMessage().deserialize("<dark_green>Wilderness"))
        }
    }
}