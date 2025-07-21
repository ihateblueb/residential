package site.remlit.blueb.residential.command.condition

import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.model.GracefulCommandException

class PlayerConditions {
    companion object {
        fun register() {
            Residential.commandManager.commandConditions.addCondition("isPlayer") { context ->
                if (!context.issuer.isPlayer)
                    throw GracefulCommandException("<red>You aren't a player.")
            }

            Residential.commandManager.commandConditions.addCondition("isNotPlayer") { context ->
                if (context.issuer.isPlayer)
                    throw GracefulCommandException("<red>You are a player.")
            }
        }
    }
}