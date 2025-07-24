package site.remlit.blueb.residential.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender
import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.service.InboxService
import site.remlit.blueb.residential.service.ResidentService
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

    @Subcommand("announce")
    fun announce(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val message = args.joinToString(" ")

            if (message.isBlank())
                throw GracefulCommandException("Announcement cannot be blank.")

            val residents = ResidentService.getAll()
            for (resident in residents) {
                InboxService.sendFromSystem(resident.uuid, message)
                MessageUtil.send(sender, "<dark_green>Sent announcement to ${resident.getPlayer().name}.")
            }

            MessageUtil.send(sender, "<dark_green>Sent announcement.")
        }
}