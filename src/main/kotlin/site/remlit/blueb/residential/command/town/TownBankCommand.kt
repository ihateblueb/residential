package site.remlit.blueb.residential.command.town

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.service.ResidentService
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.MessageUtil
import site.remlit.blueb.residential.util.inline.safeCommand

@CommandAlias("town|t")
@Subcommand("bank")
@CommandPermission("residential.town.bank")
@Description("Commands for managing town banks")
class TownBankCommand : BaseCommand() {
    @Default
    fun default(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val resident = ResidentService.get(player.uniqueId)

            if (resident?.town == null)
                throw GracefulCommandException("<red>You aren't in a town.")

            val town = TownService.get(resident.town)!!

            MessageUtil.send(sender, "Current town balance is ${Residential.economy.format(town.balance)}.")
        }

    @Subcommand("deposit")
    @Syntax("[amount]")
    @CommandPermission("residential.town.deposit")
    @Description("Deposit money to your town bank")
    fun deposit(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val resident = ResidentService.get(player.uniqueId)

            if (resident?.town == null)
                throw GracefulCommandException("<red>You aren't in a town.")

            if (resident.getTownRoles().find { it.bankDeposit || it.cmdMayor } == null)
                throw GracefulCommandException("<red>You do not have bank deposit permissions in this town.")

            val amount = args.getOrNull(0)?.toDoubleOrNull()

            if (amount == null)
                throw GracefulCommandException("<red>You must specify an amount.")

            if (amount < 0.01)
                throw GracefulCommandException("<red>You must deposit at least ${Residential.economy.format(0.01)}.")

            if (Residential.economy.getBalance(player) < amount)
                throw GracefulCommandException("<red>You don't have enough money.")

            if (Residential.economy.withdrawPlayer(player, amount).type != EconomyResponse.ResponseType.SUCCESS)
                throw GracefulCommandException("<red>Failed to withdraw from your account.")

            TownService.deposit(resident.town, amount)
        }

    @Subcommand("withdraw")
    @Syntax("[amount]")
    @CommandPermission("residential.town.withdraw")
    @Description("Withdraw money from your town bank")
    fun withdraw(sender: CommandSender, args: Array<String>) =
        safeCommand(sender) {
            val player = sender as Player
            val resident = ResidentService.get(player.uniqueId)

            if (resident?.town == null)
                throw GracefulCommandException("<red>You aren't in a town.")

            if (resident.getTownRoles().find { it.bankWithdraw || it.cmdMayor } == null)
                throw GracefulCommandException("<red>You do not have bank withdraw permissions in this town.")

            val amount = args.getOrNull(0)?.toDoubleOrNull()

            if (amount == null)
                throw GracefulCommandException("<red>You must specify an amount.")

            if (amount < 0.01)
                throw GracefulCommandException("<red>You must withdraw at least ${Residential.economy.format(0.01)}.")

            val town = TownService.get(resident.town)!!
            if (town.balance < amount)
                throw GracefulCommandException("<red>Your town doesn't have enough money.")

            if (Residential.economy.depositPlayer(player, amount).type != EconomyResponse.ResponseType.SUCCESS)
                throw GracefulCommandException("<red>Failed to deposit to your account.")

            TownService.withdraw(resident.town, amount)
        }
}