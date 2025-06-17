package me.blueb.residential.models

import me.blueb.residential.util.DatabaseUtil
import java.sql.ResultSet
import java.util.UUID

data class Resident(
    val uuid: UUID,
    val trusted: List<UUID?>,
    val town: UUID?,
    val roles: List<UUID?>,
) {
    companion object {
        fun fromRs(rs: ResultSet): Resident? {
            while (rs.next()) {
                val rsTrusted = rs.getString("trusted")
                val trusted = if (!rs.wasNull())
                    DatabaseUtil.extractList(rsTrusted) { DatabaseUtil.extractUuid(it) }
                else listOf()

                val rsTown = rs.getString("town")
                val town = if (!rs.wasNull())
                    DatabaseUtil.extractUuid(rsTown)
                else null

                val rsRoles = rs.getString("roles")
                val roles = if (!rs.wasNull())
                    DatabaseUtil.extractList(rsRoles) { DatabaseUtil.extractUuid(it) }
                else listOf()

                return Resident(
                    uuid = UUID.fromString(rs.getString("uuid")),
                    trusted = trusted,
                    town = town,
                    roles = roles
                )
            }
            return null
        }
    }
}
