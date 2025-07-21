package site.remlit.blueb.residential.model

import java.sql.ResultSet
import java.util.UUID

/*
* When you update this, update command.condition.TownConditions too!
* */
data class TownRole(
    val uuid: UUID,
    val name: String,
    val town: UUID,

    val isDefault: Boolean,
    val isMayor: Boolean,

    val destroy: Boolean,
    val place: Boolean,
    val use: Boolean,
    val spawn: Boolean,

    val bankWithdraw: Boolean,
    val bankDeposit: Boolean,

    val announce: Boolean,

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

                    destroy = rs.getBoolean("destroy"),
                    place = rs.getBoolean("place"),
                    use = rs.getBoolean("use"),
                    spawn = rs.getBoolean("spawn"),

                    bankWithdraw = rs.getBoolean("bank_withdraw"),
                    bankDeposit = rs.getBoolean("bank_deposit"),

                    announce = rs.getBoolean("announce"),

                    cmdPlotManagement = rs.getBoolean("cmd_plot_management"),
                    cmdMayor = rs.getBoolean("cmd_mayor")
                )
            }
            return null
        }
    }
}