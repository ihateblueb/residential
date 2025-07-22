package site.remlit.blueb.residential.integration.dynmap

import org.dynmap.DynmapCommonAPI
import org.dynmap.DynmapCommonAPIListener
import org.dynmap.markers.AreaMarker
import org.dynmap.markers.MarkerIcon
import org.dynmap.markers.MarkerSet
import site.remlit.blueb.residential.model.Chunk
import site.remlit.blueb.residential.model.Town
import site.remlit.blueb.residential.service.ChunkService
import site.remlit.blueb.residential.service.TownService
import site.remlit.blueb.residential.util.ChunkUtil
import site.remlit.blueb.residential.util.LocationUtil

class DynmapListener : DynmapCommonAPIListener() {
    lateinit var townMarkerSet: MarkerSet
    lateinit var townMarkerIcon: MarkerIcon
    lateinit var capitolMarkerIcon: MarkerIcon
    lateinit var abandonedMarkerIcon: MarkerIcon

    /**
     * Create area marker
     * @param id - marker ID
     * @param lbl - label
     * @param markup - if true, label is HTML markup
     * @param world - world id
     * @param x - x coord list
     * @param z - z coord list
     * @param persistent - true if persistent
     */
    fun createAreaMarker(id: String, lbl: String, markup: Boolean, world: String, x: List<Double>, z: List<Double>, persistent: Boolean): AreaMarker? =
        townMarkerSet.createAreaMarker(id, lbl, markup, world, x.toDoubleArray(), z.toDoubleArray(), persistent)

    override fun apiEnabled(api: DynmapCommonAPI?) {
        if (api == null) return

        townMarkerSet =
            api.markerAPI.createMarkerSet(
                "residential_towns",
                "Towns",
                null,
                false
            )
        townMarkerIcon = api.markerAPI.getMarkerIcon("house")
        capitolMarkerIcon = api.markerAPI.getMarkerIcon("star")
        abandonedMarkerIcon = api.markerAPI.getMarkerIcon("skull")

        TownService.getAll()
            .forEach { markOnMap(it) }
    }

    fun markOnMap(town: Town) {
        val homeChunk = ChunkUtil.stringToChunk(town.homeChunk)!!
        val spawn = LocationUtil.stringToLocation(town.spawn, homeChunk.world.name)

        townMarkerSet.createMarker(
            "residential_town_${town.uuid}",
            "<div>${town.name}</div>",
            true,
            homeChunk.world.name,
            spawn.x,
            spawn.y,
            spawn.z,
            if (town.abandoned) abandonedMarkerIcon else townMarkerIcon,
            false
        )

        val chunks = ChunkService.getAllClaimedChunks(town.uuid)
        val (tracedX, tracedZ) = trace(chunks)

        createAreaMarker(
            "residential_area_${town.uuid}",
            town.name,
            true,
            homeChunk.world.name,
            tracedX,
            tracedZ,
            false
        )
    }

    fun trace(chunks: List<Chunk>): Pair<List<Double>, List<Double>> {
        val borderX = mutableListOf<Double>()
        val borderZ = mutableListOf<Double>()

        for (chunk in chunks) {
            val chunk = ChunkUtil.stringToChunk(chunk.location)!!
        }

        return Pair(borderX, borderZ)
    }
}