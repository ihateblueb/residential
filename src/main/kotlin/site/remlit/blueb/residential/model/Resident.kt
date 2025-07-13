package site.remlit.blueb.residential.model

import org.bukkit.entity.Player
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.service.TownRoleService
import site.remlit.blueb.residential.util.DatabaseUtil
import java.sql.ResultSet
import java.util.UUID

data class Resident(
    val uuid: UUID,
    val trusted: List<UUID?>,
    val town: UUID?,
    val roles: List<UUID?>,
) {
    fun getPlayer(): Player = Residential.instance.server.getPlayer(uuid) ?: throw Exception("Resident couldn't be found")
    fun getTownRoles(): List<TownRole> {
        val townRoles = mutableListOf<TownRole>()
        if (town == null) return townRoles
        for (role in roles) {
            if (role != null) TownRoleService.get(town, role).also { if (it != null) townRoles.add(it) }
        }
        return townRoles
    }

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

        fun manyFromRs(rs: ResultSet): List<Resident> {
            val list = mutableListOf<Resident>()
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

                list.add(
                    Resident(
                        uuid = UUID.fromString(rs.getString("uuid")),
                        trusted = trusted,
                        town = town,
                        roles = roles
                    )
                )
            }
            return list.toList()
        }
    }
}
