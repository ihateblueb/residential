package me.blueb.residential.services

import me.blueb.residential.ResidentialDatabase
import me.blueb.residential.events.ResidentialTownCreationEvent
import me.blueb.residential.models.GracefulCommandException
import me.blueb.residential.models.Town
import me.blueb.residential.util.DatabaseUtil
import java.time.LocalDateTime
import java.util.UUID

class TownService {
    companion object {
        fun get(uuid: UUID): Town? {
            val connection = ResidentialDatabase.connection

            connection.prepareStatement("SELECT * FROM town WHERE uuid = ?").use { stmt ->
                stmt.setString(1, uuid.toString())
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        val rsTag = rs.getString("tag")
                        val tag = if (!rs.wasNull())
                            rsTag
                        else null

                        val rsNation = rs.getString("nation")
                        val nation = if (!rs.wasNull())
                            UUID.fromString(rsNation)
                        else null

                        val town = Town(
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
                        println("Fetched town ${town.uuid} ${town.name}")
                        return@get town
                    }
                }
            }

            println("Town doesn't exist")
            return null
        }

        fun getByName(name: String): Town? {
            val connection = ResidentialDatabase.connection

            connection.prepareStatement("SELECT * FROM town WHERE name = ?").use { stmt ->
                stmt.setString(1, name)
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        val rsTag = rs.getString("tag")
                        val tag = if (!rs.wasNull())
                            rsTag
                        else null

                        val rsNation = rs.getString("nation")
                        val nation = if (!rs.wasNull())
                            UUID.fromString(rsNation)
                        else null

                        val town = Town(
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
                        println("Fetched town ${town.uuid} ${town.name}")
                        return@getByName town
                    }
                }
            }

            println("Town doesn't exist")
            return null
        }

        fun register(name: String, founder: UUID, homeChunk: String, world: String, spawn: String): Town {
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

            println("Town $uuid $name registered")

            ResidentialTownCreationEvent(uuid).callEvent()

            ResidentService.joinTown(founder, uuid)
            ChunkService.claim(uuid, homeChunk, world)

            return get(uuid)!!
        }
    }
}