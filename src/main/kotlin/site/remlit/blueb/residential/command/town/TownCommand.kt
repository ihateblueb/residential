package site.remlit.blueb.residential.command.town

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.service.ResidentService
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.ChunkUtil
import site.remlit.blueb.residential.util.LocationUtil
import site.remlit.blueb.residential.util.MessageUtil
import java.util.UUID

@CommandAlias("town|t")
@CommandPermission("residential.town")
class TownCommand : BaseCommand() {
    @Default
    fun default(sender: CommandSender, args: Array<String>) {
        val player = sender as Player

        val resident = ResidentService.Companion.get(player.uniqueId)
        val townUuid = if (args.getOrNull(0) != null) UUID.fromString(args[0]) else resident?.town

        if (townUuid == null) {
            MessageUtil.Companion.send(player, "<red>You aren't in a town, please specify one.")
            return
        }

        val town = TownService.Companion.get(townUuid)
        if (town == null) {
            MessageUtil.Companion.send(player, "<red>Town doesn't exist.")
            return
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_gray> -- <yellow>${town.name}<dark_gray> --"))
        player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Founded at ${town.foundedAt} by ${town.founder}"))
        player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Spawn: ${town.spawn} Home: ${town.homeChunk}"))
        player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Abandoned: ${town.abandoned} Nation: ${town.nation}"))
    }

    @Subcommand("new")
    @CommandPermission("residential.town.new")
    fun new(sender: CommandSender, args: Array<String>) {
        val player = sender as Player

        val name = args.getOrNull(0)

        // TODO: min distance between claimed areas

        if (name.isNullOrBlank()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Town name cannot be blank."))
            return
        }

        if (name.length > Configuration.Companion.config.town.name.maxLength) {
            MessageUtil.Companion.send(player, "<red>Town name cannot be longer than ${Configuration.Companion.config.town.name.maxLength} characters.")
            return
        }

        val balance = Residential.Companion.economy.getBalance(player)

        if (balance < Configuration.Companion.config.town.cost) {
            MessageUtil.Companion.send(player, "<red>You cannot afford creating a town, which costs ${
                Residential.Companion.economy.format(
                    Configuration.Companion.config.town.cost.toDouble())}.")
            return
        }

        val homeChunk = player.chunk

        try {
            TownService.Companion.register(name, player.uniqueId, ChunkUtil.Companion.chunkToString(homeChunk), player.world.name, LocationUtil.Companion.locationToString(player.location))
        } catch (e: GracefulCommandException) {
            MessageUtil.Companion.send(player, "<red>${e.message}")
            return
        }

        return
    }

    @Subcommand("claim")
    @CommandPermission("residential.town.claim")
    fun claim(sender: CommandSender, args: Array<String>) {}

    @Subcommand("delete")
    @CommandPermission("residential.town.delete")
    fun delete(sender: CommandSender, args: Array<String>) {}

    @Subcommand("spawn")
    @CommandPermission("residential.town.spawn")
    fun spawn(sender: CommandSender, args: Array<String>) {
        val player = sender as Player
        val resident = ResidentService.Companion.get(player.uniqueId)
        val townUuid = if (args.getOrNull(0) != null) UUID.fromString(args[0]) else resident?.town

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
}