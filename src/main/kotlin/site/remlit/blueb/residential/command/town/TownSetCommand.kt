package site.remlit.blueb.residential.command.town

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.service.ResidentService
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.ChunkUtil
import site.remlit.blueb.residential.util.LocationUtil
import site.remlit.blueb.residential.util.MessageUtil

@CommandAlias("town|t")
@Subcommand("set")
@CommandPermission("residential.town.set")
class TownSetCommand : BaseCommand() {
    @Subcommand("name")
    @CommandPermission("residential.town.set.name")
    fun setName(sender: CommandSender, args: Array<String>) {
        val player = sender as Player

        val resident = ResidentService.get(player.uniqueId)

        if (resident?.town == null) {
            MessageUtil.send(player, "<red>You aren't in a town.")
            return
        }

        if (resident.getTownRoles().find { it.cmdMayor } == null) {
            MessageUtil.send(player, "<red>You do not have mayor permissions in this town.")
            return
        }

        val name = args.getOrNull(0)
        if (name == null) {
            MessageUtil.send(player, "<red>New name cannot be blank.")
            return
        }

        try {
            TownService.setName(resident.town, name)
        } catch (e: GracefulCommandException) {
            MessageUtil.send(player, "<red>${e.message}")
            return
        }

        MessageUtil.send(player, "<dark_green>Set town name to $name.")
    }

    @Subcommand("open")
    @CommandPermission("residential.town.set.open")
    fun setOpen(sender: CommandSender, args: Array<String>) {
        val player = sender as Player

        val resident = ResidentService.get(player.uniqueId)

        if (resident?.town == null) {
            MessageUtil.send(player, "<red>You aren't in a town.")
            return
        }

        if (resident.getTownRoles().find { it.cmdMayor } == null) {
            MessageUtil.send(player, "<red>You do not have mayor permissions in this town.")
            return
        }

        var open: Boolean

        try {
            open = TownService.setOpen(resident.town, args.getOrNull(0)?.toBoolean())
        } catch (e: GracefulCommandException) {
            MessageUtil.send(player, "<red>${e.message}")
            return
        }

        if (open) MessageUtil.send(player, "<dark_green>Set town to be open for players to join.")
        else MessageUtil.send(player, "<dark_green>Set town to be closed for players to join.")
    }

    @Subcommand("spawn")
    @CommandPermission("residential.town.set.spawn")
    fun setSpawn(sender: CommandSender, args: Array<String>) {
        val player = sender as Player

        val resident = ResidentService.get(player.uniqueId)

        if (resident?.town == null) {
            MessageUtil.send(player, "<red>You aren't in a town.")
            return
        }

        if (resident.getTownRoles().find { it.cmdMayor } == null) {
            MessageUtil.send(player, "<red>You do not have mayor permissions in this town.")
            return
        }

        val chunk = ChunkUtil.chunkToString(player.chunk)
        val location = LocationUtil.locationToString(player.location)

        try {
            TownService.setSpawn(resident.town, chunk, location)
        } catch (e: GracefulCommandException) {
            MessageUtil.send(player, "<red>${e.message}")
            return
        }

        MessageUtil.send(player, "<dark_green>Set spawn location")
    }

    @Subcommand("mayor")
    @CommandPermission("residential.town.set.mayor")
    fun setMayor(sender: CommandSender, args: Array<String>) { TODO() }

    @Subcommand("tag")
    @CommandPermission("residential.town.set.tag")
    fun setTag(sender: CommandSender, args: Array<String>) { TODO() }
}