package me.blueb.residential.database

import java.util.UUID

data class Resident(
    val uuid: UUID,
    val claims: List<UUID>,
    val trusted: List<UUID>,
    val towns: List<UUID>
)