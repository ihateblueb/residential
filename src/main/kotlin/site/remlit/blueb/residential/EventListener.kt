package site.remlit.blueb.residential

import site.remlit.blueb.residential.listener.*

class EventListener {
    init { throw AssertionError("This class is not intended to be initialized.") }
    companion object {
        fun register() {
            Residential.instance.server.pluginManager.registerEvents(PlayerJoinListener(), Residential.instance)
            Residential.instance.server.pluginManager.registerEvents(PlayerMoveListener(), Residential.instance)

            Residential.instance.server.pluginManager.registerEvents(TownCreationListener(), Residential.instance)

            Residential.instance.server.pluginManager.registerEvents(TownBankDepositListener(), Residential.instance)
            Residential.instance.server.pluginManager.registerEvents(TownBankWithdrawListener(), Residential.instance)
        }
    }
}