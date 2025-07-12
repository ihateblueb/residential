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
    // todo: ensure resident has perm, apart of some role maybe
    @Subcommand("name")
    @CommandPermission("residential.town.set.name")
    fun setName(sender: CommandSender, args: Array<String>) { TODO() }

    @Subcommand("mayor")
    @CommandPermission("residential.town.set.mayor")
    fun setMayor(sender: CommandSender, args: Array<String>) { TODO() }

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
            MessageUtil.send(player, "<red>You do not have plot management permissions in this town.")
            return
        }

        val chunk = ChunkUtil.chunkToString(player.chunk)
        val location = LocationUtil.locationToString(player.location)

        println(chunk)
        println(location)

        try {
            TownService.setSpawn(resident.town, chunk, location)
        } catch (e: GracefulCommandException) {
            MessageUtil.send(player, "<red>${e.message}")
            return
        }

        MessageUtil.send(player, "<dark_green>Changed spawn location")
    }
}