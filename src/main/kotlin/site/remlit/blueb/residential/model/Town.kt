package site.remlit.blueb.residential.model

import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.DatabaseUtil
import site.remlit.blueb.residential.util.UuidUtil
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

    val open: Boolean,
    val pvp: Boolean,
    val mobs: Boolean,
    val fire: Boolean,

    val balance: Double,

    val tax: Double,
    val taxPercent: Boolean,
    val taxDebt: Boolean,
    val taxFeeMultiplier: Double,
    val taxMaxLate: Int,
) {
    fun getMayor() = TownService.getMayor(uuid)
    fun getMaxChunks() = Configuration.config.town.claimableChunks.initial

    companion object {
        fun fromRs(rs: ResultSet): Town? {
            while (rs.next()) {
                return Town(
                    uuid = UUID.fromString(rs.getString("uuid")),
                    name = rs.getString("name"),
                    tag = DatabaseUtil.extractNullable<String>(rs, "tag"),
                    founder = UUID.fromString(rs.getString("founder")),
                    foundedAt = DatabaseUtil.extractLocalDateTime(rs.getString("foundedAt"))!!,
                    abandoned = rs.getBoolean("abandoned"),
                    nation = DatabaseUtil.extractNullable<UUID>(rs, "nation"),
                    homeChunk = rs.getString("homeChunk"),
                    spawn = rs.getString("spawn"),

                    open = rs.getBoolean("open"),
                    pvp = rs.getBoolean("pvp"),
                    mobs = rs.getBoolean("mobs"),
                    fire = rs.getBoolean("fire"),

                    balance = rs.getDouble("balance"),

                    tax = DatabaseUtil.extractNullable<Double>(rs, "tax") ?: Configuration.config.town.tax.resident.cost,
                    taxPercent = DatabaseUtil.extractNullable<Boolean>(rs, "taxPercent") ?: Configuration.config.town.tax.resident.percent,
                    taxDebt = DatabaseUtil.extractNullable<Boolean>(rs, "taxDebt") ?: Configuration.config.town.tax.resident.debt,
                    taxFeeMultiplier = DatabaseUtil.extractNullable<Double>(rs, "taxFeeMultiplier") ?: Configuration.config.town.tax.resident.feeMultiplier,
                    taxMaxLate = DatabaseUtil.extractNullable<Int>(rs, "taxMaxLate") ?: Configuration.config.town.tax.resident.maxLate,
                )
            }
            return null
        }
    }
}