package me.blueb.residential.util

import java.util.UUID

class DatabaseUtil {
    companion object {
        fun extractUuid(string: String?): UUID? {
            return if (string != null) UUID.fromString(string) else null
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