package site.remlit.blueb.residential.util

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Player

class SoundUtil {
    companion object {
        fun playTeleport(player: Player) =
            player.playSound(Sound.sound(Key.key("entity.enderman.teleport"), Sound.Source.AMBIENT, 1f, 1f))
    }
}