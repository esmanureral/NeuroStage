package com.esmanureral.neurostage.ui.patient.hub

import com.esmanureral.neurostage.data.AppPreferences
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
    fun resolve(
        morning: Array<String>,
        afternoon: Array<String>,
        cached: AppPreferences.CachedHubMotivationQuote?,
        onPersist: (HubMotivationQuote) -> Unit,
    ): HubMotivationQuote {
        cached?.let { entry ->
            val period = HubMotivationPeriod.entries.getOrElse(entry.periodOrdinal) {
                HubMotivationPeriod.AFTERNOON
            }
            return HubMotivationQuote(text = entry.text, period = period)
        }
        val fresh = pickRandom(morning, afternoon)
        onPersist(fresh)
        return fresh
    }

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
