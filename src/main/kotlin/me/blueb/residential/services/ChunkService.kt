package me.blueb.residential.services

import me.blueb.residential.ResidentialDatabase
import me.blueb.residential.models.Chunk
import java.util.UUID

class ChunkService {
    companion object {
        fun get(location: String): Chunk? {
            val connection = ResidentialDatabase.connection

            connection.prepareStatement("SELECT * FROM chunk WHERE location = ?").use { stmt ->
                stmt.setString(1, location)
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        val rsPlot = rs.getString("plot")
                        val plot = if (!rs.wasNull())
                            UUID.fromString(rsPlot)
                        else null

                        val rsTown = rs.getString("town")
                        val town = if (!rs.wasNull())
                            UUID.fromString(rsTown)
                        else null

                        val chunk = Chunk(
                            location = rs.getString("location"),
                            plot = plot,
                            town = town,
                        )
                        println("Fetched chunk ${chunk.location}")
                        return@get chunk
                    }
                }
            }

            println("Chunk not registered")
            return null
        }

        /* Town */

        fun claim(town: UUID) { }

        fun unclaim(location: String) { }

        /* Plot */

        fun createPlot(town: UUID) { }

        fun deletePlot(location: String) { }
    }
}