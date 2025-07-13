package site.remlit.blueb.residential.model

import site.remlit.blueb.residential.util.DatabaseUtil
import java.sql.ResultSet
import java.util.UUID

data class Chunk(
    val location: String,
    val town: UUID?,
    val plot: UUID?,
) {
    companion object {
        fun fromRs(rs: ResultSet): Chunk? {
            while (rs.next()) {
                return Chunk(
                    location = rs.getString("location"),
                    plot = DatabaseUtil.extractNullable<UUID>(rs, "plot"),
                    town = DatabaseUtil.extractNullable<UUID>(rs, "town"),
                )
            }
            return null
        }

        fun manyFromRs(rs: ResultSet): List<Chunk> {
            val list = mutableListOf<Chunk>()
            while (rs.next()) {
                list.add(Chunk(
                    location = rs.getString("location"),
                    plot = DatabaseUtil.extractNullable<UUID>(rs, "plot"),
                    town = DatabaseUtil.extractNullable<UUID>(rs, "town"),
                ))
            }
            return list
        }
    }
}
