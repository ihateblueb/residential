package site.remlit.blueb.residential.event

import site.remlit.blueb.residential.model.Chunk
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.jetbrains.annotations.ApiStatus

@ApiStatus.OverrideOnly
open class ChunkEvent(val chunk: Chunk?) : Event(), Cancellable {
    private var cancelled: Boolean = false
    override fun isCancelled(): Boolean = cancelled
    override fun setCancelled(cancel: Boolean) {
        cancelled = true
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }

    companion object {
        @JvmStatic
        @Suppress("Unused")
        fun getHandlerList(): HandlerList = HANDLER_LIST
        val HANDLER_LIST: HandlerList = HandlerList()
    }
}