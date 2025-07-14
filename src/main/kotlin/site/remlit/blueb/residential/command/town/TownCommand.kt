package site.remlit.blueb.residential.command.town

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.util.inline.safeCommand
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.service.ChunkService
import site.remlit.blueb.residential.service.ResidentService
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.ChunkUtil
import site.remlit.blueb.residential.util.LocationUtil
import site.remlit.blueb.residential.util.MessageUtil
import site.remlit.blueb.residential.util.UuidUtil

@CommandAlias("town|t")
@CommandPermission("residential.town")
@Description("Commands for managing towns")
class TownCommand : BaseCommand() {
    // todo: for town and the like: you can input nonsense and it just goes to your town

    @Default
    @Syntax("<town>")
    @Description("Get information about a town")
    fun default(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player

            val resident = ResidentService.Companion.get(player.uniqueId)
            val townUuid = UuidUtil.fromStringOrNull(args.getOrNull(0)) ?: resident?.town

            if (townUuid == null)
                throw GracefulCommandException("<red>You aren't in a town, please specify one.")

            val town = TownService.Companion.get(townUuid)
            if (town == null)
                throw GracefulCommandException("<red>Town doesn't exist.")

            val founder = Residential.instance.server.getPlayer(town.founder)
            val mayor = town.getMayor()?.getPlayer()

            val allClaimedChunks = ChunkService.getAllClaimedChunks(town.uuid)

            MessageUtil.send(player, MessageUtil.createLine(town.name))
            MessageUtil.send(player, "Founded ${MessageUtil.formatLocalDateTime(town.foundedAt)}${if (founder != null) " by ${founder.name}" else ""}")
            MessageUtil.send(player, "Mayor: ${mayor?.name}")
            MessageUtil.send(player, "Balance: ${Residential.economy.format(town.balance)}")
            MessageUtil.send(player, "Claimed ${allClaimedChunks.size}/${town.getMaxChunks()}")
            MessageUtil.send(player, "Open: ${if (town.open) "T" else "F"} PVP: ${if (town.pvp) "T" else "F"} Mobs: ${if (town.mobs) "T" else "F"} Fire: ${if (town.fire) "T" else "F"}")
            MessageUtil.send(player, MessageUtil.createLine())
        }

    @Subcommand("new")
    @Syntax("<town>")
    @CommandPermission("residential.town.new")
    @Description("Create a new town")
    fun new(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player

            val name = args.getOrNull(0)

            // TODO: min distance between claimed areas

            if (name.isNullOrBlank())
                throw GracefulCommandException("<red>Town name cannot be blank.")

            if (name.length > Configuration.Companion.config.town.name.maxLength)
                throw GracefulCommandException("<red>Town name cannot be longer than ${Configuration.Companion.config.town.name.maxLength} characters.")

            val balance = Residential.economy.getBalance(player)
            if (balance < Configuration.config.town.cost)
                throw GracefulCommandException("<red>You cannot afford creating a town, which costs ${Residential.economy.format(Configuration.config.town.cost.toDouble())}.")

            val homeChunk = player.chunk
            TownService.register(name, player.uniqueId, ChunkUtil.chunkToString(homeChunk), player.world.name, LocationUtil.locationToString(player.location))
        }

    @Subcommand("spawn")
    @Syntax("<town>")
    @CommandPermission("residential.town.spawn")
    @Description("Teleport to the spawn of a town")
    fun spawn(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val resident = ResidentService.Companion.get(player.uniqueId)
            val townUuid = UuidUtil.fromStringOrNull(args.getOrNull(0)) ?: resident?.town

            if (townUuid == null)
                throw GracefulCommandException("<red>You aren't in a town, please specify one.")

            val town = TownService.Companion.get(townUuid)
            if (town == null)
                throw GracefulCommandException("<red>Town doesn't exist")

            TownService.Companion.teleport(townUuid, player.uniqueId)
        }

    @Subcommand("claim")
    @CommandPermission("residential.town.claim")
    @Description("Claim the current chunk you're standing in for you town")
    fun claim(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val resident = ResidentService.get(player.uniqueId)

            if (resident?.town == null)
                throw GracefulCommandException("<red>You aren't in a town.")

            if (resident.getTownRoles().find { it.cmdPlotManagement || it.cmdMayor } == null)
                throw GracefulCommandException("<red>You do not have plot management permissions in this town.")

            val chunk = ChunkUtil.chunkToString(player.chunk)

            ChunkService.claim(resident.town, chunk)
            MessageUtil.send(player, "<dark_green>Claimed chunk at $chunk")
        }

    @Subcommand("delete")
    @CommandPermission("residential.town.delete")
    @Description("Delete your town")
    fun delete(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) { TODO() }

    @Subcommand("invite")
    @Syntax("[player]")
    @CommandPermission("residential.town.invite")
    @Description("Invite a player to your town")
    fun invite(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) { TODO() }

    /*
    * Bank
    * */

    @Subcommand("deposit")
    @Syntax("[amount]")
    @CommandPermission("residential.town.deposit")
    @Description("Deposit money to your town bank")
    fun deposit(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val resident = ResidentService.get(player.uniqueId)

            if (resident?.town == null)
                throw GracefulCommandException("<red>You aren't in a town.")

            if (resident.getTownRoles().find { it.bankDeposit || it.cmdMayor } == null)
                throw GracefulCommandException("<red>You do not have bank deposit permissions in this town.")

            val amount = args.getOrNull(0)?.toDoubleOrNull()

            if (amount == null)
                throw GracefulCommandException("<red>You must specify an amount.")

            if (amount < 0.01)
                throw GracefulCommandException("<red>You must deposit at least ${Residential.economy.format(0.01)}.")

            if (Residential.economy.getBalance(player) < amount)
                throw GracefulCommandException("<red>You don't have enough money.")

            if (Residential.economy.withdrawPlayer(player, amount).type != EconomyResponse.ResponseType.SUCCESS)
                throw GracefulCommandException("<red>Failed to withdraw from your account.")

            TownService.deposit(resident.town, amount)
        }

    @Subcommand("withdraw")
    @Syntax("[amount]")
    @CommandPermission("residential.town.withdraw")
    @Description("Withdraw money from your town bank")
    fun withdraw(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val resident = ResidentService.get(player.uniqueId)

            if (resident?.town == null)
                throw GracefulCommandException("<red>You aren't in a town.")

            if (resident.getTownRoles().find { it.bankWithdraw || it.cmdMayor } == null)
                throw GracefulCommandException("<red>You do not have bank withdraw permissions in this town.")

            val amount = args.getOrNull(0)?.toDoubleOrNull()

            if (amount == null)
                throw GracefulCommandException("<red>You must specify an amount.")

            if (amount < 0.01)
                throw GracefulCommandException("<red>You must withdraw at least ${Residential.economy.format(0.01)}.")

            val town = TownService.get(resident.town)!!
            if (town.balance < amount)
                throw GracefulCommandException("<red>Your town doesn't have enough money.")

            if (Residential.economy.depositPlayer(player, amount).type != EconomyResponse.ResponseType.SUCCESS)
                throw GracefulCommandException("<red>Failed to deposit to your account.")

            TownService.withdraw(resident.town, amount)
        }
}