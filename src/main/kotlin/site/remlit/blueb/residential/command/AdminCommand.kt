package site.remlit.blueb.residential.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender
import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.util.MessageUtil
import site.remlit.blueb.residential.util.inline.safeCommand

@CommandAlias("residential|rd")
@Subcommand("admin")
@CommandPermission("residential.admin")
class AdminCommand : BaseCommand() {
    @Subcommand("reload")
    fun reload(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            Configuration.load()
            MessageUtil.send(sender, "<dark_green>Reloaded configuration.")
        }
}