package site.remlit.blueb.residential.util

import site.remlit.blueb.residential.Residential
import org.bukkit.Chunk

class ChunkUtil {
    companion object {
        fun chunkToString(chunk: Chunk): String {
            return "${chunk.x},${chunk.z},${chunk.world.name}"
        }

        /**
         * Convert a string to a chunk
         * @param string x,z
         * @param world world
         * */
        fun stringToChunk(chunk: String, world: String): Chunk? {
            val split = chunk.split(",")
            return Residential.instance.server.getWorld(world)?.getChunkAt(split.first().toInt(), split.last().toInt())
        }

        /**
         * Convert a string to a chunk
         * @param string x,z,world
         * */
        fun stringToChunk(string: String): Chunk? {
            val split = string.split(",")
            return Residential.instance.server.getWorld(split[2])?.getChunkAt(split[0].toInt(), split[1].toInt())
        }
    }
}