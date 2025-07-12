package site.remlit.blueb.residential.event

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.jetbrains.annotations.ApiStatus
import java.util.*

@ApiStatus.OverrideOnly
open class TownEvent(val town: UUID) : Event(), Cancellable {
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