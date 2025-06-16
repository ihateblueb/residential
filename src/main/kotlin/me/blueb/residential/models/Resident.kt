package me.blueb.residential.models

import java.util.UUID

data class Resident(
    val uuid: UUID,
    val claims: List<UUID?>,
    val trusted: List<UUID?>,
    val town: UUID?,
)
