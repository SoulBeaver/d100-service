package dev.christianbroomfield.d100web.model.encounter

enum class Difficulty {
    Easy,
    Challenging,
    Harsh,
    Unfair,
    Hellscape,
    Unreal
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
)