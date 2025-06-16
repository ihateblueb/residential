package me.blueb.residential

import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection

class Residential : JavaPlugin() {
    override fun onEnable() {
        instance = this

        ResidentialDatabase.connect()
        ResidentialDatabase.setup()

        ResidentialCommands.register(instance)
    }

    override fun onDisable() {
        this.logger.info("Goodbye!")
    }

    companion object {
        lateinit var instance: Residential
    }
}
