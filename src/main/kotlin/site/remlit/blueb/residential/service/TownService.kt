package site.remlit.blueb.residential.service

import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.Database
import site.remlit.blueb.residential.Logger
import site.remlit.blueb.residential.event.TownBankDepositEvent
import site.remlit.blueb.residential.event.TownBankWithdrawEvent
import site.remlit.blueb.residential.event.TownCreationEvent
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.model.Resident
import site.remlit.blueb.residential.model.Town
import site.remlit.blueb.residential.util.ChunkUtil
import site.remlit.blueb.residential.util.LocationUtil
import site.remlit.blueb.residential.util.MessageUtil
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

        fun getAll(): List<Town> {
            Database.connection.prepareStatement("SELECT * FROM town WHERE abandoned = false").use { stmt ->
                stmt.executeQuery().use { rs ->
                    return Town.manyFromRs(rs)
                }
            }
        }

        fun getAllUuids(): List<UUID> {
                Database.connection.prepareStatement("SELECT * FROM town WHERE abandoned = false").use { stmt ->
                val list = mutableListOf<UUID>()
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        list.add(UUID.fromString(rs.getString("uuid")))
                    }
                }
                return list.toList()
            }
        }

        fun getAllNames(): List<String> {
            Database.connection.prepareStatement("SELECT * FROM town WHERE abandoned = false").use { stmt ->
                val list = mutableListOf<String>()
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        list.add(rs.getString("name"))
                    }
                }
                return list.toList()
            }
        }

        fun getMayor(town: UUID): Resident? {
            val role = TownRoleService.getByTypeMayor(town)!!
            return ResidentService.getByRole(role.uuid)
        }

        fun getResidents(town: UUID): List<Resident> {
            Database.connection.prepareStatement("SELECT * FROM resident WHERE town = ?").use { stmt ->
                stmt.setString(1, town.toString())
                stmt.executeQuery().use { rs ->
                    return Resident.manyFromRs(rs)
                }
            }
        }

        fun getResidentCount(town: UUID): Int {
            Database.connection.prepareStatement("SELECT COUNT(*) FROM resident WHERE town = ?").use { stmt ->
                stmt.setString(1, town.toString())
                stmt.executeQuery().use { rs ->
                    while (rs.next())
                        return rs.getInt(1)
                }
            }
            return 0
        }

        fun broadcastToResidents(town: UUID, message: String) {
            val town = get(town)!!
            val residents = getResidents(town.uuid)
            Logger.info("Broadcast", "${town.name}: $message")

            for (resident in residents) {
                MessageUtil.send(resident.getPlayer(), message)
            }
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

            player.teleport(LocationUtil.stringToLocation(foundTown.spawn, ChunkUtil.stringToChunk(foundTown.homeChunk)!!.world.name))
            SoundUtil.playTeleport(player)
        }

        private fun handleMoneyAdd(town: UUID, amount: Double) {
            val town = get(town)!!
            val calculatedAmount = town.balance + amount
            Database.connection.prepareStatement("UPDATE town SET balance = ? WHERE uuid = ?").use { stmt ->
                stmt.setDouble(1, calculatedAmount)
                stmt.setString(2, town.uuid.toString())
                stmt.execute()
            }
        }

        fun deposit(town: UUID, amount: Double) {
            handleMoneyAdd(town, amount)
            TownBankDepositEvent(town, amount).callEvent()
        }

        fun withdraw(town: UUID, amount: Double) {
            handleMoneyAdd(town, amount * -1)
            TownBankWithdrawEvent(town, amount).callEvent()
        }

        fun setName(town: UUID, name: String) {
            val townWithName = getByName(name)

            if (townWithName != null && townWithName.uuid != town)
                throw GracefulCommandException("This name has been taken.")

            Database.connection.prepareStatement("UPDATE town SET name = ? WHERE uuid = ?").use { stmt ->
                stmt.setString(1, name)
                stmt.setString(2, town.toString())
                stmt.execute()
            }
        }

        fun setOpen(town: UUID, to: Boolean? = null): Boolean {
            val changeTo = to ?: !(get(town)!!.open)

            Database.connection.prepareStatement("UPDATE town SET open = ? WHERE uuid = ?").use { stmt ->
                stmt.setBoolean(1, changeTo)
                stmt.setString(2, town.toString())
                stmt.execute()
            }

            return changeTo
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