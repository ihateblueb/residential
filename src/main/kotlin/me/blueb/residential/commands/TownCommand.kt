package me.blueb.residential.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.blueb.residential.Residential
import me.blueb.residential.ResidentialConfig
import me.blueb.residential.models.GracefulCommandException
import me.blueb.residential.services.ResidentService
import me.blueb.residential.services.TownService
import me.blueb.residential.util.ChunkUtil
import me.blueb.residential.util.CommandUtil
import me.blueb.residential.util.LocationUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player

@Suppress("UnstableApiUsage", "SameReturnValue")
class TownCommand {
    companion object {
        fun create(): LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("town")
            .executes { ctx -> run(ctx) }
            .then(Commands.literal("new")
                .requires { sender -> sender.sender.hasPermission("residential.town.new") }
                .then(Commands.argument("name", StringArgumentType.string())
                    .executes { ctx -> runNew(ctx) }
                )
            )
            .then(Commands.literal("spawn")
                .requires { sender -> sender.sender.hasPermission("residential.town.spawn") }
                .executes { ctx -> runSpawn(ctx) }
                .then(Commands.argument("name", StringArgumentType.string())
                    .executes { ctx -> runSpawnSpecific(ctx) }
                )
            )

        private fun run(ctx: CommandContext<CommandSourceStack>): Int {
            if (!CommandUtil.ensurePlayer(ctx)) return Command.SINGLE_SUCCESS

            val player = ctx.source.executor as Player

            val resident = ResidentService.get(player.uniqueId)
            val townUuid = resident?.town
            if (townUuid == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You aren't in a town."))
                return Command.SINGLE_SUCCESS
            }

            val town = TownService.get(townUuid)
            if (town == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Town couldn't be found."))
                return Command.SINGLE_SUCCESS
            }

            player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_gray> -- <yellow>town<dark_gray> --"))
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Founded at ${town.foundedAt} by ${town.founder}"))
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Spawn: ${town.spawn} Home: ${town.homeChunk}"))
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Abandoned: ${town.abandoned} Nation: ${town.nation}"))

            return Command.SINGLE_SUCCESS
        }

        private fun runSpawn(ctx: CommandContext<CommandSourceStack>): Int {
            if (!CommandUtil.ensurePlayer(ctx)) return Command.SINGLE_SUCCESS
            val player = ctx.source.sender as Player
            val resident = ResidentService.get(player.uniqueId)

            if (resident?.town == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You aren't in a town, please specify one to teleport to."))
                return Command.SINGLE_SUCCESS
            }

            TownService.teleport(resident.town, player.uniqueId)

            return Command.SINGLE_SUCCESS
        }

        private fun runSpawnSpecific(ctx: CommandContext<CommandSourceStack>): Int {
            if (!CommandUtil.ensurePlayer(ctx)) return Command.SINGLE_SUCCESS
            val player = ctx.source.executor as Player

            val name = ctx.getArgument("name", String::class.java)

            val town = TownService.getByName(name)

            if (town == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Town doesn't exist."))
                return Command.SINGLE_SUCCESS
            }

            TownService.teleport(town.uuid, player.uniqueId)

            return Command.SINGLE_SUCCESS
        }

        private fun runNew(ctx: CommandContext<CommandSourceStack>): Int {
            if (!CommandUtil.ensurePlayer(ctx)) return Command.SINGLE_SUCCESS
            val player = ctx.source.executor as Player

            val name = ctx.getArgument("name", String::class.java)

            // TODO: min distance between claimed areas

            if (name.isNullOrBlank()) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Town name cannot be blank."))
                return Command.SINGLE_SUCCESS
            }

            if (name.length > ResidentialConfig.config.town!!.name.maxLength) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Town name cannot be longer than ${ResidentialConfig.config.town!!.name.maxLength} characters."))
                return Command.SINGLE_SUCCESS
            }

            val balance = Residential.economy.getBalance(player)

            if (balance < ResidentialConfig.config.town!!.cost) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You cannot afford creating a town, which costs ${Residential.economy.format(ResidentialConfig.config.town!!.cost.toDouble())}."))
                return Command.SINGLE_SUCCESS
            }

            val homeChunk = player.chunk

            try {
                TownService.register(name, player.uniqueId, ChunkUtil.chunkToString(homeChunk), player.world.name, LocationUtil.locationToString(player.location))
            } catch (e: GracefulCommandException) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>${e.message}"))
                return Command.SINGLE_SUCCESS
            }

            return Command.SINGLE_SUCCESS
        }
    }
}