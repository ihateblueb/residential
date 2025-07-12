package site.remlit.blueb.residential.util

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player

class MessageUtil {
    companion object {
        fun send(player: Player, message: String) =
            player.sendMessage {
                MiniMessage.miniMessage().deserialize(message)
            }
    }
}