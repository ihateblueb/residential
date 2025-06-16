package me.blueb.residential.services

import me.blueb.residential.Residential
import me.blueb.residential.ResidentialDatabase
import me.blueb.residential.models.Resident
import me.blueb.residential.util.DatabaseUtil
import java.util.UUID
import kotlin.use

class ResidentService {
    companion object {
        fun get(uuid: UUID): Resident? {
            val connection = ResidentialDatabase.connection

            connection.prepareStatement("SELECT * FROM resident WHERE uuid = ?").use { stmt ->
                stmt.setString(1, uuid.toString())
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        val rsTrusted = rs.getString("trusted")
                        val trusted = if (!rs.wasNull())
                            DatabaseUtil.extractList(rsTrusted) { DatabaseUtil.extractUuid(it) }
                        else listOf()

                        val rsTown = rs.getString("town")
                        val town = if (!rs.wasNull())
                            DatabaseUtil.extractUuid(rsTown)
                        else null

                        val resident = Resident(
                            uuid = UUID.fromString(rs.getString("uuid")),
                            trusted = trusted,
                            town = town,
                        )
                        println("Fetched resident ${resident.uuid}")
                        return@get resident
                    }
                }
            }

            println("Resident doesn't exist")
            return null
        }

        fun register(uuid: UUID): Resident {
            val connection = ResidentialDatabase.connection

            connection.prepareStatement("INSERT INTO resident (uuid) VALUES (?)").use { stmt ->
                stmt.setString(1, uuid.toString())
                stmt.executeUpdate()
            }

            println("Resident $uuid registered")

            return get(uuid)!!
        }

        fun joinTown(resident: UUID, town: UUID) {
            val connection = ResidentialDatabase.connection

            connection.prepareStatement("UPDATE resident SET town = ? WHERE uuid = ?").use { stmt ->
                stmt.setString(1, town.toString())
                stmt.setString(2, resident.toString())
                stmt.execute()
            }
        }
    }
}