package site.remlit.blueb.residential.event

import java.util.UUID

class TownBankWithdrawEvent(town: UUID, val amount: Double) : TownEvent(town)