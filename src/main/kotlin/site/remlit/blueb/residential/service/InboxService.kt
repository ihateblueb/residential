package site.remlit.blueb.residential.service

import site.remlit.blueb.residential.Database
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.model.InboxMessage
import site.remlit.blueb.residential.util.MessageUtil
import java.util.UUID
import kotlin.use

class InboxService {
    companion object {
        fun getInbox(player: UUID): List<InboxMessage> {
            Database.connection.prepareStatement("SELECT * FROM inbox_message WHERE player = ?").use { stmt ->
                stmt.setString(2, player.toString())
                stmt.executeQuery().use { rs ->
                    return InboxMessage.manyFromRs(rs)
                }
            }
        }

        fun send(player: UUID, from: String, message: String) {
            Database.connection.prepareStatement("INSERT INTO inbox_message (uuid, player, from, message) VALUES (?, ?, ?, ?)").use { stmt ->
                stmt.setString(1, UUID.randomUUID().toString())
                stmt.setString(2, player.toString())
                stmt.setString(3, from)
                stmt.setString(4, message)
                stmt.execute()
            }

            val targetPlayer = Residential.instance.server.getPlayer(player)
            if (targetPlayer == null) return

            if (targetPlayer.isOnline)
                MessageUtil.send(targetPlayer, "<yellow>You have a new message from <gold>$from</gold>.")
        }

        fun sendFromSystem(player: UUID, message: String) = send(player, "System", message)

        fun delete(player: UUID, message: UUID) {
            Database.connection.prepareStatement("DELETE FROM inbox_message WHERE player = ? AND uuid = ?").use { stmt ->
                stmt.setString(1, player.toString())
                stmt.setString(2, message.toString())
                stmt.execute()
            }}

        fun clear(player: UUID) {
            Database.connection.prepareStatement("DELETE FROM inbox_message WHERE player = ?").use { stmt ->
                stmt.setString(1, player.toString())
                stmt.execute()
            }
        }
    }
}