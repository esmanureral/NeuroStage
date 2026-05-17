package com.esmanureral.neurostage.ui.patient.hub

import java.util.Calendar

enum class HubMotivationPeriod {
    MORNING,
    AFTERNOON,
    EVENING,
}

data class HubMotivationQuote(
    val text: String,
    val period: HubMotivationPeriod,
)

object HubMotivationQuotes {
    /** 05:00–11:59 sabah; 12:00–16:59 öğleden sonra; akşamda her iki havuzdan rastgele. */
    fun pickRandom(morning: Array<String>, afternoon: Array<String>): HubMotivationQuote {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val period = when (hour) {
            in 5..11 -> HubMotivationPeriod.MORNING
            in 12..16 -> HubMotivationPeriod.AFTERNOON
            else -> HubMotivationPeriod.EVENING
        }
        val pool = when (period) {
            HubMotivationPeriod.MORNING -> morning
            HubMotivationPeriod.AFTERNOON -> afternoon
            HubMotivationPeriod.EVENING -> morning + afternoon
        }
        val text = pool.randomOrNull().orEmpty()
        return HubMotivationQuote(text = text, period = period)
    }
}
