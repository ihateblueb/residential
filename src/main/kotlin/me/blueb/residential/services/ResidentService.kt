package me.blueb.residential.services

import me.blueb.residential.ResidentialDatabase
import me.blueb.residential.models.Resident
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
                        val rsClaims = rs.getString("claims")
                        val claims = if (!rs.wasNull())
                            DatabaseService.extractList(rsClaims) { DatabaseService.extractUuid(it) }
                        else listOf()

                        val rsTrusted = rs.getString("claims")
                        val trusted = if (!rs.wasNull())
                            DatabaseService.extractList(rsTrusted) { DatabaseService.extractUuid(it) }
                        else listOf()

                        val resident = Resident(
                            uuid = UUID.fromString(rs.getString("uuid")),
                            claims = claims,
                            trusted = trusted,
                            town = null,
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

            return Resident(
                uuid = uuid,
                claims = listOf(),
                trusted = listOf(),
                town = null
            )
        }
    }
}