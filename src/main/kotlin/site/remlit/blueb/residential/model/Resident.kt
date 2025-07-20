package site.remlit.blueb.residential.model

import org.bukkit.entity.Player
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.service.TownRoleService
import site.remlit.blueb.residential.service.TownService
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
    fun getTown(): Town? = if (town != null) TownService.get(town) else null
    fun getBalance(): Double = Residential.economy.getBalance(getPlayer())
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
                return Resident(
                    uuid = UUID.fromString(rs.getString("uuid")),
                    trusted = DatabaseUtil.extractNullable<List<UUID>>(rs, "trusted") { DatabaseUtil.extractUuid(it) }
                        ?: listOf(),
                    town = DatabaseUtil.extractNullable<UUID>(rs, "town"),
                    roles = DatabaseUtil.extractNullable<List<UUID>>(rs, "roles") { DatabaseUtil.extractUuid(it) }
                        ?: listOf()
                )
            }
            return null
        }

        fun manyFromRs(rs: ResultSet): List<Resident> {
            val list = mutableListOf<Resident>()
            while (rs.next()) {
                list.add(
                    Resident(
                        uuid = UUID.fromString(rs.getString("uuid")),
                        trusted = DatabaseUtil.extractNullable<List<UUID>>(rs, "trusted") { DatabaseUtil.extractUuid(it) }
                            ?: listOf(),
                        town = DatabaseUtil.extractNullable<UUID>(rs, "town"),
                        roles = DatabaseUtil.extractNullable<List<UUID>>(rs, "roles") { DatabaseUtil.extractUuid(it) }
                            ?: listOf()
                    )
                )
            }
            return list.toList()
        }
    }
}
