package me.blueb.residential.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.jetbrains.annotations.ApiStatus
import java.util.UUID

@ApiStatus.OverrideOnly
open class TownEvent : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    private var town: UUID
        get() = this.town
        set(town) { this.town = town }

    companion object {
        val handlerList: HandlerList = HandlerList()
    }
}