package dev.christianbroomfield.d100web.model.encounter

import org.http4k.template.ViewModel

enum class Difficulty(val threshold: Int) {
    Easy(1),
    Challenging(2),
    Harsh(3),
    Unfair(4),
    Hellscape(5),
    Unreal(6)
}

enum class TimeOfDay {
    Morning,
    Afternoon,
    Evening,
    Dusk,
    Midnight,
    Predawn
}

data class EncounterDay(
    val weather: String,

    val morningEvent: String,
    val afternoonEvent: String,
    val eveningEvent: String,
    val duskEvent: String,
    val midnightEvent: String,
    val predawnEvent: String
) : ViewModel