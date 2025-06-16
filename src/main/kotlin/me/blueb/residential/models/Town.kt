package me.blueb.residential.models

import java.time.LocalDateTime
import java.util.UUID

data class Town(
    val uuid: UUID,
    val name: String,
    val tag: String?,
    val founder: UUID,
    val foundedAt: LocalDateTime,
    val abandoned: Boolean,
    val nation: UUID?,
    val homeChunk: String,
    val spawn: String,
)