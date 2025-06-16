package me.blueb.residential.services

import me.blueb.residential.ResidentialDatabase
import me.blueb.residential.events.ResidentialTownCreationEvent
import me.blueb.residential.models.Town
import me.blueb.residential.util.DatabaseUtil
import java.util.UUID
import kotlin.use

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

        fun register(name: String, founder: UUID, homeChunk: String, spawn: String): Town {
            val connection = ResidentialDatabase.connection

            val uuid = UUID.randomUUID()

            connection.prepareStatement("INSERT INTO town (uuid, name, founder, homeChunk, spawn) VALUES (?, ?, ?, ?)").use { stmt ->
                stmt.setString(1, uuid.toString())
                stmt.setString(2, name)
                stmt.setString(3, founder.toString())
                stmt.setString(4, homeChunk)
                stmt.setString(5, spawn)
                stmt.executeUpdate()
            }

            println("Town $uuid $name registered")

            val creationEvent = ResidentialTownCreationEvent()
            creationEvent.town = uuid
            creationEvent.callEvent()

            return get(uuid)!!
        }
    }
}