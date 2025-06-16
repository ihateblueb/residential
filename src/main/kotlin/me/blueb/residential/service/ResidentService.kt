package me.blueb.residential.service

import java.util.UUID

class ResidentService {
    companion object {
        fun getTown(resident: UUID): UUID? {
            val townUuid = null // TODO
            return if (townUuid != null) UUID.fromString(townUuid) else null
        }
    }
}