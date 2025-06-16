package me.blueb.residential.models

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class Config(
    val worlds: List<String> = listOf("world", "world_nether", "world_the_end"),
    val town: TownConfig = TownConfig(),
    val nation: NationConfig = NationConfig(),
)

@ConfigSerializable
data class TownConfig(
    val name: NameConfig = NameConfig(20),
    val tag: TagConfig = TagConfig(true, 4),
    val cost: Int = 150,
    val abandonment: Boolean = true,
    val tax: TaxConfig = TaxConfig(enabled = true, percent = false, interval = 1, cost = 5),
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
data class TaxConfig(
    val enabled: Boolean,
    val percent: Boolean,
    val interval: Int,
    val cost: Int
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