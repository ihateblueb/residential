package me.blueb.residential.models

import java.util.UUID

data class Chunk(
    val location: String,
    val world: String,
    val town: UUID?,
    val plot: UUID?,
)
