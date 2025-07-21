package site.remlit.blueb.residential.command.condition

import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.model.GracefulCommandException
import site.remlit.blueb.residential.service.ResidentService

class TownConditions {
    companion object {
        fun register() {
            Residential.commandManager.commandConditions.addCondition("inTown") { context ->
                val player = context.issuer.player
                val resident = ResidentService.get(player.uniqueId)

                if (resident?.town == null)
                    throw GracefulCommandException("<red>You aren't in a town.")
            }

            Residential.commandManager.commandConditions.addCondition("notInTown") { context ->
                val player = context.issuer.player
                val resident = ResidentService.get(player.uniqueId)

                if (resident?.town != null)
                    throw GracefulCommandException("<red>You're already in a town.")
            }

            // town roles

            Residential.commandManager.commandConditions.addCondition("bankWithdraw") { context ->
                val player = context.issuer.player
                val resident = ResidentService.get(player.uniqueId)!!
                if (resident.getTownRoles().find { it.bankDeposit || it.cmdMayor } == null)
                    throw GracefulCommandException("<red>You don't have bank withdraw permissions in this town.")
            }

            Residential.commandManager.commandConditions.addCondition("bankDeposit") { context ->
                val player = context.issuer.player
                val resident = ResidentService.get(player.uniqueId)!!
                if (resident.getTownRoles().find { it.bankDeposit || it.cmdMayor } == null)
                    throw GracefulCommandException("<red>You don't have bank deposit permissions in this town.")
            }

            Residential.commandManager.commandConditions.addCondition("announce") { context ->
                val player = context.issuer.player
                val resident = ResidentService.get(player.uniqueId)!!
                if (resident.getTownRoles().find { it.announce || it.cmdMayor } == null)
                    throw GracefulCommandException("<red>You don't have announcement permissions in this town.")
            }

            Residential.commandManager.commandConditions.addCondition("cmdPlotManagement") { context ->
                val player = context.issuer.player
                val resident = ResidentService.get(player.uniqueId)!!
                if (resident.getTownRoles().find { it.cmdPlotManagement || it.cmdMayor } == null)
                    throw GracefulCommandException("<red>You don't have plot management permissions in this town.")
            }

            Residential.commandManager.commandConditions.addCondition("cmdMayor") { context ->
                val player = context.issuer.player
                val resident = ResidentService.get(player.uniqueId)!!
                if (resident.getTownRoles().find { it.cmdMayor } == null)
                    throw GracefulCommandException("<red>You don't have permission to run mayor commands in this town.")
            }

            Residential.commandManager.commandConditions.addCondition("isMayor") { context ->
                val player = context.issuer.player
                val resident = ResidentService.get(player.uniqueId)!!
                if (resident.getTownRoles().find { it.isMayor } == null)
                    throw GracefulCommandException("<red>You aren't the mayor of this town.")
            }
        }
    }
}