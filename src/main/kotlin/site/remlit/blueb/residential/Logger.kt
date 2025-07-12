package site.remlit.blueb.residential

class Logger {
    companion object {
        val paperLogger = Residential.instance.logger

        fun info(context: String? = null, message: String) = paperLogger.info("${if (context != null) "[$context] " else ""}$message")
        fun warn(context: String? = null, message: String) = paperLogger.warning("${if (context != null) "[$context] " else ""}$message")
        fun severe(context: String? = null, message: String) = paperLogger.severe("${if (context != null) "[$context] " else ""}$message")

        fun info(message: String) = info(null, message)
        fun warn(message: String) = warn(null, message)
        fun severe(message: String) = severe(null, message)
    }
}