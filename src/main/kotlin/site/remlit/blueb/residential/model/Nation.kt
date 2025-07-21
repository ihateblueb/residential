package site.remlit.blueb.residential.model

import java.time.LocalDateTime
import java.util.UUID

data class Nation(
    val uuid: UUID,
    val name: String,
    val tag: String?,
    val founder: UUID,
    val foundedAt: LocalDateTime,

    val open: Boolean,

    val balance: Double,

    val tax: Double,
    val taxPercent: Boolean,
    val taxDebt: Boolean,
    val taxFeeMultiplier: Double,
    val taxMaxLate: Int,
) {
    fun getLeader() = { TODO() }
    fun getCollectiveBalance() = { TODO() }

    fun getTowns() = { TODO() }
    fun getResidents() = { TODO() }
}