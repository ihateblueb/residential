package me.blueb.residential.services

import java.sql.ResultSet
import java.util.UUID

class DatabaseService {
    companion object {
        fun extractUuid(string: String?): UUID? {
            return if (string != null) UUID.fromString(string) else null
        }

        fun getStringOrNull(rs: ResultSet, column: String): String? {
            val value = rs.getString(column)
            return if (rs.wasNull()) null else value
        }

        fun <T> extractList(string: String, converter: (String) -> T): List<T> {
            val list = mutableListOf<T>()
            string.drop(1).dropLast(1).split(",").forEach { list.add(converter(it)) }
            return list.toList()
        }

        fun <T> listToJson(list: List<T>, converter: (T) -> String): String {
            val stringList = mutableListOf<String>()
            list.forEach { stringList.add(converter(it)) }
            return "[${stringList.joinToString(",")}]"
        }
    }
}