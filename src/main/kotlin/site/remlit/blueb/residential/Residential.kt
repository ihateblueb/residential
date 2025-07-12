package site.remlit.blueb.residential

import co.aikar.commands.PaperCommandManager
import site.remlit.blueb.residential.service.TownRoleService
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin
import kotlin.concurrent.thread
import kotlin.time.measureTime

class Residential : JavaPlugin() {
    override fun onEnable() {
        instance = this

        if (instance.server.pluginManager.getPlugin("Towny") != null) {
            instance.logger.severe("Towny conflicts severely with Residential and it cannot run alongside it.")
            instance.server.pluginManager.disablePlugin(this)
            return
        }

        val vaultTimeTaken = measureTime {
            if (instance.server.pluginManager.getPlugin("Vault") == null) {
                instance.logger.severe("Vault is required to use Residential.")
                instance.server.pluginManager.disablePlugin(this)
                return
            }
            val rsp = instance.server.servicesManager.getRegistration(Economy::class.java)
            if (rsp == null) {
                instance.logger.severe("An economy provider is required to use Residential.")
                instance.server.pluginManager.disablePlugin(this)
                return
            }
            economy = rsp.provider
        }
        instance.logger.info("Vault setup in ${vaultTimeTaken.inWholeMilliseconds} ms")

        val configTimeTaken = measureTime { Configuration.load() }
        instance.logger.info("Loaded configuration in ${configTimeTaken.inWholeMilliseconds} ms")

        val dbTimeTaken = measureTime {
            Database.connect()
            Database.setup()
        }
        instance.logger.info("Connected to and setup database in ${dbTimeTaken.inWholeMilliseconds} ms")

        Commands.register()
        EventListener.register()

        thread(name = "ResidentialSyncThread") {
            try {
                instance.logger.info("Syncing town roles...")
                TownRoleService.syncRoles()
            } finally { instance.logger.info("Town role sync completed.") }
        }
    }

    override fun onDisable() {
        if (Database.connectionInitialized)
            Database.connection.close()

        this.logger.info("Goodbye!")
    }

    companion object {
        lateinit var instance: Residential
        lateinit var economy: Economy
        lateinit var commandManager: PaperCommandManager
    }
}
