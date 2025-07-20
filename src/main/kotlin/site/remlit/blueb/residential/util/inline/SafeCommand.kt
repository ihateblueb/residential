package site.remlit.blueb.residential.util.inline

import org.bukkit.command.CommandSender
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.util.ExceptionUtil
import site.remlit.blueb.residential.util.MessageUtil

inline fun safeCommand(sender: CommandSender, block: () -> Unit) =
    try { block() } catch(e: Throwable) {
        if (e is GracefulCommandException)
            MessageUtil.send(sender, if (e.message != null) "<red>${e.message}" else "<red>Something went wrong with this command.")
        else {
            MessageUtil.send(sender, "<red>Something unexpected went wrong with this command.")
            ExceptionUtil.createReport("safeCommand", e)
        }
    }