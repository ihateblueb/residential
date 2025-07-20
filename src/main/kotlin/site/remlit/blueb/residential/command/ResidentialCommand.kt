package site.remlit.blueb.residential.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import org.bukkit.command.CommandSender
import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.util.MessageUtil
import site.remlit.blueb.residential.util.inline.safeCommand

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
}