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
                    return Resident.fromRs(rs)
                }
            }
        }

        fun register(uuid: UUID): Resident {
            val connection = ResidentialDatabase.connection

            connection.prepareStatement("INSERT INTO resident (uuid) VALUES (?)").use { stmt ->
                stmt.setString(1, uuid.toString())
                stmt.executeUpdate()
            }

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