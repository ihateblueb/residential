package site.remlit.blueb.residential.service

import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.Logger
import java.util.UUID

/**
 * Tax collection and calculation
 * */
class TaxService {

    class Town {
        companion object {
            fun calculateTax(town: UUID): Double {
                val town = TownService.get(town)!!

                var tax = 0.0

                // Possibly percentage

                if (Configuration.config.town.tax.server.percent) {
                    tax += (town.balance / Configuration.config.town.tax.server.cost) * 100
                } else {
                    tax += Configuration.config.town.tax.server.cost
                }

                Logger.info("Tax", "Post-base $tax")

                // Non-percentage

                val claimedChunks = ChunkService.getAllClaimedChunks(town.uuid)
                val extraChunks = claimedChunks.size - Configuration.config.town.claimableChunks.initial

                if (extraChunks > 1)
                    tax += (extraChunks * Configuration.config.town.claimableChunks.tax)

                Logger.info("Tax", "Post-chunk $tax")

                return tax
            }
        }
    }

    class Resident {
        companion object {
            fun calculateTax(resident: UUID): Double {
                val resident = ResidentService.get(resident) ?: return 0.0
                if (resident.town == null) return 0.0
                val town = TownService.get(resident.town) ?: return 0.0
                return 0.0
            }
        }
    }

    companion object {
        val town = Town.Companion
        val resident = Resident.Companion
    }
}