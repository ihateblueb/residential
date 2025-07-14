package site.remlit.blueb.residential.util

import site.remlit.blueb.residential.Logger
import site.remlit.blueb.residential.Residential
import java.io.File
import java.time.LocalDateTime
import kotlin.concurrent.thread
import kotlin.io.path.Path

class ExceptionUtil {
    companion object {
        fun createReport(location: String, e: Exception) {
            thread(name = "Residential Exception Report Creator") {
                Logger.severe("Exception", "At ${location}: ${e.message}")

                val basePath = Path("${Residential.instance.dataFolder.path}/exceptions")
                File(basePath.toString()).mkdirs()

                val time = LocalDateTime.now()
                val reportFile = File(basePath.toString(), "/report-${time}.txt")
                reportFile.createNewFile()

                val writer = reportFile.writer(Charsets.UTF_8)
                writer.write("Residential Exception Report at $time\n")
                writer.write("(Exception@$location)\n")

                writer.write("\n---- Exception ----\n")
                writer.write("e.message: ${e.message}\n")
                writer.write("e.stackTrace: |-\n")
                for (line in e.stackTrace) {
                    writer.write("$line\n")
                }

                writer.write("\n---- Server ----\n")
                writer.write("Version: ${Residential.instance.server.name} ${Residential.instance.server.version}\n")
                writer.write("Plugins: |-\n")
                for (plugin in Residential.instance.server.pluginManager.plugins) {
                    writer.write("${plugin.pluginMeta.name} (${plugin.pluginMeta.version})\n")
                }

                writer.write("\n---- System ----\n")
                writer.write("${System.getProperties()}\n")

                writer.close()

                Logger.severe("Exception", "Saved exception report at ${reportFile.path}. Please report this to the developer.")
            }
        }
    }
}