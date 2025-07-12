package site.remlit.blueb.residential.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.remlit.blueb.residential.service.ChunkService
import site.remlit.blueb.residential.util.ChunkUtil
import site.remlit.blueb.residential.util.MessageUtil

@CommandAlias("residential|rd")
@Subcommand("debug")
@CommandPermission("residential.debug")
class DebugCommand : BaseCommand() {
    @Subcommand("chunk")
    fun chunk(sender: CommandSender, args: Array<String>) {
        val player = sender as Player

        val chunk = ChunkService.get(args.getOrNull(0) ?: ChunkUtil.chunkToString(player.chunk), player.world.name)

        MessageUtil.send(player, "<dark_aqua>Debug (Chunk@${chunk?.location})")
        MessageUtil.send(player, "<gold>World: <yellow>${chunk?.world}")
        MessageUtil.send(player, "<gold>Town: <yellow>${chunk?.town}")
        MessageUtil.send(player, "<gold>Plot: <yellow>${chunk?.plot}")
    }
}