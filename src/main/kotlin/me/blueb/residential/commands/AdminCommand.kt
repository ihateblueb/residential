package me.blueb.residential.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.blueb.residential.ResidentialDatabase.Companion.connection
import net.kyori.adventure.text.minimessage.MiniMessage
import kotlin.use

@Suppress("UnstableApiUsage", "SameReturnValue")
class AdminCommand {
    companion object {
        fun create(): LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("admin")
            .requires { sender -> sender.sender.hasPermission("residential.admin") }
            .then(Commands.literal("debug")
                .then(Commands.literal("database")
                    .then(
                        Commands.literal("getSchemaVersion")
                            .executes { ctx -> runDebugDatabaseGetSchemaVersion(ctx) }
                    )
                )
            )

        private fun runDebugDatabaseGetSchemaVersion(ctx: CommandContext<CommandSourceStack>): Int {
            connection.createStatement().use { stmt ->
                val version = stmt.executeQuery("SELECT version FROM database_meta WHERE id = 'r'").use { rs ->
                    rs.getInt("version")
                }

                ctx.source.sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Current database schema version is $version"))
            }

            return Command.SINGLE_SUCCESS
        }
    }
}