package site.remlit.blueb.residential

import site.remlit.blueb.residential.event.ClockTickEvent
import site.remlit.blueb.residential.event.NewDayEvent
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class Clock {
    companion object {
        var clockState = 0

        val clockInterval = 5

        // 228 * 5 = 1440 = 24 hours
        val maxClockState = 228

        private fun getClockState() {
            Database.connection.createStatement().use { stmt ->
                stmt.executeQuery("SELECT state FROM clock WHERE id = 'r'").use { rs ->
                    while (rs.next())
                        clockState = rs.getInt("state")
                }
            }
        }

        private fun setClock(int: Int) {
            Database.connection.prepareStatement("UPDATE clock SET state = ? WHERE id = 'r'").use { stmt ->
                stmt.setInt(1, int)
                stmt.execute()
            }
        }

        fun start() {
            thread(name = "Residential Clock Thread") {
                getClockState()

                Logger.info("Clock", "Starting clock at $clockState")

                while (!Thread.interrupted()) {
                    sleep((clockInterval.toLong() * 60) * 1000)

                    if (clockState == maxClockState) {
                        setClock(0)
                        NewDayEvent().callEvent()
                        Logger.info("Clock", "New day, resetting clock.")
                    } else {
                        setClock(clockState + 1)
                        ClockTickEvent(clockState + 1, maxClockState).callEvent()
                    }

                    getClockState()
                }
            }
        }
    }
}