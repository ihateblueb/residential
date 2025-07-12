package site.remlit.blueb.residential.command.town

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender

@CommandAlias("town|t")
@Subcommand("warp")
@CommandPermission("residential.town.warp")
class TownWarpCommand : BaseCommand() {
    // todo: ensure resident has perm, town has public warps, warp is public
    @Default
    @CommandPermission("residential.town.warp")
    fun warp(sender: CommandSender, args: Array<String>) {} // teleport to warp

    @Subcommand("create")
    @CommandPermission("residential.town.warp.create")
    fun warpCreate(sender: CommandSender, args: Array<String>) {}

    @Subcommand("delete")
    @CommandPermission("residential.town.warp.delete")
    fun warpDelete(sender: CommandSender, args: Array<String>) {}
}