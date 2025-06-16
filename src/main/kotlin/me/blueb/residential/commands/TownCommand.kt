package me.blueb.residential.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.blueb.residential.Residential
import me.blueb.residential.ResidentialConfig
import me.blueb.residential.services.ChunkService
import me.blueb.residential.services.ResidentService
import me.blueb.residential.util.ChunkUtil
import me.blueb.residential.util.CommandUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.milkbowl.vault.economy.Economy
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

        private fun run(ctx: CommandContext<CommandSourceStack>): Int {
            if (!CommandUtil.ensurePlayer(ctx)) return Command.SINGLE_SUCCESS

            val player = ctx.source.executor as Player

            val resident = ResidentService.get(player.uniqueId)
            val townUuid = resident?.town

            if (townUuid == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You aren't in a town."))
                return Command.SINGLE_SUCCESS
            }

            player.sendMessage(Component.text("Town: $townUuid"))

            return Command.SINGLE_SUCCESS
        }

        private fun runNew(ctx: CommandContext<CommandSourceStack>): Int {
            if (!CommandUtil.ensurePlayer(ctx)) return Command.SINGLE_SUCCESS
            val player = ctx.source.executor as Player

            val name = ctx.getArgument("name", String::class.java)

            println(ChunkUtil.chunkToString(player.chunk))

            val chunk = ChunkService.get(ChunkUtil.chunkToString(player.chunk))

            if (chunk != null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>This chunk is already claimed."))
                return Command.SINGLE_SUCCESS
            }

            // TODO: min distance between claimed areas

            if (name.isNullOrBlank()) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Town name cannot be blank."))
                return Command.SINGLE_SUCCESS
            }

            if (name.length > ResidentialConfig.config.town.name.maxLength) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Town name cannot be longer than ${ResidentialConfig.config.town.name.maxLength} characters."))
                return Command.SINGLE_SUCCESS
            }

            val balance = Residential.economy.getBalance(player)

            if (balance < ResidentialConfig.config.town.cost) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You cannot afford creating a town, which costs ${Residential.economy.format(ResidentialConfig.config.town.cost.toDouble())}."))
                return Command.SINGLE_SUCCESS
            }

            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Supposed to create town: $name."))

            return Command.SINGLE_SUCCESS
        }
    }
}