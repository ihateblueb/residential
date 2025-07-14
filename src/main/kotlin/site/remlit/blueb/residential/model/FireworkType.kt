package site.remlit.blueb.residential.model

enum class FireworkType {
    NEW_TOWN,
    NEW_DAY;

    companion object {
        fun toStringList(): List<String> {
            val list = mutableListOf<String>()
            entries.forEach { entry -> list.add(entry.toString()) }
            return list
        }
    }
}
