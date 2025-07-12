package site.remlit.blueb.residential.model

import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.DatabaseUtil
import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.UUID

data class Town(
    val uuid: UUID,
    val name: String,
    val tag: String?,
    val founder: UUID,
    val foundedAt: LocalDateTime,
    val abandoned: Boolean,
    val nation: UUID?,
    val homeChunk: String,
    val spawn: String,
) {
    fun getMayor() = TownService.getMayor(uuid)
    fun getMaxChunks() = Configuration.config.town.claimableChunks.initial

    companion object {
        fun fromRs(rs: ResultSet): Town? {
            while (rs.next()) {
                val rsTag = rs.getString("tag")
                val tag = if (!rs.wasNull())
                    rsTag
                else null

                val rsNation = rs.getString("nation")
                val nation = if (!rs.wasNull())
                    UUID.fromString(rsNation)
                else null

                return Town(
                    uuid = UUID.fromString(rs.getString("uuid")),
                    name = rs.getString("name"),
                    tag = tag,
                    founder = UUID.fromString(rs.getString("founder")),
                    foundedAt = DatabaseUtil.extractLocalDateTime(rs.getString("foundedAt"))!!,
                    abandoned = rs.getBoolean("abandoned"),
                    nation = nation,
                    homeChunk = rs.getString("homeChunk"),
                    spawn = rs.getString("spawn"),
                )
            }
            return null
        }
    }
}