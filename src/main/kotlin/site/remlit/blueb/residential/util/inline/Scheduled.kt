package site.remlit.blueb.residential.util.inline

import org.bukkit.Bukkit
import site.remlit.blueb.residential.Residential

/**
 * Uses Bukkit scheduler to run task later
 *
 * @param tickDelay Delay in ticks
 * */
inline fun scheduled(tickDelay: Long = 0, crossinline block: () -> Unit) =
    Bukkit.getScheduler().runTaskLater(
        Residential.instance,
        Runnable { block() },
        tickDelay
    )

/**
 * Uses Bukkit scheduler to run task later asynchronously
 *
 * @param tickDelay Delay in ticks
 * */
inline fun scheduledAsync(tickDelay: Long = 0, crossinline block: () -> Unit) =
    Bukkit.getScheduler().runTaskLaterAsynchronously(
        Residential.instance,
        Runnable { block() },
        tickDelay
    )