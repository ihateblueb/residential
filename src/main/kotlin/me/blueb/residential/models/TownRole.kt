package me.blueb.residential.models

import java.sql.ResultSet
import java.util.UUID

data class TownRole(
    val uuid: UUID,
    val name: String,
    val town: UUID,

    val isDefault: Boolean,
    val isMayor: Boolean,

    val bankWithdraw: Boolean,
    val bankDeposit: Boolean,

    val cmdPlotManagement: Boolean,
    val cmdMayor: Boolean,
) {
    companion object {
        fun fromRs(rs: ResultSet): TownRole? {
            while (rs.next()) {
                return TownRole(
                    uuid = UUID.fromString(rs.getString("uuid")),
                    name = rs.getString("name"),
                    town = UUID.fromString(rs.getString("town")),

                    isDefault = rs.getBoolean("is_default"),
                    isMayor = rs.getBoolean("is_mayor"),

                    bankWithdraw = rs.getBoolean("bank_withdraw"),
                    bankDeposit = rs.getBoolean("bank_deposit"),

                    cmdPlotManagement = rs.getBoolean("cmd_plot_management"),
                    cmdMayor = rs.getBoolean("cmd_mayor")
                )
            }
            return null
        }
    }
}