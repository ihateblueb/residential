package me.blueb.residential.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component

@Suppress("UnstableApiUsage")
class DebugCommand {
    companion object {
        fun create(): LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("debug")
            .executes { ctx -> run(ctx) }
            .then(
                Commands.literal("sql")
                    .then(
                        Commands.argument("sql", StringArgumentType.string())
                            .executes { ctx -> runSql(ctx) }
                    )
            )

        private fun run(ctx: CommandContext<CommandSourceStack>): Int {
            ctx.source.sender.sendMessage(Component.text("Argument required!"))
            return Command.SINGLE_SUCCESS
        }

        private fun runSql(ctx: CommandContext<CommandSourceStack>): Int {
            val sql = ctx.getArgument("section", String::class.java)

            // TODO

            return Command.SINGLE_SUCCESS
        }
    }
}