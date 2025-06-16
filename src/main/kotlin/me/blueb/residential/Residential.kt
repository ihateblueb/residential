package me.blueb.residential

import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin
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
            if (instance.server.servicesManager.getRegistration(Economy::class.java) == null) {
                instance.logger.severe("An economy provider is required to use Residential.")
                instance.server.pluginManager.disablePlugin(this)
                return
            }
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
    }

    override fun onDisable() {
        if (ResidentialDatabase.connectionInitialized)
            ResidentialDatabase.connection.close()

        this.logger.info("Goodbye!")
    }

    companion object {
        lateinit var instance: Residential
    }
}
