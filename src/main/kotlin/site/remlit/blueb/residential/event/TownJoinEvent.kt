package site.remlit.blueb.residential.event

import java.util.UUID

class TownJoinEvent(val resident: UUID, town: UUID) : TownEvent(town)