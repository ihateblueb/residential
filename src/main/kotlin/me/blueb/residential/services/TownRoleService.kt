package me.blueb.residential.services

import me.blueb.residential.Residential
import me.blueb.residential.ResidentialConfig
import me.blueb.residential.ResidentialDatabase
import me.blueb.residential.models.GracefulCommandException
import me.blueb.residential.models.TownRole
import java.util.UUID
import kotlin.use

class TownRoleService {
    companion object {
        fun get(town: UUID, role: UUID): TownRole? {
            val connection = ResidentialDatabase.connection

            connection.prepareStatement("SELECT * FROM town_role WHERE uuid = ?").use { stmt ->
                stmt.setString(1, role.toString())
                stmt.executeQuery().use { rs ->
                    return TownRole.fromRs(rs)
                }
            }
        }

        fun getByName(town: UUID, name: String): TownRole? {
            val connection = ResidentialDatabase.connection

            connection.prepareStatement("SELECT * FROM town_role WHERE town = ? AND name = ?").use { stmt ->
                stmt.setString(1, town.toString())
                stmt.setString(2, name)
                stmt.executeQuery().use { rs ->
                    return TownRole.fromRs(rs)
                }
            }
        }

        fun getByTypeDefault(town: UUID): TownRole? {
            val connection = ResidentialDatabase.connection

            connection.prepareStatement("SELECT * FROM town_role WHERE town = ? AND is_default = true").use { stmt ->
                stmt.setString(1, town.toString())
                stmt.executeQuery().use { rs ->
                    return TownRole.fromRs(rs)
                }
            }
        }

        fun getByTypeMayor(town: UUID): TownRole? {
            val connection = ResidentialDatabase.connection

            connection.prepareStatement("SELECT * FROM town_role WHERE town = ? AND is_mayor = true").use { stmt ->
                stmt.setString(1, town.toString())
                stmt.executeQuery().use { rs ->
                    return TownRole.fromRs(rs)
                }
            }
        }

        fun create(
            town: UUID,
            name: String,

            isDefault: Boolean = false,
            isMayor: Boolean = false,

            bankWithdraw: Boolean = false,
            bankDeposit: Boolean = true,

            cmdPlotManagement: Boolean = false,
            cmdMayor: Boolean = false
        ) {
            val connection = ResidentialDatabase.connection

            val existing = getByName(town, name)
            if (existing != null)
                throw GracefulCommandException("A role with this name already exists.")

            if (isDefault) {
                val existingDefault = getByTypeDefault(town)
                println(existingDefault)
                if (existingDefault != null)
                    throw GracefulCommandException("Only one default role can exist.")
            }

            if (isMayor) {
                val existingMayor = getByTypeMayor(town)
                if (existingMayor != null)
                    throw GracefulCommandException("Only one mayor role can exist.")
            }

            val uuid = UUID.randomUUID()

            connection.prepareStatement("INSERT INTO town_role (uuid, town, name, is_default, is_mayor, bank_withdraw, bank_deposit, cmd_plot_management, cmd_mayor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)").use { stmt ->
                stmt.setString(1, uuid.toString())
                stmt.setString(2, town.toString())
                stmt.setString(3, name)

                stmt.setBoolean(4, isDefault)
                stmt.setBoolean(5, isMayor)

                stmt.setBoolean(6, bankWithdraw)
                stmt.setBoolean(7, bankDeposit)

                stmt.setBoolean(8, cmdPlotManagement)
                stmt.setBoolean(9, cmdMayor)
                stmt.execute()
            }

            Residential.instance.logger.info("Created role $uuid named $name")
        }

        fun giveRole(player: UUID, role: UUID) {
            val resident = ResidentService.get(player)

            // give
        }

        fun setMayor(town: UUID, player: UUID) {
            val mayorRole = getByName(town, "mayor")
            if (mayorRole == null) throw GracefulCommandException("Mayor role does not exist.")


        }

        fun createDefaults(town: UUID) {
            val configDefaults = ResidentialConfig.config.town!!.roles.default
            for (role in configDefaults) {
                val split = role.split(",")

                create(
                    town,
                    split[0],

                    split[1].toBoolean(),
                    split[2].toBoolean(),

                    split[3].toBoolean(),
                    split[4].toBoolean(),

                    split[5].toBoolean(),
                    split[6].toBoolean()
                )
            }
        }

        fun createDefaultRole(town: UUID) {
            val configDefaults = ResidentialConfig.config.town!!.roles.default
            val defaultRole = configDefaults.find { it.split(",")[1].toBoolean() }
            val split = defaultRole?.split(",") ?: listOf()

            create(
                town,
                split[0],

                split[1].toBoolean(),
                split[2].toBoolean(),

                split[3].toBoolean(),
                split[4].toBoolean(),

                split[5].toBoolean(),
                split[6].toBoolean()
            )
        }

        fun createMayorRole(town: UUID) {
            val configDefaults = ResidentialConfig.config.town!!.roles.default
            val mayorRole = configDefaults.find { it.split(",")[2].toBoolean() }
            val split = mayorRole?.split(",") ?: listOf()

            create(
                town,
                split[0],

                split[1].toBoolean(),
                split[2].toBoolean(),

                split[3].toBoolean(),
                split[4].toBoolean(),

                split[5].toBoolean(),
                split[6].toBoolean()
            )
        }

        fun syncRoles() {
            val towns = TownService.getAllUuids()

            for (town in towns) {
                val defaultRole = getByTypeDefault(town)
                val mayorRole = getByTypeMayor(town)

                if (defaultRole == null) createDefaultRole(town)
                if (mayorRole == null) createMayorRole(town)
            }
        }
    }
}