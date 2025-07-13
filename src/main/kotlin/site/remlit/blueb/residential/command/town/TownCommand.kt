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
import site.remlit.blueb.residential.command.safeCommand
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
    fun new(sender: CommandSender, args: Array<String>) {
        val player = sender as Player

        val name = args.getOrNull(0)

        // TODO: min distance between claimed areas

        if (name.isNullOrBlank()) {
            MessageUtil.send(player, "<red>Town name cannot be blank.")
            return
        }

        if (name.length > Configuration.Companion.config.town.name.maxLength) {
            MessageUtil.send(player, "<red>Town name cannot be longer than ${Configuration.Companion.config.town.name.maxLength} characters.")
            return
        }

        val balance = Residential.economy.getBalance(player)

        if (balance < Configuration.config.town.cost) {
            MessageUtil.send(player, "<red>You cannot afford creating a town, which costs ${Residential.economy.format(Configuration.config.town.cost.toDouble())}.")
            return
        }

        val homeChunk = player.chunk

        try {
            TownService.register(name, player.uniqueId, ChunkUtil.chunkToString(homeChunk), player.world.name, LocationUtil.locationToString(player.location))
        } catch (e: GracefulCommandException) {
            MessageUtil.Companion.send(player, "<red>${e.message}")
            return
        }

        return
    }

    @Subcommand("spawn")
    @Syntax("<town>")
    @CommandPermission("residential.town.spawn")
    @Description("Teleport to the spawn of a town")
    fun spawn(sender: CommandSender, args: Array<String>) {
        val player = sender as Player
        val resident = ResidentService.Companion.get(player.uniqueId)
        val townUuid = UuidUtil.fromStringOrNull(args.getOrNull(0)) ?: resident?.town

        if (townUuid == null) {
            MessageUtil.Companion.send(player, "<red>You aren't in a town, please specify one.")
            return
        }

        val town = TownService.Companion.get(townUuid)
        if (town == null) {
            MessageUtil.Companion.send(player, "<red>Town doesn't exist.")
            return
        }

        TownService.Companion.teleport(townUuid, player.uniqueId)
    }

    @Subcommand("claim")
    @CommandPermission("residential.town.claim")
    @Description("Claim the current chunk you're standing in for you town")
    fun claim(sender: CommandSender, args: Array<String>) {
        val player = sender as Player
        val resident = ResidentService.get(player.uniqueId)

        if (resident?.town == null) {
            MessageUtil.send(player, "<red>You aren't in a town.")
            return
        }

        if (resident.getTownRoles().find { it.cmdPlotManagement || it.cmdMayor } == null) {
            MessageUtil.send(player, "<red>You do not have plot management permissions in this town.")
            return
        }

        val chunk = ChunkUtil.chunkToString(player.chunk)

        try {
            ChunkService.claim(resident.town, chunk)
        } catch (e: GracefulCommandException) {
            MessageUtil.send(player, "<red>${e.message}")
            return
        }

        MessageUtil.send(player, "<dark_green>Claimed chunk at $chunk")
    }

    @Subcommand("delete")
    @CommandPermission("residential.town.delete")
    @Description("Delete your town")
    fun delete(sender: CommandSender, args: Array<String>) { TODO() }

    @Subcommand("invite")
    @Syntax("[player]")
    @CommandPermission("residential.town.invite")
    @Description("Invite a player to your town")
    fun invite(sender: CommandSender, args: Array<String>) { TODO() }

    /*
    * Bank
    * */

    @Subcommand("deposit")
    @Syntax("[amount]")
    @CommandPermission("residential.town.deposit")
    @Description("Deposit money to your town bank")
    fun deposit(sender: CommandSender, args: Array<String>) {
        val player = sender as Player
        val resident = ResidentService.get(player.uniqueId)

        if (resident?.town == null) {
            MessageUtil.send(player, "<red>You aren't in a town.")
            return
        }

        if (resident.getTownRoles().find { it.bankDeposit || it.cmdMayor } == null) {
            MessageUtil.send(player, "<red>You do not have bank deposit permissions in this town.")
            return
        }

        val amount = args.getOrNull(0)?.toDoubleOrNull()

        if (amount == null) {
            MessageUtil.send(player, "<red>You must specify an amount.")
            return
        }

        if (amount < 0.01) {
            MessageUtil.send(player, "<red>You must deposit at least ${Residential.economy.format(0.01)}.")
            return
        }

        if (Residential.economy.getBalance(player) < amount) {
            MessageUtil.send(player, "<red>You don't have enough money.")
            return
        }

        if (Residential.economy.withdrawPlayer(player, amount).type != EconomyResponse.ResponseType.SUCCESS) {
            MessageUtil.send(player, "<red>Failed to withdraw from your account.")
            return
        } else {
            TownService.deposit(resident.town, amount)
        }
    }

    @Subcommand("withdraw")
    @Syntax("[amount]")
    @CommandPermission("residential.town.withdraw")
    @Description("Withdraw money from your town bank")
    fun withdraw(sender: CommandSender, args: Array<String>) {
        val player = sender as Player
        val resident = ResidentService.get(player.uniqueId)

        if (resident?.town == null) {
            MessageUtil.send(player, "<red>You aren't in a town.")
            return
        }

        if (resident.getTownRoles().find { it.bankWithdraw || it.cmdMayor } == null) {
            MessageUtil.send(player, "<red>You do not have bank withdraw permissions in this town.")
            return
        }

        val amount = args.getOrNull(0)?.toDoubleOrNull()

        if (amount == null) {
            MessageUtil.send(player, "<red>You must specify an amount.")
            return
        }

        if (amount < 0.01) {
            MessageUtil.send(player, "<red>You must withdraw at least ${Residential.economy.format(0.01)}.")
            return
        }

        val town = TownService.get(resident.town)!!

        if (town.balance < amount) {
            MessageUtil.send(player, "<red>Your town doesn't have enough money.")
            return
        }

        if (Residential.economy.depositPlayer(player, amount).type != EconomyResponse.ResponseType.SUCCESS) {
            MessageUtil.send(player, "<red>Failed to deposit to your account.")
            return
        } else {
            TownService.withdraw(resident.town, amount)
        }
    }
}