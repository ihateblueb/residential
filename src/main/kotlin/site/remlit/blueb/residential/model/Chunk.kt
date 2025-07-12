package site.remlit.blueb.residential.model

import java.sql.ResultSet
import java.util.UUID

data class Chunk(
    val location: String,
    val world: String,
    val town: UUID?,
    val plot: UUID?,
) {
    companion object {
        fun fromRs(rs: ResultSet): Chunk? {
            while (rs.next()) {
                val rsPlot = rs.getString("plot")
                val plot = if (!rs.wasNull())
                    UUID.fromString(rsPlot)
                else null

                val rsTown = rs.getString("town")
                val town = if (!rs.wasNull())
                    UUID.fromString(rsTown)
                else null

                return Chunk(
                    location = rs.getString("location"),
                    world = rs.getString("world"),
                    plot = plot,
                    town = town,
                )
            }
            return null
        }

        fun manyFromRs(rs: ResultSet): List<Chunk> {
            val list = mutableListOf<Chunk>()
            while (rs.next()) {
                val rsPlot = rs.getString("plot")
                val plot = if (!rs.wasNull())
                    UUID.fromString(rsPlot)
                else null

                val rsTown = rs.getString("town")
                val town = if (!rs.wasNull())
                    UUID.fromString(rsTown)
                else null

                list.add(Chunk(
                    location = rs.getString("location"),
                    world = rs.getString("world"),
                    plot = plot,
                    town = town,
                ))
            }
            return list
        }
    }
}
