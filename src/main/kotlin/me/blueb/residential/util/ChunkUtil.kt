package me.blueb.residential.util

import me.blueb.residential.Residential
import org.bukkit.Chunk

class ChunkUtil {
    companion object {
        fun chunkToString(chunk: Chunk): String {
            return "${chunk.x},${chunk.z}"
        }
        fun stringToChunk(string: String, world: String): Chunk? {
            val split = string.split(",")
            return Residential.instance.server.getWorld(world)?.getChunkAt(split.first().toInt(), split.last().toInt())
        }
    }
}