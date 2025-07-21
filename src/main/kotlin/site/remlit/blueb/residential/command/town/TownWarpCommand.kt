package site.remlit.blueb.residential.command.town

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Conditions
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender
import site.remlit.blueb.residential.util.inline.safeCommand

@CommandAlias("town|t")
@Subcommand("warp")
@CommandPermission("residential.town.warp")
class TownWarpCommand : BaseCommand() {
    @Default
    @Conditions("isPlayer|inTown") // todo: warpUse, warpManage
    @CommandPermission("residential.town.warp")
    fun warp(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) { TODO() }

    @Subcommand("create")
    @Conditions("isPlayer|inTown")
    @CommandPermission("residential.town.warp.create")
    fun warpCreate(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) { TODO() }

    @Subcommand("delete")
    @Conditions("isPlayer|inTown")
    @CommandPermission("residential.town.warp.delete")
    fun warpDelete(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) { TODO() }
}