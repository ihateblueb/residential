package site.remlit.blueb.residential.command.town

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Conditions
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.remlit.blueb.residential.Commands
import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.util.inline.safeCommand
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.service.ChunkService
import site.remlit.blueb.residential.service.ResidentService
import site.remlit.blueb.residential.service.TaxService
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.ChunkUtil
import site.remlit.blueb.residential.util.LocationUtil
import site.remlit.blueb.residential.util.MessageUtil

@CommandAlias("town|t")
@CommandPermission("residential.town")
@Description("Commands for managing towns")
class TownCommand : BaseCommand() {
    // todo: for town and the like: you can input nonsense and it just goes to your town

    @Default
    @Syntax("<town>")
    @Conditions("isPlayer")
    @CommandCompletion("@towns")
    @Description("Get information about a town")
    fun default(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player

            val resident = ResidentService.get(player.uniqueId)
            val town = if (args.getOrNull(0) != null) TownService.getByName(args[0]) else resident?.getTown()

            if (town == null)
                throw GracefulCommandException("<red>You aren't in a town, please specify one.")

            val founder = Residential.instance.server.getPlayer(town.founder)
            val mayor = town.getMayor()?.getPlayer()

            val allClaimedChunks = ChunkService.getAllClaimedChunks(town.uuid)

            val div = MessageUtil.bullet()

            MessageUtil.send(player, MessageUtil.createLine(town.name))
            MessageUtil.send(player, "Founded ${MessageUtil.formatLocalDateTime(town.foundedAt)}${if (founder != null) " by ${founder.name}" else ""}")
            MessageUtil.send(player, "Mayor: ${mayor?.name} $div Balance: ${Residential.economy.format(town.balance)}")
            MessageUtil.send(player, "Residents: ${town.getResidentCount()} $div Claimed ${allClaimedChunks.size}/${town.getMaxChunks()}")
            MessageUtil.send(player, "Tax: ${Residential.economy.format(TaxService.town.calculateTax(town.uuid))} $div Resident Tax ${TaxService.town.getBaseTaxForResidents(town.uuid)}")
            MessageUtil.send(player, "Open: ${MessageUtil.formatBoolean(town.open, true)} $div PVP: ${MessageUtil.formatBoolean(town.pvp, true)} $div Mobs: ${MessageUtil.formatBoolean(town.mobs, true)} $div Fire: ${MessageUtil.formatBoolean(town.fire, true)}")
            MessageUtil.send(player, MessageUtil.createLine())
        }

    @Subcommand("new")
    @Syntax("<town>")
    @Conditions("isPlayer|notInTown")
    @CommandPermission("residential.town.new")
    @Description("Create a new town")
    fun new(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player

            val name = args.getOrNull(0)

            // TODO: min distance between claimed areas

            if (name.isNullOrBlank())
                throw GracefulCommandException("<red>Town name cannot be blank.")

            if (name.length > Configuration.config.town.name.maxLength)
                throw GracefulCommandException("<red>Town name cannot be longer than ${Configuration.config.town.name.maxLength} characters.")

            val balance = Residential.economy.getBalance(player)
            if (balance < Configuration.config.town.cost)
                throw GracefulCommandException("<red>You cannot afford creating a town, which costs ${Residential.economy.format(Configuration.config.town.cost.toDouble())}.")

            val homeChunk = player.chunk
            TownService.register(name, player.uniqueId, ChunkUtil.chunkToString(homeChunk), player.world.name, LocationUtil.locationToString(player.location))
        }

    @Subcommand("spawn")
    @Syntax("<town>")
    @Conditions("isPlayer") // todo: spawn perm check
    @CommandCompletion("@towns")
    @CommandPermission("residential.town.spawn")
    @Description("Teleport to the spawn of a town")
    fun spawn(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val resident = ResidentService.get(player.uniqueId)
            val town = if (args.getOrNull(0) != null) TownService.getByName(args[0]) else resident?.getTown()

            if (town == null)
                throw GracefulCommandException("<red>You aren't in a town, please specify one.")

            TownService.teleport(town.uuid, player.uniqueId)
        }

    @Subcommand("join")
    @Syntax("<town>")
    @Conditions("isPlayer|notInTown")
    @CommandCompletion("@towns")
    @CommandPermission("residential.town.join")
    @Description("Join a town")
    fun join(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val resident = ResidentService.get(player.uniqueId)!!

            val townName = args.getOrNull(0)
            if (townName == null)
                throw GracefulCommandException("<red>You must specify a town.")

            val town = TownService.getByName(townName)
            if (town == null)
                throw GracefulCommandException("<red>Town doesn't exist.")

            if (!town.open)
                throw GracefulCommandException("<red>${town.name} isn't open to join. Ask for an invite.")

            ResidentService.joinTown(resident.uuid, town.uuid)
        }

    @Subcommand("claim")
    @Conditions("isPlayer|inTown|cmdPlotManagement")
    @CommandPermission("residential.town.claim")
    @Description("Claim the current chunk you're standing in for you town")
    fun claim(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val resident = ResidentService.get(player.uniqueId)!!

            val chunk = ChunkUtil.chunkToString(player.chunk)

            ChunkService.claim(resident.town!!, chunk)
            MessageUtil.send(player, "<dark_green>Claimed chunk at $chunk")
        }

    @Subcommand("residents")
    @Syntax("<town>")
    @Conditions("isPlayer")
    @CommandCompletion("@towns")
    @CommandPermission("residential.town.residents")
    @Description("List all residents of a town")
    fun residents(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val resident = ResidentService.get(player.uniqueId)
            val town = if (args.getOrNull(0) != null) TownService.getByName(args[0]) else resident?.getTown()

            if (town == null)
                throw GracefulCommandException("<red>You aren't in a town, please specify one.")

            val residents = TownService.getResidents(town.uuid)

            MessageUtil.send(sender, MessageUtil.createLine(center = "${town.name} Residents (${residents.size})"))

            var skipFor = 0
            for (resident in residents) {
                if (skipFor == 0) {
                    val index = residents.indexOf(resident)

                    var message = resident.getPlayer().name
                    if (residents.getOrNull(index + 1) != null) {
                        message += ", ${residents[index + 1].getPlayer().name}"
                        skipFor++
                    }
                    if (residents.getOrNull(index + 2) != null) {
                        message += ", ${residents[index + 2].getPlayer().name}"
                        skipFor++
                    }

                    MessageUtil.send(player, message)
                } else {
                    skipFor--
                }
            }
        }

    @Subcommand("delete")
    @Conditions("isPlayer|inTown|isMayor")
    @CommandPermission("residential.town.delete")
    @Description("Delete your town")
    fun delete(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) { TODO() }

    @Subcommand("invite")
    @Syntax("[player]")
    @Conditions("isPlayer|inTown") // todo: canInvite
    @CommandPermission("residential.town.invite")
    @Description("Invite a player to your town")
    fun invite(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) { TODO() }

    @Subcommand("announce")
    @Syntax("[message]")
    @Conditions("isPlayer|inTown|announce")
    @CommandPermission("residential.town.announce")
    @Description("Send an announcement to the town's residents")
    fun announce(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val resident = ResidentService.get(player.uniqueId)!!

            val message = args.joinToString(" ")
            if (message.isBlank())
                throw GracefulCommandException("<red>You can't send a blank announcement.")

            TownService.announce(resident.town!!, message)
            MessageUtil.send(sender, "<dark_green>Sent announcement.")
        }

    init {
        Commands.commandManager.commandCompletions.registerCompletion("towns") {
            TownService.getAllNames()
        }
    }
}