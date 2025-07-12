package site.remlit.blueb.residential.command.town

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender

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
    fun setSpawn(sender: CommandSender, args: Array<String>) { TODO() }
}