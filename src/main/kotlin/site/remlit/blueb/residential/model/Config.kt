package site.remlit.blueb.residential.model

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class Config(
    val database: String = "sqlite",
    val dbConnection: DbConnectionConfig? = DbConnectionConfig(),
    val worlds: List<String> = listOf("world", "world_nether", "world_the_end"),
    val town: TownConfig = TownConfig(),
    val nation: NationConfig = NationConfig(),
)

@ConfigSerializable
data class DbConnectionConfig(
    val host: String? = "127.0.0.1",
    val port: String? = "5432",
    val name: String? = "residential",
    val user: String? = null,
    val password: String? = null,
)

@ConfigSerializable
data class TownConfig(
    val name: NameConfig = NameConfig(20),
    val tag: TagConfig = TagConfig(true, 4),
    val cost: Double = 150.0,
    val claimableChunks: ClaimableChunksConfig = ClaimableChunksConfig(500, 15, 50, 15),
    val permissions: PermissionsConfig = PermissionsConfig(1, 3, 3, 3, 2),
    val roles: RolesConfig = RolesConfig(15, listOf(
        "Mayor,false,true,true,true,true,true,true",
        "Treasurer,false,false,true,true,false,false,false",
        "Land Manager,false,false,false,false,false,true,false",
        "Resident,true,false,false,true,false,false,false"
    )),
    val abandonment: Boolean = true,
    val tax: TownTaxConfig = TownTaxConfig(
        TaxConfig(
            enabled = true,
            percent = false,
            interval = 1,
            cost = 25.0,
            debt = true,
            feeMultiplier = 1.75,
            maxLate = 5
        ),
        TaxConfig(
            enabled = true,
            percent = false,
            interval = 1,
            cost = 5.0,
            debt = true,
            feeMultiplier = 1.75,
            maxLate = 10
        )
    ),
    val balanceLimit: Int = 0
)

@ConfigSerializable
data class NameConfig(
    val maxLength: Int
)

@ConfigSerializable
data class TagConfig(
    val enabled: Boolean,
    val maxLength: Int
)

@ConfigSerializable
data class ClaimableChunksConfig(
    val max: Int,
    val initial: Int,
    val cost: Int,
    val tax: Int
)

@ConfigSerializable
data class PermissionsConfig(
    val enter: Int,
    val destroy: Int,
    val place: Int,
    val use: Int,
    val cmdSpawn: Int,
)

@ConfigSerializable
data class RolesConfig(
    val max: Int,
    val default: List<String>,
)

@ConfigSerializable
data class TownTaxConfig(
    val server: TaxConfig,
    val resident: TaxConfig,
)

@ConfigSerializable
data class TaxConfig(
    val enabled: Boolean,
    val percent: Boolean,
    val interval: Int,
    val cost: Double,
    val debt: Boolean,
    val feeMultiplier: Double,
    val maxLate: Int
)

@ConfigSerializable
data class NationTaxConfig(
    val enabled: Boolean,
    val percent: Boolean,
    val target: String,
    val interval: Int,
    val cost: Int
)

@ConfigSerializable
data class NationConfig(
    val enabled: Boolean = true,
    val name: NameConfig = NameConfig(24),
    val tag: TagConfig = TagConfig(true, 4),
    val cost: Int = 1500,
    val tax: NationTaxConfig = NationTaxConfig(enabled = true, percent = true, target = "town", interval = 1, cost = 5),
    val balanceLimit: Int = 0,
)