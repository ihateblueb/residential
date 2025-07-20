package site.remlit.blueb.residential.command.town

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand

@CommandAlias("town|t")
@Subcommand("role")
@CommandPermission("residential.town.role")
class TownRoleCommand : BaseCommand() {

}