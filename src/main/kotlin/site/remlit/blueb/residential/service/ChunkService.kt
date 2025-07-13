package site.remlit.blueb.residential.service

import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.Database
import site.remlit.blueb.residential.event.ChunkClaimEvent
import site.remlit.blueb.residential.model.Chunk
import site.remlit.blueb.residential.model.GracefulCommandException
import java.util.UUID
import kotlin.use

class ChunkService {
    companion object {
        fun get(location: String): Chunk? {
            val connection = Database.connection

            connection.prepareStatement("SELECT * FROM chunk WHERE location = ?").use { stmt ->
                stmt.setString(1, location)
                stmt.executeQuery().use { rs ->
                    return Chunk.fromRs(rs)
                }
            }
        }

        /* Town */

        fun getAllClaimedChunks(town: UUID): List<Chunk> {
            Database.connection.prepareStatement("SELECT * FROM chunk WHERE town = ?").use { stmt ->
                stmt.setString(1, town.toString())
                stmt.executeQuery().use { rs ->
                    return Chunk.manyFromRs(rs)
                }
            }
        }

        fun claim(town: UUID, location: String): Chunk? {
            val town = TownService.get(town)!!
            val world = location.split(",")[2]

            if (!Configuration.config.worlds.contains(world))
                throw GracefulCommandException("You cannot claim chunks in this world.")

            val chunk = get(location)

            if (chunk != null)
                throw GracefulCommandException("Chunk already claimed.")

            val allClaimedChunks = getAllClaimedChunks(town.uuid)
            if (allClaimedChunks.size >= town.getMaxChunks() || allClaimedChunks.size >= Configuration.config.town.claimableChunks.max)
                throw GracefulCommandException("Your town has reached the limit of chunks it can claim.")

            val connection = Database.connection

            connection.prepareStatement("INSERT INTO chunk (location, town) VALUES (?, ?)").use { stmt ->
                stmt.setString(1, location)
                stmt.setString(2, town.uuid.toString())
                stmt.execute()
            }

            println("Chunk $location claimed by ${town.name}")

            val newChunk = get(location)!!

            ChunkClaimEvent(newChunk)

            return newChunk
        }

        fun unclaim(location: String) {}

        /* Plot */

        fun createPlot(town: UUID, location: String) {}

        fun deletePlot(location: String) {}
    }
}