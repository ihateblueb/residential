package site.remlit.blueb.residential.service

import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.Database
import site.remlit.blueb.residential.Logger
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.model.TownRole
import site.remlit.blueb.residential.util.DatabaseUtil
import site.remlit.blueb.residential.util.UuidUtil
import java.util.UUID
import kotlin.use

class TownRoleService {
    companion object {
        fun get(town: UUID, role: UUID): TownRole? {
            val connection = Database.connection

            connection.prepareStatement("SELECT * FROM town_role WHERE uuid = ?").use { stmt ->
                stmt.setString(1, role.toString())
                stmt.executeQuery().use { rs ->
                    return TownRole.fromRs(rs)
                }
            }
        }

        fun getByName(town: UUID, name: String): TownRole? {
            val connection = Database.connection

            connection.prepareStatement("SELECT * FROM town_role WHERE town = ? AND name = ?").use { stmt ->
                stmt.setString(1, town.toString())
                stmt.setString(2, name)
                stmt.executeQuery().use { rs ->
                    return TownRole.fromRs(rs)
                }
            }
        }

        fun getByTypeDefault(town: UUID): TownRole? {
            val connection = Database.connection

            connection.prepareStatement("SELECT * FROM town_role WHERE town = ? AND is_default = true").use { stmt ->
                stmt.setString(1, town.toString())
                stmt.executeQuery().use { rs ->
                    return TownRole.fromRs(rs)
                }
            }
        }

        fun getByTypeMayor(town: UUID): TownRole? {
            val connection = Database.connection

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

            destroy: Boolean = false,
            place: Boolean = false,
            use: Boolean = false,
            spawn: Boolean = false,

            bankWithdraw: Boolean = false,
            bankDeposit: Boolean = true,

            announce: Boolean = false,

            cmdPlotManagement: Boolean = false,
            cmdMayor: Boolean = false
        ) {
            val connection = Database.connection

            val existing = getByName(town, name)
            if (existing != null)
                throw GracefulCommandException("A role with this name already exists.")

            if (isDefault) {
                val existingDefault = getByTypeDefault(town)
                if (existingDefault != null)
                    throw GracefulCommandException("Only one default role can exist.")
            }

            if (isMayor) {
                val existingMayor = getByTypeMayor(town)
                if (existingMayor != null)
                    throw GracefulCommandException("Only one mayor role can exist.")
            }

            val uuid = UUID.randomUUID()

            connection.prepareStatement("INSERT INTO town_role (uuid, town, name, is_default, is_mayor, bank_withdraw, bank_deposit, announce, cmd_plot_management, cmd_mayor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)").use { stmt ->
                stmt.setString(1, uuid.toString())
                stmt.setString(2, town.toString())
                stmt.setString(3, name)

                stmt.setBoolean(4, isDefault)
                stmt.setBoolean(5, isMayor)

                stmt.setBoolean(6, bankWithdraw)
                stmt.setBoolean(7, bankDeposit)

                stmt.setBoolean(8, announce)

                stmt.setBoolean(9, cmdPlotManagement)
                stmt.setBoolean(10, cmdMayor)
                stmt.execute()
            }

            Logger.info("Created role $uuid named $name")
        }

        fun giveRole(player: UUID, role: UUID) {
            val resident = ResidentService.get(player)
            if (resident == null)
                throw Exception("No resident found by $player")

            val newList = resident.roles.toMutableList().apply { add(role) }
            val json = DatabaseUtil.listToJson(newList) { it.toString() }
            println(json)

            Database.connection.prepareStatement("UPDATE resident SET roles = ?").use { stmt ->
                stmt.setString(1, json)
                stmt.executeUpdate()
            }
        }

        fun setMayor(town: UUID, player: UUID) {
            val mayorRole = getByName(town, "mayor")
            if (mayorRole == null) throw GracefulCommandException("Mayor role does not exist.")

            // TODO
        }

        fun createFromSplit(town: UUID, split: List<String>) {
            create(
                town,
                split[0],

                split[1].toBoolean(), // isDefault
                split[2].toBoolean(), // isMayor

                split[3].toBoolean(), // destroy
                split[4].toBoolean(), // place
                split[5].toBoolean(), // use
                split[6].toBoolean(), // spawn

                split[7].toBoolean(), // bankWithdraw
                split[8].toBoolean(), // bankDeposit

                split[9].toBoolean(), // announce

                split[10].toBoolean(), // cmdPlotManagement
                split[11].toBoolean(), // cmdMayor
            )
        }

        fun createDefaults(town: UUID) {
            val configDefaults = Configuration.config.town!!.roles.default
            for (role in configDefaults) {
                val split = role.split(",")
                createFromSplit(town, split)
            }
        }

        fun createDefaultRole(town: UUID) {
            val configDefaults = Configuration.config.town!!.roles.default
            val defaultRole = configDefaults.find { it.split(",")[1].toBoolean() }
            val split = defaultRole?.split(",") ?: listOf()
            createFromSplit(town, split)
        }

        fun createMayorRole(town: UUID) {
            val configDefaults = Configuration.config.town!!.roles.default
            val mayorRole = configDefaults.find { it.split(",")[2].toBoolean() }
            val split = mayorRole?.split(",") ?: listOf()
            createFromSplit(town, split)
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