package me.blueb.residential.services

import me.blueb.residential.Residential
import me.blueb.residential.ResidentialConfig
import me.blueb.residential.ResidentialDatabase
import me.blueb.residential.events.ResidentialTownCreationEvent
import me.blueb.residential.models.GracefulCommandException
import me.blueb.residential.models.Town
import me.blueb.residential.util.LocationUtil
import java.time.LocalDateTime
import java.util.UUID

class TownService {
    companion object {
        fun get(uuid: UUID): Town? {
            val connection = ResidentialDatabase.connection

            connection.prepareStatement("SELECT * FROM town WHERE uuid = ?").use { stmt ->
                stmt.setString(1, uuid.toString())
                stmt.executeQuery().use { rs ->
                    return Town.fromRs(rs)
                }
            }
        }

        fun getByName(name: String): Town? {
            val connection = ResidentialDatabase.connection

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
            val connection = ResidentialDatabase.connection

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

        fun register(name: String, founder: UUID, homeChunk: String, world: String, spawn: String): Town {
            if (!ResidentialConfig.config.worlds!!.contains(world))
                throw GracefulCommandException("You cannot create towns in this world.")

            val resident = ResidentService.get(founder)

            if (resident == null)
                throw GracefulCommandException("Founder couldn't be found.")

            if (resident.town != null)
                throw GracefulCommandException("You're already apart of a town.")

            val existingTown = getByName(name)

            if (existingTown != null)
                throw GracefulCommandException("A town with this name already exists.")

            val chunk = ChunkService.Companion.get(homeChunk, world)

            if (chunk != null)
                throw GracefulCommandException("Chunk already claimed.")

            val connection = ResidentialDatabase.connection

            val uuid = UUID.randomUUID()

            connection.prepareStatement("INSERT INTO town (uuid, name, founder, foundedAt, homeChunk, spawn) VALUES (?, ?, ?, ?, ?, ?)").use { stmt ->
                stmt.setString(1, uuid.toString())
                stmt.setString(2, name)
                stmt.setString(3, founder.toString())
                stmt.setString(4, LocalDateTime.now().toString())
                stmt.setString(5, homeChunk)
                stmt.setString(6, spawn)
                stmt.execute()
            }

            ResidentialTownCreationEvent(uuid).callEvent()

            Residential.economy.withdrawPlayer(Residential.instance.server.getOfflinePlayer(founder), ResidentialConfig.config.town!!.cost.toDouble())

            TownRoleService.createDefaults(uuid)
            ResidentService.joinTown(founder, uuid)
            ChunkService.claim(uuid, homeChunk, world)

            return get(uuid)!!
        }

        fun teleport(town: UUID, player: UUID) {
            val foundTown = get(town)

            if (foundTown == null)
                throw GracefulCommandException("Town doesn't exist.")

            val player = Residential.instance.server.getPlayer(player)
            // TODO: add spawnWorld for town, this is bad
            // TODO: always faces south
            player?.teleport(LocationUtil.stringToLocation(foundTown.spawn, player.world.name))
        }
    }
}