package site.remlit.blueb.residential.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Conditions
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.service.InboxService
import site.remlit.blueb.residential.util.MessageUtil
import site.remlit.blueb.residential.util.inline.safeCommand
import java.util.UUID

@CommandAlias("inbox|rdi")
class InboxCommand : BaseCommand() {
    @Default
    @Conditions("isPlayer")
    fun default(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val messages = InboxService.getInbox(player.uniqueId)

            MessageUtil.send(sender, MessageUtil.createLine("Inbox (${messages.size})"))
            MessageUtil.send(sender, "<red>- <click:run_command:/inbox clear>Clear all messages</click>")
            // todo: stack and show send date
            for (message in messages) {
                val number = messages.indexOf(message) + 1
                MessageUtil.send(sender, "<red><hover:show_text:'<red>Delete message'><click:run_command:/inbox delete ${message.uuid}>✘</click></hover> <gray>${number}. <yellow>${message.sender}: <white>${message.message}")
            }
        }

    @Subcommand("delete")
    @Conditions("isPlayer")
    fun delete(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val messageUuid = args.getOrNull(0)

            if (messageUuid == null)
                throw GracefulCommandException("You must provide a UUID.")

            InboxService.delete(player.uniqueId, UUID.fromString(messageUuid))
            MessageUtil.send(sender, "<dark_green>Deleted message.")
        }

    @Subcommand("clear")
    @Conditions("isPlayer")
    fun clear(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            InboxService.clear(player.uniqueId)
            MessageUtil.send(sender, "<dark_green>Cleared inbox.")
        }
}