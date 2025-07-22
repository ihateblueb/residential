package site.remlit.blueb.residential.integration.dynmap

import org.dynmap.DynmapCommonAPIListener

class DynmapIntegration {
    companion object {
        fun register() {
            DynmapCommonAPIListener.register(DynmapListener())
        }
    }
}