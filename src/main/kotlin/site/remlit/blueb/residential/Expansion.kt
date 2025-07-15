package site.remlit.blueb.residential

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import site.remlit.blueb.residential.model.Town
import site.remlit.blueb.residential.service.ChunkService
import site.remlit.blueb.residential.service.ResidentService
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.ChunkUtil
import site.remlit.blueb.residential.util.ExceptionUtil
import java.util.UUID

class Expansion : PlaceholderExpansion() {
    override fun getIdentifier(): String = "residential"
    override fun getAuthor(): String = Residential.instance.pluginMeta.authors.first()
    override fun getVersion(): String = Residential.instance.pluginMeta.version

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        val split = params.split("_")
        var indexModifier = 0

        fun isUuid(string: String): Boolean =
            try { UUID.fromString(string); true } catch (e: Throwable) { false }

        // todo: playerPlaceholders

        fun townPlaceholders(town: Town?): String? {
            if (town == null) return null

            val thing = split.getOrNull(1+indexModifier)
            val thingModifier = split.getOrNull(2+indexModifier)

            if (thing == null) return town.name
            if (thing == "uuid") return town.uuid.toString()
            if (thing == "name") return town.name
            if (thing == "tag") return town.tag

            if (thing == "founder") return town.founder.toString()

            if (thing == "foundedAt" && thingModifier == null) return town.foundedAt.toString()
            if (thing == "foundedAt" && thingModifier == "formatted") return town.foundedAt.toString() // todo

            if (thing == "abandoned") return town.abandoned.toString()
            if (thing == "nation") return town.nation.toString()
            if (thing == "homeChunk") return town.homeChunk
            if (thing == "spawn") return town.spawn

            if (thing == "open") return town.open.toString()
            if (thing == "pvp") return town.open.toString()
            if (thing == "mobs") return town.open.toString()
            if (thing == "fire") return town.open.toString()

            if (thing == "balance" && thingModifier == null) return town.balance.toString()
            if (thing == "balance" && thingModifier == "formatted") return Residential.economy.format(town.balance)

            if (thing == "tax" && thingModifier == null) return town.tax.toString()
            if (thing == "tax" && thingModifier == "formatted") return if (town.taxPercent) "${town.tax}%" else Residential.economy.format(town.tax)

            if (thing == "taxDebt" && thingModifier == null) return town.taxDebt.toString()
            if (thing == "taxFeeMultiplier" && thingModifier == null) return town.taxFeeMultiplier.toString()
            if (thing == "taxMaxLate" && thingModifier == null) return town.taxMaxLate.toString()

            if (thing == "mayor") return town.getMayor()?.uuid.toString()
            if (thing == "maxChunks") return town.getMaxChunks().toString()

            return null
        }

        try {
            if (split.getOrNull(0) == "town") {
                return townPlaceholders(
                    if (split.getOrNull(1) != null && isUuid(split[1])) {
                        indexModifier++
                        TownService.get(UUID.fromString(split[1]))
                    }
                    else if (player != null) TownService.get(ResidentService.get(player.uniqueId)!!.town!!)
                    else null
                )
            } else if (split.getOrNull(0) == "townIn") {
                if (player == null) return null
                return townPlaceholders(
                    TownService.get(ChunkService.get(ChunkUtil.chunkToString(player.location.chunk))?.town ?: return null)
                )
            }
        } catch (e: Throwable) {
            ExceptionUtil.createReport("onPlaceholderRequest:$params", e)
        }

        return null
    }
}