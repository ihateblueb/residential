package site.remlit.blueb.residential.util.inline

import org.bukkit.command.CommandSender
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.util.ExceptionUtil
import site.remlit.blueb.residential.util.MessageUtil

/**
 * Executes a block safely and creates an exception report if it fails.
 * */
inline fun safe(task: String, block: () -> Unit) =
    try { block() } catch(e: Throwable) {
        ExceptionUtil.createReport("safe:$task", e)
    }

/**
 * Executes a block safely and creates an exception report and disable the plugin if it fails.
 * */
inline fun safeStart(task: String, block: () -> Unit) =
    try { block() } catch(e: Throwable) {
        ExceptionUtil.createReport("safeStart:$task", e)
        Residential.instance.server.pluginManager.disablePlugin(Residential.instance)
    }

/**
 * Executes a command safely and creates an exception report and messages the sender if it fails.
 * */
inline fun safeCommand(sender: CommandSender, block: () -> Unit) =
    try { block() } catch(e: Throwable) {
        if (e is GracefulCommandException)
            MessageUtil.send(sender, if (e.message != null) "<red>${e.message}" else "<red>Something went wrong with this command.")
        else {
            MessageUtil.send(sender, "<red>Something unexpected went wrong with this command.")
            ExceptionUtil.createReport("safeCommand", e)
        }
    }