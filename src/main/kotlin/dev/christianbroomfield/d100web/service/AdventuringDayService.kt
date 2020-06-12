package dev.christianbroomfield.d100web.service

import dev.christianbroomfield.d100.RollMaster
import dev.christianbroomfield.d100web.model.TableGroupName
import dev.christianbroomfield.d100web.model.encounter.AdventuringDay
import dev.christianbroomfield.d100web.model.encounter.Event
import dev.christianbroomfield.d100web.model.encounter.EventType
import dev.christianbroomfield.d100web.model.encounter.TimeOfDay
import kotlin.random.Random
import mu.KotlinLogging

private const val D6_MAX = 6
private const val D10_MAX = 10

private val log = KotlinLogging.logger {}
class AdventuringDayService(
    private val tableService: TableService,
    private val random: Random = Random.Default,
    private val rollMaster: RollMaster = RollMaster()
) {
    private val d6 = { random.nextInt(D6_MAX) + 1 }
    private val d10 = { random.nextInt(D10_MAX) + 1 }

    fun generateEncounterDay(difficulty: Int): AdventuringDay {
        val events = TimeOfDay.values().map { timeofDay ->
            generateEvent(difficulty, timeofDay)
        }

        return AdventuringDay(
            weather = generateWeather(),
            events = events
        ).also {
            log.debug { "$it" }
        }
    }

    private fun generateWeather(): String {
        val weatherTable = tableService.get(TableGroupName("default_SpringWeather"))!!

        return rollMaster.roll(weatherTable.tables, hideDescriptor = true)
            .joinToString("\n")
    }

    private fun generateEvent(difficulty: Int, timeOfDay: TimeOfDay): Event {
        val encounterRoll = d6()
        val discoveryRoll = d6()
        val treasureRoll = d10()

        log.debug { "Encounter=$encounterRoll; Discovery=$discoveryRoll; Treasure=$treasureRoll" }

        return when {
            encounterRoll <= difficulty -> encounter(timeOfDay)

            encounterRoll == D6_MAX -> {
                when {
                    treasureRoll == D10_MAX -> treasure(timeOfDay)
                    discoveryRoll == D6_MAX -> discovery(timeOfDay)
                    else -> mundane(timeOfDay)
                }
            }

            else -> mundane(timeOfDay)
        }
    }

    private fun encounter(timeOfDay: TimeOfDay): Event {
        val encounter = tableService.getOneOf("encounter_")
        return Event(
            timeOfDay = timeOfDay,
            eventType = EventType.Encounter,
            results = rollMaster.roll(encounter.tables)
        )
    }

    private fun discovery(timeOfDay: TimeOfDay): Event {
        val discovery = tableService.getOneOf("discovery_")
        return Event(
            timeOfDay = timeOfDay,
            eventType = EventType.Discovery,
            results = rollMaster.roll(discovery.tables)
        )
    }

    private fun treasure(timeOfDay: TimeOfDay): Event {
        val treasure = tableService.getOneOf("treasure_")
        return Event(
            timeOfDay = timeOfDay,
            eventType = EventType.Treasure,
            results = rollMaster.roll(treasure.tables)
        )
    }

    private fun mundane(timeOfDay: TimeOfDay): Event {
        return Event(
            timeOfDay = timeOfDay,
            eventType = EventType.Nothing,
            results = emptyList()
        )
    }
}
