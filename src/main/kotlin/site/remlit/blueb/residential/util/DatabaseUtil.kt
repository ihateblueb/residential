package site.remlit.blueb.residential.util

import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.UUID

class DatabaseUtil {
    companion object {
        fun extractUuid(string: String?): UUID? {
            return if (string != null) UUID.fromString(string) else null
        }

        fun extractLocalDateTime(string: String?): LocalDateTime? {
            return if (string != null) LocalDateTime.parse(string) else null
        }

        fun <T> extractList(string: String, converter: (String) -> T): List<T> {
            val list = mutableListOf<T>()
            string.drop(1).dropLast(1).split(",").forEach { list.add(converter(it.drop(1).dropLast(1))) }
            return list.toList()
        }

        fun <T> listToJson(list: List<T>, converter: (T) -> String): String {
            val stringList = mutableListOf<String>()
            list.forEach { stringList.add("\"${converter(it)}\"") }
            return "[${stringList.joinToString(",")}]"
        }

        inline fun <reified T> extractNullable(
            rs: ResultSet,
            name: String,
            noinline listConverter: (String) -> Any? = { it }
        ): T? {
            val nullable =
                when (T::class) {
                    String::class, UUID::class, List::class -> rs.getString(name)
                    Boolean::class -> rs.getBoolean(name)
                    Int::class -> rs.getInt(name)
                    Double::class -> rs.getDouble(name)
                    else -> rs.getString(name)
                }
            return if (rs.wasNull()) null
            else if (T::class == UUID::class) UuidUtil.fromStringOrNull(nullable as String) as? T
            else if (T::class == List::class) extractList(nullable as String, listConverter) as? T
            else nullable as? T
        }
    }
}