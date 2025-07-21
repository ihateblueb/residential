package site.remlit.blueb.residential.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.remlit.blueb.residential.Clock
import site.remlit.blueb.residential.Commands
import site.remlit.blueb.residential.Database
import site.remlit.blueb.residential.event.NewDayEvent
import site.remlit.blueb.residential.model.FireworkType
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.service.ChunkService
import site.remlit.blueb.residential.service.ResidentService
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.ChunkUtil
import site.remlit.blueb.residential.util.FireworkUtil
import site.remlit.blueb.residential.util.LocationUtil
import site.remlit.blueb.residential.util.MessageUtil
import site.remlit.blueb.residential.util.UuidUtil
import site.remlit.blueb.residential.util.inline.safeCommand
import java.util.UUID

@CommandAlias("residential|rd")
@Subcommand("debug")
@CommandPermission("residential.debug")
class DebugCommand : BaseCommand() {
    private fun debugHeader(type: String, at: String) =
        "<dark_aqua>Debug ($type@$at)"

    @Subcommand("clock")
    fun clock(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player

            MessageUtil.send(player, debugHeader("Clock", "server"))
            MessageUtil.send(player, "<gold>Current state: <yellow>${Clock.clockState}/${Clock.maxClockState}")
            MessageUtil.send(player, "<gold>In minutes: <yellow>${Clock.clockState * Clock.clockInterval}")
        }

    @Subcommand("chunk")
    fun chunk(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player

            val chunk = ChunkService.get(args.getOrNull(0) ?: ChunkUtil.chunkToString(player.chunk))

            MessageUtil.send(player, debugHeader("Chunk", "${chunk?.location}"))
            MessageUtil.send(player, "<gold>Town: <yellow>${chunk?.town}")
            MessageUtil.send(player, "<gold>Plot: <yellow>${chunk?.plot}")
        }

    @Subcommand("town")
    fun town(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player

            val townUuid = UuidUtil.fromStringOrNull(args.getOrNull(0)) ?: ResidentService.get(player.uniqueId)?.town
            val town = TownService.getByName(args.getOrNull(0) ?: "") ?: TownService.get(townUuid ?: UUID.randomUUID())

            MessageUtil.send(player, debugHeader("Town", "${town?.uuid}"))
            MessageUtil.send(player, "<yellow>${town}")
        }

    @Subcommand("resident")
    fun resident(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player

            val residentUuid = UuidUtil.fromStringOrNull(args.getOrNull(0)) ?: player.uniqueId
            val resident = ResidentService.get(residentUuid)

            MessageUtil.send(player, debugHeader("Resident", "${resident?.uuid}"))
            MessageUtil.send(player, "<yellow>${resident}")
        }

    @Subcommand("firework")
    @CommandCompletion("@firework_type")
    fun firework(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player

            val arg = args.getOrNull(0)
            if (arg == null)
                throw GracefulCommandException("<red>You must provide a firework type.")

            val enum = FireworkType.valueOf(arg)
            val town = TownService.get(ResidentService.get(player.uniqueId)?.town!!)!!

            when (enum) {
                FireworkType.NEW_DAY -> FireworkUtil.spawnAt(
                    LocationUtil.stringToLocation(
                        town.spawn,
                        ChunkUtil.stringToChunk(town.homeChunk)!!.world.name
                    ), enum
                )

                else -> FireworkUtil.spawnAt(player.location, enum)
            }
        }

    @Subcommand("exception")
    fun exception(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            throw Exception("This is a test exception for debugging!")
        }

    @Subcommand("newday")
    fun newDay(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            Clock.clockState = Clock.maxClockState
            MessageUtil.send(sender, "<yellow>At next clock tick, a new day will occur.")
        }

    @Subcommand("newdayevent")
    fun newDayEvent(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            NewDayEvent().callEvent()
        }

    @Subcommand("dbsetup")
    fun dbSetup(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            Database.setup()
        }

    init {
        Commands.commandManager.commandCompletions.registerCompletion("firework_type") {
            FireworkType.toStringList()
        }
    }
}