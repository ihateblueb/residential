package site.remlit.blueb.residential.util

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class MessageUtil {
    companion object {
        fun send(player: Player, message: String) =
            player.sendMessage {
                MiniMessage.miniMessage().deserialize(message)
            }

        const val LINE_LENGTH = "--------------------------------------------------------------".length

        fun createLine(
            center: String = "",
            padding: String = " ",
            line: String = "-",
            endCap: String = "",
            outerPadding: Int = 1,
            innerPadding: Int = 2
        ): String {
            val halfLineLength = (LINE_LENGTH / 2) - (center.length / 2) - (endCap.length * 2) - innerPadding - outerPadding

            var string = ""

            string += "<gray>"
            repeat(outerPadding) { string += padding }
            string += endCap
            repeat(halfLineLength) { string += line }
            string += endCap
            repeat(innerPadding) { string += padding }
            string += "</gray>"

            string += "<white>"
            string += center
            string += "</white>"

            string += "<gray>"
            repeat(innerPadding) { string += padding }
            string += endCap
            repeat(halfLineLength) { string += line }
            string += endCap
            repeat(outerPadding) { string += padding }
            string += "</gray>"

            return string
        }

        fun formatLocalDateTime(localDateTime: LocalDateTime, includeTime: Boolean = true): String {
            var string = "${localDateTime.dayOfMonth} ${localDateTime.month.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())} ${localDateTime.year}"
            if (includeTime) string += " at ${localDateTime.hour}:${localDateTime.minute}"
            return string
        }
    }
}