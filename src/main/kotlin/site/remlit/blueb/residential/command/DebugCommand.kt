package site.remlit.blueb.residential.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.remlit.blueb.residential.Clock
import site.remlit.blueb.residential.service.ChunkService
import site.remlit.blueb.residential.service.ResidentService
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.ChunkUtil
import site.remlit.blueb.residential.util.MessageUtil
import site.remlit.blueb.residential.util.UuidUtil
import java.util.UUID

@CommandAlias("residential|rd")
@Subcommand("debug")
@CommandPermission("residential.debug")
class DebugCommand : BaseCommand() {
    private fun debugHeader(type: String, at: String) =
        "<dark_aqua>Debug ($type@$at)"

    @Subcommand("clock")
    fun clock(sender: CommandSender, args: Array<String>) {
        val player = sender as Player

        MessageUtil.send(player, debugHeader("Clock", "server"))
        MessageUtil.send(player, "<gold>Current state: <yellow>${Clock.clockState}/${Clock.maxClockState}")
        MessageUtil.send(player, "<gold>In minutes: <yellow>${Clock.clockState * Clock.clockInterval}")
    }

    @Subcommand("chunk")
    fun chunk(sender: CommandSender, args: Array<String>) {
        val player = sender as Player

        val chunk = ChunkService.get(args.getOrNull(0) ?: ChunkUtil.chunkToString(player.chunk))

        MessageUtil.send(player, debugHeader("Chunk", "${chunk?.location}"))
        MessageUtil.send(player, "<gold>Town: <yellow>${chunk?.town}")
        MessageUtil.send(player, "<gold>Plot: <yellow>${chunk?.plot}")
    }

    @Subcommand("town")
    fun town(sender: CommandSender, args: Array<String>) {
        val player = sender as Player

        val townUuid = UuidUtil.fromStringOrNull(args.getOrNull(0)) ?: ResidentService.get(player.uniqueId)?.town
        val town = TownService.getByName(args.getOrNull(0) ?: "") ?: TownService.get(townUuid ?: UUID.randomUUID())

        MessageUtil.send(player, debugHeader("Town", "${town?.uuid}"))
        MessageUtil.send(player, "<yellow>${town}")
    }

    @Subcommand("resident")
    fun resident(sender: CommandSender, args: Array<String>) {
        val player = sender as Player

        val residentUuid = UuidUtil.fromStringOrNull(args.getOrNull(0)) ?: player.uniqueId
        val resident = ResidentService.get(residentUuid)

        MessageUtil.send(player, debugHeader("Resident", "${resident?.uuid}"))
        MessageUtil.send(player, "<yellow>${resident}")
    }
}