package site.remlit.blueb.residential.command.town

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import org.bukkit.command.CommandSender
import site.remlit.blueb.residential.Commands
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.inline.safeCommand

@CommandAlias("town|t")
@Subcommand("role")
@CommandPermission("residential.town.role")
class TownRoleCommand : BaseCommand() {
    @Subcommand("create")
    @Syntax("[role]")
    @CommandPermission("residential.town.role.create")
    fun create(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) { TODO() }

    @Subcommand("delete")
    @Syntax("[role]")
    @CommandPermission("residential.town.role.delete")
    fun delete(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) { TODO() }

    @Subcommand("set")
    @Syntax("[role] [property] <value>")
    @CommandCompletion("@town_roles @town_role_properties")
    @CommandPermission("residential.town.role.set")
    fun set(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val roleName = args.getOrNull(0)
            val property = args.getOrNull(1)
            val value = args.getOrNull(2)

            when (property) {
                "name" -> TODO()
                "destroy" -> TODO()
                "place" -> TODO()
                "use" -> TODO()
                "spawn" -> TODO()
                "bank_withdraw" -> TODO()
                "bank_deposit" -> TODO()
                "announce" -> TODO()
                "plot_management" -> TODO()
                "cmd_mayor" -> TODO()
                else -> throw GracefulCommandException("Unknown role property $property.")
            }
        }

    init {
        Commands.commandManager.commandCompletions.registerCompletion("town_roles") {
            listOf("outsider", "resident", "mayor")
        }

        Commands.commandManager.commandCompletions.registerCompletion("town_role_properties") {
            listOf("name", "destroy", "place", "use", "spawn", "bank_withdraw", "bank_deposit", "announce", "cmd_plot_management", "cmd_mayor")
        }
    }
}