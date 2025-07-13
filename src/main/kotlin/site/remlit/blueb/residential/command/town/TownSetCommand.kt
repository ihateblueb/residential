package site.remlit.blueb.residential.command.town

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.remlit.blueb.residential.command.safeCommand
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
    @Syntax("[name]")
    @CommandPermission("residential.town.set.name")
    @Description("Change the name of your town")
    fun setName(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player

            val resident = ResidentService.get(player.uniqueId)

            if (resident?.town == null)
                throw GracefulCommandException("<red>You aren't in a town.")

            if (resident.getTownRoles().find { it.cmdMayor } == null)
                throw GracefulCommandException("<red>You do not have mayor permissions in this town.")

            val name = args.getOrNull(0)
            if (name == null)
                throw GracefulCommandException("<red>New name cannot be blank.")

            TownService.setName(resident.town, name)
            MessageUtil.send(player, "<dark_green>Set town name to $name.")
        }

    @Subcommand("open")
    @Syntax("<open>")
    @CommandPermission("residential.town.set.open")
    @Description("Toggle if your town is able to be joined without an invite")
    fun setOpen(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player

            val resident = ResidentService.get(player.uniqueId)

            if (resident?.town == null)
                throw GracefulCommandException("<red>You aren't in a town.")

            if (resident.getTownRoles().find { it.cmdMayor } == null)
                throw GracefulCommandException("<red>You do not have mayor permissions in this town.")

            val open: Boolean = TownService.setOpen(resident.town, args.getOrNull(0)?.toBoolean())
            if (open) MessageUtil.send(player, "<dark_green>Set town to be open for players to join.")
            else MessageUtil.send(player, "<dark_green>Set town to be closed for players to join.")
        }

    @Subcommand("spawn")
    @CommandPermission("residential.town.set.spawn")
    @Description("Change the spawn of your town")
    fun setSpawn(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player

            val resident = ResidentService.get(player.uniqueId)

            if (resident?.town == null)
                throw GracefulCommandException("<red>You aren't in a town.")

            if (resident.getTownRoles().find { it.cmdMayor } == null)
                throw GracefulCommandException("<red>You do not have mayor permissions in this town.")

            val chunk = ChunkUtil.chunkToString(player.chunk)
            val location = LocationUtil.locationToString(player.location)

            TownService.setSpawn(resident.town, chunk, location)
            MessageUtil.send(player, "<dark_green>Set spawn location")
        }

    @Subcommand("mayor")
    @Syntax("[name]")
    @CommandPermission("residential.town.set.mayor")
    @Description("Change the mayor of your town")
    fun setMayor(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) { TODO() }

    @Subcommand("tag")
    @Syntax("[tag]")
    @CommandPermission("residential.town.set.tag")
    @Description("Change the tag of your town")
    fun setTag(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) { TODO() }
}