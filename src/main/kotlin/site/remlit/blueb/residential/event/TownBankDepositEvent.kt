package site.remlit.blueb.residential.event

import java.util.UUID

class TownBankDepositEvent(town: UUID, val amount: Double) : TownEvent(town)