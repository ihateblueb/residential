package me.blueb.residential

import me.blueb.residential.services.TownRoleService
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin
import kotlin.concurrent.thread
import kotlin.time.measureTime

class Residential : JavaPlugin() {
    override fun onEnable() {
        instance = this

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

        val configTimeTaken = measureTime { ResidentialConfig.load() }
        instance.logger.info("Loaded configuration in ${configTimeTaken.inWholeMilliseconds} ms")

        val dbTimeTaken = measureTime {
            ResidentialDatabase.connect()
            ResidentialDatabase.setup()
        }
        instance.logger.info("Setup database in ${dbTimeTaken.inWholeMilliseconds} ms")

        val registrationTimeTaken = measureTime {
            ResidentialCommands.register()
            ResidentialListeners.register()
        }
        instance.logger.info("Registered hooks in ${registrationTimeTaken.inWholeMilliseconds} ms")

        thread(name = "ResidentialSyncThread") {
            try {
                instance.logger.info("Syncing town roles...")
                TownRoleService.syncRoles()
            } finally { instance.logger.info("Town role sync completed.") }
        }
    }

    override fun onDisable() {
        if (ResidentialDatabase.connectionInitialized)
            ResidentialDatabase.connection.close()

        this.logger.info("Goodbye!")
    }

    companion object {
        lateinit var instance: Residential
        lateinit var economy: Economy
    }
}
