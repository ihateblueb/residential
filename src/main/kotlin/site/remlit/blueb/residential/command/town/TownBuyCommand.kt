package site.remlit.blueb.residential.command.town

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Conditions
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.remlit.blueb.residential.service.ConfirmationService
import site.remlit.blueb.residential.service.ResidentService
import site.remlit.blueb.residential.util.MessageUtil
import site.remlit.blueb.residential.util.inline.safeCommand

@CommandAlias("town|t")
@Subcommand("buy")
@Conditions("isPlayer|inTown|cmdMayor")
@CommandPermission("residential.town.buy")
class TownBuyCommand : BaseCommand() {
    @Subcommand("chunks")
    fun chunks(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val resident = ResidentService.get(player.uniqueId)

            val confirmation = ConfirmationService.create(run {
                MessageUtil.send(sender, "<dark_green>Successfully bought 1 extra chunk for your town.")
            })
        }
}