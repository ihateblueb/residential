package site.remlit.blueb.residential.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender
import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.service.ConfirmationService
import site.remlit.blueb.residential.util.MessageUtil
import site.remlit.blueb.residential.util.inline.safeCommand
import java.util.UUID

@CommandAlias("residential|rd")
class ResidentialCommand : BaseCommand() {
    @Default
    fun default(sender: CommandSender) =
        safeCommand(sender) {
            MessageUtil.send(sender, "Running Residential ${Residential.instance.pluginMeta.version} on ${when (Configuration.config.database) {
                "sqlite" -> "SQLite"
                "postgres" -> "PostgreSQL"
                "mysql" -> "MySQL"
                else -> "Unknown Database"
            }}")
        }

    @Subcommand("confirm")
    fun confirm(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val uuid = args.getOrNull(0)

            if (uuid == null)
                throw GracefulCommandException("You must specify a UUID.")

            ConfirmationService.confirm(UUID.fromString(uuid))
        }

    @Subcommand("cancel")
    fun cancel(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val uuid = args.getOrNull(0)

            if (uuid == null)
                throw GracefulCommandException("You must specify a UUID.")

            ConfirmationService.cancel(UUID.fromString(uuid))

            MessageUtil.send(sender, "<yellow>Cancelled action.")
        }
}