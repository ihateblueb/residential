package me.blueb.residential.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.blueb.residential.service.ResidentService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

@Suppress("UnstableApiUsage")
class TownCommand {
    companion object {
        fun create(): LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("town")
            .executes { ctx -> run(ctx) }

        private fun run(ctx: CommandContext<CommandSourceStack>): Int {
            ctx.source.sender.sendMessage(Component.text("hello from /town!"))

            if (ctx.source.executor !is Player) {
                ctx.source.sender.sendMessage("Only players can run this command without arguments.")
                return Command.SINGLE_SUCCESS
            }

            val player = ctx.source.executor as Player

            val residentTown = ResidentService.getTown(player.uniqueId)

            player.sendMessage(Component.text(if (residentTown != null) "Town: $residentTown" else "You aren't apart of a town."))

            return Command.SINGLE_SUCCESS
        }
    }
}