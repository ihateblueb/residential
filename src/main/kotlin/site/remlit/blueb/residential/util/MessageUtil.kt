package site.remlit.blueb.residential.util

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.ceil

class MessageUtil {
    companion object {
        fun send(recipient: CommandSender, key: String) {
            /*val tagResolver =
                TagResolver.resolver(placeholders.entries.map {
                    Placeholder.component(it.key, Component.text(it.value))
                })*/
            recipient.sendMessage {
                MiniMessage.miniMessage().deserialize(key)
            }
        }

        const val LINE_LENGTH = "------------------------------------------------------".length

        fun createLine(
            center: String = "",
            padding: String = " ",
            line: String = "-",
            endCap: String = "",
            outerPadding: Int = 1,
            innerPadding: Int = 2
        ): String {
            // rounded because you cant have half a character, rather too short than overflow
            val halfLineLength = (LINE_LENGTH / 2) - ceil((center.length / 2).toDouble()).toInt() - (endCap.length * 2) - innerPadding - outerPadding

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