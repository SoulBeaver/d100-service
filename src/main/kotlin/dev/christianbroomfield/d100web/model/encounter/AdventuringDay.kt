package dev.christianbroomfield.d100web.model.encounter

import org.http4k.template.ViewModel

data class AdventuringDayVariants(
    val variants: List<AdventuringDayVariant>
) : ViewModel

data class AdventuringDayVariant(
    val difficulty: Difficulty,
    val description: String,
    val relativeUrl: String
)

data class AdventuringDay(
    val weather: String,

    val events: List<Event>
) : ViewModel

data class Event(
    val timeOfDay: TimeOfDay,
    val eventType: EventType,
    val results: List<String>
)

enum class TimeOfDay {
    Morning,
    Afternoon,
    Evening,
    Dusk,
    Midnight,
    Predawn
}

enum class EventType {
    Nothing,
    Encounter,
    Discovery,
    Treasure
}

enum class Difficulty(val threshold: Int) {
    Easy(1),
    Challenging(2),
    Harsh(3),
    Unfair(4),
    Hellscape(5),
    Unreal(6)
}