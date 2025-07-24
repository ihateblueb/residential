package site.remlit.blueb.residential.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import site.remlit.blueb.residential.model.InboxMessage

class InboxMessageEvent(
    val message: InboxMessage,
) : Event() {
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