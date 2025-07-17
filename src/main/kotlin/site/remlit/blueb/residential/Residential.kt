package site.remlit.blueb.residential

import co.aikar.commands.PaperCommandManager
import site.remlit.blueb.residential.service.TownRoleService
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin
import site.remlit.blueb.residential.util.ExceptionUtil
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.time.measureTime

class Residential : JavaPlugin() {
    override fun onEnable() {
        instance = this

        if (instance.server.pluginManager.getPlugin("Towny") != null) {
            Logger.severe("Towny conflicts severely with Residential and cannot run alongside it.")
            instance.server.pluginManager.disablePlugin(this)
            return
        }

        try {
            val configTimeTaken = measureTime { Configuration.load() }
            Logger.info("Loaded configuration in ${configTimeTaken.inWholeMilliseconds} ms")
        } catch (e: Throwable) {
            ExceptionUtil.createReport("start:config", e)
        }

        try {
            val dbTimeTaken = measureTime {
                Database.connect()
                Database.setup()
            }
            Logger.info("Connected to and setup database in ${dbTimeTaken.inWholeMilliseconds} ms")
        } catch (e: Throwable) {
            ExceptionUtil.createReport("start:db", e)
        }

        if (instance.server.pluginManager.getPlugin("Vault") == null) {
            Logger.severe("Vault is required to use Residential.")
            instance.server.pluginManager.disablePlugin(this)
            return
        }
        val rsp = instance.server.servicesManager.getRegistration(Economy::class.java)
        if (rsp == null) {
            Logger.severe("An economy provider is required to use Residential.")
            instance.server.pluginManager.disablePlugin(this)
            return
        }
        economy = rsp.provider

        try {
            if (instance.server.pluginManager.getPlugin("PlaceholderAPI") != null) {
                Logger.info("Found PlaceholderAPI, registering expansion")
                Expansion().register()
            }
        } catch (e: Throwable) {
            ExceptionUtil.createReport("start:placeholderapi", e)
        }

        try {
            Commands.register()
            EventListener.register()
            Clock.start()

            thread(name = "Residential Sync Thread") {
                val interval = 30L // minutes
                Logger.info("Sync", "Sync thread interval set at $interval minutes")

                fun runSync() {
                    val syncTimeTaken = measureTime {
                        Logger.info("Sync", "Syncing town roles...")
                        TownRoleService.syncRoles()
                    }
                    Logger.info("Sync", "Town role sync completed in ${syncTimeTaken.inWholeMilliseconds}ms.")
                }

                while (!Thread.interrupted()) {
                    runSync()
                    sleep((interval * 60) * 1000)
                }
            }
        } catch (e: Throwable) {
            ExceptionUtil.createReport("start:end", e)
        }
    }

    override fun onDisable() {
        if (Database.connectionInitialized) {
            Logger.info("Closing database connection...")
            Database.connection.close()
        }

        Logger.info("Goodbye!")
    }

    companion object {
        lateinit var instance: Residential
        lateinit var economy: Economy
        lateinit var commandManager: PaperCommandManager
    }
}
