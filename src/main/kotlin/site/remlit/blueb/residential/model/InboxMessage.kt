package site.remlit.blueb.residential.model

import java.sql.ResultSet
import java.util.UUID

data class InboxMessage(
    val uuid: UUID,
    val player: UUID,
    val from: String,
    val message: String
) {
    companion object {
        fun fromRs(rs: ResultSet): InboxMessage? {
            while (rs.next()) {
                return InboxMessage(
                    uuid = UUID.fromString(rs.getString("uuid")),
                    player = UUID.fromString(rs.getString("player")),
                    from = rs.getString("from"),
                    message = rs.getString("message")
                )
            }
            return null
        }

        fun manyFromRs(rs: ResultSet): List<InboxMessage> {
            val list = mutableListOf<InboxMessage>()
            while (rs.next()) {
                list.add(
                    InboxMessage(
                        uuid = UUID.fromString(rs.getString("uuid")),
                        player = UUID.fromString(rs.getString("player")),
                        from = rs.getString("from"),
                        message = rs.getString("message")
                    )
                )
            }
            return list
        }
    }
}
