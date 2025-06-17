package me.blueb.residential.services

import me.blueb.residential.ResidentialConfig
import me.blueb.residential.ResidentialDatabase
import me.blueb.residential.events.ResidentialChunkClaimEvent
import me.blueb.residential.models.Chunk
import me.blueb.residential.models.GracefulCommandException
import java.util.UUID

class ChunkService {
    companion object {
        fun get(location: String, world: String): Chunk? {
            val connection = ResidentialDatabase.connection

            connection.prepareStatement("SELECT * FROM chunk WHERE location = ? AND world = ?").use { stmt ->
                stmt.setString(1, location)
                stmt.setString(2, world)
                stmt.executeQuery().use { rs ->
                    return Chunk.fromRs(rs)
                }
            }
        }

        /* Town */

        fun claim(town: UUID, location: String, world: String): Chunk? {
            if (!ResidentialConfig.config.worlds.contains(world))
                throw GracefulCommandException("You cannot claim chunks in this world.")

            val chunk = get(location, world)

            if (chunk != null)
                throw GracefulCommandException("Chunk already claimed.")

            val connection = ResidentialDatabase.connection

            connection.prepareStatement("INSERT INTO chunk (location, world, town) VALUES (?, ?, ?)").use { stmt ->
                stmt.setString(1, location)
                stmt.setString(2, world)
                stmt.setString(3, town.toString())
                stmt.execute()
            }

            println("Chunk $location in $world claimed by town $town")

            val newChunk = get(location, world)!!

            ResidentialChunkClaimEvent(newChunk).callEvent()

            return newChunk
        }

        fun unclaim(location: String, world: String) { }

        /* Plot */

        fun createPlot(town: UUID, location: String, world: String) { }

        fun deletePlot(
            location: String, world: String) { }
    }
}