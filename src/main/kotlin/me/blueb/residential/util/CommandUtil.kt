package me.blueb.residential.util

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player

@Suppress("UnstableApiUsage")
class CommandUtil {
    companion object {
        fun ensurePlayer(ctx: CommandContext<CommandSourceStack>): Boolean {
            if (ctx.source.executor !is Player) {
                ctx.source.sender.sendMessage("Only players can run this command without arguments.")
                return false
            }
            return true
        }
    }
}