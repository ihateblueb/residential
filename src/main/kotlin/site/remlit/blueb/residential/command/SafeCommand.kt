package site.remlit.blueb.residential.command

import org.bukkit.command.CommandSender
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.util.ExceptionUtil
import site.remlit.blueb.residential.util.MessageUtil

inline fun safeCommand(sender: CommandSender, block: () -> Unit) =
    try { block() } catch(e: Exception) {
        if (e is GracefulCommandException)
            MessageUtil.send(sender, e.message ?: "Something went wrong with this command.")
        else {
            MessageUtil.send(sender, "Something unexpected went wrong with this command.")
            ExceptionUtil.createReport("safeCommand", e)
        }
    }