package site.remlit.blueb.residential.command.town

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Conditions
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.service.ConfirmationService
import site.remlit.blueb.residential.service.ResidentService
import site.remlit.blueb.residential.service.TownService
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
            val resident = ResidentService.get(player.uniqueId)!!
            val town = resident.getTown()!!

            val amount = args.getOrNull(0)?.toInt() ?: 1
            val cost = (Configuration.config.town.claimableChunks.cost * amount)
            if (town.balance < cost)
                throw GracefulCommandException("Your town cannot afford this.")

            val confirmation = ConfirmationService.create {
                TownService.buyExtraChunks(town.uuid, amount)
                MessageUtil.send(sender, "<dark_green>Successfully bought $amount extra chunks for your town.")
            }

            ConfirmationService.sendConfirmationMessage(sender, confirmation, "<yellow><gold>$amount</gold> extra chunks will cost <gold>${Residential.economy.format(cost.toDouble())}</gold>.")
        }
}