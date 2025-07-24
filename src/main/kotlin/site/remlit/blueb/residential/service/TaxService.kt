package site.remlit.blueb.residential.service

import site.remlit.blueb.residential.Configuration
import site.remlit.blueb.residential.Logger
import site.remlit.blueb.residential.Residential
import site.remlit.blueb.residential.util.inline.scheduled
import java.util.UUID
import kotlin.concurrent.thread

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

                if (extraChunks >= 1)
                    tax += (extraChunks * Configuration.config.town.claimableChunks.tax)

                Logger.info("Tax", "Post-chunk $tax")

                return tax
            }

            fun getBaseTaxForResidents(town: UUID): String {
                val town = TownService.get(town)!!
                return if (town.taxPercent) {
                    "${town.tax}%"
                } else {
                    Residential.economy.format(town.tax)
                }
            }
        }
    }

    class Resident {
        companion object {
            fun calculateTax(resident: UUID): Double {
                val resident = ResidentService.get(resident) ?: return 0.0
                if (resident.town == null) return 0.0
                val town = TownService.get(resident.town) ?: return 0.0

                var tax = 0.0

                if (town.taxPercent) {
                    tax += (resident.getBalance() / town.tax) * 100
                } else {
                    tax += town.tax
                }

                return tax
            }
        }
    }

    companion object {
        val town = Town.Companion
        val resident = Resident.Companion

        fun collectTaxes() {
            thread(name = "Residential Tax Thread") {
                val residents = ResidentService.getAll()
                for (resident in residents) {
                    val tax = Resident.calculateTax(resident.uuid)
                    val player = resident.getPlayer()

                    if (resident.town == null)
                        continue

                    if (Residential.economy.getBalance(player) < tax) {
                        println("Player cannot pay their taxes!")
                        InboxService.send(resident.uuid, "Tax Service", "You are unable to pay your taxes!")
                        continue
                    }

                    scheduled {
                        Residential.economy.withdrawPlayer(player, tax)
                        TownService.deposit(resident.town, tax, true)
                    }
                }

                val towns = TownService.getAll()
                for (town in towns) {
                    val tax = Town.calculateTax(town.uuid)

                    if (town.balance < tax) {
                        println("Town cannot pay their taxes!")
                        InboxService.send(town.getMayor().uuid, "Tax Service", "Your town is unable to pay its taxes!")
                        continue
                    }

                    scheduled {
                        TownService.withdraw(town.uuid, tax, true)
                    }
                }
            }
        }
    }
}