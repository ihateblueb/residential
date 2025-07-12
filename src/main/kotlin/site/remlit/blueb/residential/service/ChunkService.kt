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
        fun get(location: String, world: String): Chunk? {
            val connection = Database.connection

            connection.prepareStatement("SELECT * FROM chunk WHERE location = ? AND world = ?").use { stmt ->
                stmt.setString(1, location)
                stmt.setString(2, world)
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

        fun claim(town: UUID, location: String, world: String): Chunk? {
            val town = TownService.get(town)!!

            if (!Configuration.config.worlds.contains(world))
                throw GracefulCommandException("You cannot claim chunks in this world.")

            val chunk = get(location, world)

            if (chunk != null)
                throw GracefulCommandException("Chunk already claimed.")

            val allClaimedChunks = getAllClaimedChunks(town.uuid)
            if (allClaimedChunks.size >= town.getMaxChunks() || allClaimedChunks.size >= Configuration.config.town.claimableChunks.max)
                throw GracefulCommandException("Your town has reached the limit of chunks it can claim.")

            val connection = Database.connection

            connection.prepareStatement("INSERT INTO chunk (location, world, town) VALUES (?, ?, ?)").use { stmt ->
                stmt.setString(1, location)
                stmt.setString(2, world)
                stmt.setString(3, town.uuid.toString())
                stmt.execute()
            }

            println("Chunk $location in $world claimed by ${town.name}")

            val newChunk = get(location, world)!!

            ChunkClaimEvent(newChunk).callEvent()

            return newChunk
        }

        fun unclaim(location: String, world: String) { }

        /* Plot */

        fun createPlot(town: UUID, location: String, world: String) { }

        fun deletePlot(
            location: String, world: String) { }
    }
}