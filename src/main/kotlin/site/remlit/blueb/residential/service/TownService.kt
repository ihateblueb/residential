package site.remlit.blueb.residential.service

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.Database
import site.remlit.blueb.residential.event.TownCreationEvent
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.model.Resident
import site.remlit.blueb.residential.model.Town
import site.remlit.blueb.residential.util.ChunkUtil
import site.remlit.blueb.residential.util.LocationUtil
import site.remlit.blueb.residential.util.SoundUtil
import java.time.LocalDateTime
import java.util.UUID

class TownService {
    companion object {
        fun get(uuid: UUID): Town? {
            val connection = Database.connection

            connection.prepareStatement("SELECT * FROM town WHERE uuid = ?").use { stmt ->
                stmt.setString(1, uuid.toString())
                stmt.executeQuery().use { rs ->
                    return Town.fromRs(rs)
                }
            }
        }

        fun getByName(name: String): Town? {
            val connection = Database.connection

            connection.prepareStatement("SELECT * FROM town WHERE name = ?").use { stmt ->
                stmt.setString(1, name)
                stmt.executeQuery().use { rs ->
                    return Town.fromRs(rs)
                }
            }
        }

        // take, offset, sort by foundedAt
        fun getAll(): Nothing = TODO()

        fun getAllUuids(): List<UUID> {
            val connection = Database.connection

            connection.prepareStatement("SELECT * FROM town WHERE abandoned = false").use { stmt ->
                val list = mutableListOf<UUID>()
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        list.add(UUID.fromString(rs.getString("uuid")))
                    }
                }
                return list.toList()
            }
        }

        fun getMayor(town: UUID): Resident? {
            val role = TownRoleService.getByTypeMayor(town)!!
            return ResidentService.getByRole(role.uuid)
        }

        fun register(name: String, founder: UUID, homeChunk: String, world: String, spawn: String): Town {
            if (!Configuration.config.worlds.contains(world))
                throw GracefulCommandException("You cannot create towns in this world.")

            val resident = ResidentService.get(founder)

            if (resident == null)
                throw GracefulCommandException("Founder couldn't be found.")

            if (resident.town != null)
                throw GracefulCommandException("You're already apart of a town.")

            val existingTown = getByName(name)

            if (existingTown != null)
                throw GracefulCommandException("A town with this name already exists.")

            val chunk = ChunkService.get(homeChunk)

            if (chunk != null)
                throw GracefulCommandException("Chunk already claimed.")

            val uuid = UUID.randomUUID()

            Database.connection.prepareStatement("INSERT INTO town (uuid, name, founder, foundedAt, homeChunk, spawn) VALUES (?, ?, ?, ?, ?, ?)").use { stmt ->
                stmt.setString(1, uuid.toString())
                stmt.setString(2, name)
                stmt.setString(3, founder.toString())
                stmt.setString(4, LocalDateTime.now().toString())
                stmt.setString(5, homeChunk)
                stmt.setString(6, spawn)
                stmt.execute()
            }

            Residential.economy.withdrawPlayer(Residential.instance.server.getPlayer(founder), Configuration.config.town.cost.toDouble())

            TownCreationEvent(uuid).callEvent()

            ChunkService.claim(uuid, homeChunk)
            ResidentService.joinTown(founder, uuid)

            TownRoleService.createDefaults(uuid)
            val mayorRole = TownRoleService.getByTypeMayor(uuid)!!
            TownRoleService.giveRole(founder, mayorRole.uuid)

            return get(uuid)!!
        }

        fun teleport(town: UUID, player: UUID) {
            val foundTown = get(town)

            if (foundTown == null)
                throw GracefulCommandException("Town doesn't exist.")

            val player = Residential.instance.server.getPlayer(player)!!

            SoundUtil.playTeleport(player)
            player.teleport(LocationUtil.stringToLocation(foundTown.spawn, ChunkUtil.stringToChunk(foundTown.homeChunk)!!.world.name))
        }

        fun setSpawn(town: UUID, homeChunk: String, location: String) {
            val chunk = ChunkService.get(homeChunk)

            if (chunk == null || chunk.town != town)
                throw GracefulCommandException("This chunk isn't claimed by your town.")

            Database.connection.prepareStatement("UPDATE town SET homeChunk = ?, spawn = ? WHERE uuid = ?").use { stmt ->
                stmt.setString(1, homeChunk)
                stmt.setString(2, location)
                stmt.setString(3, town.toString())
                stmt.execute()
            }
        }
    }
}