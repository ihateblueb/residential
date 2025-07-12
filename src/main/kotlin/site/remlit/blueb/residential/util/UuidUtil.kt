package site.remlit.blueb.residential.util

import java.util.UUID

class UuidUtil {
    companion object {
        fun fromStringOrNull(uuid: String?): UUID? =
            try { UUID.fromString(uuid) } catch (e: Exception) { null }
    }
}