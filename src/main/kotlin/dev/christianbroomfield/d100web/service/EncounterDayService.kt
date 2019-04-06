package dev.christianbroomfield.d100web.service

import dev.christianbroomfield.d100.RollMaster
import dev.christianbroomfield.d100web.model.TableGroupName
import dev.christianbroomfield.d100web.model.encounter.EncounterDay
import mu.KotlinLogging
import kotlin.random.Random

private val log = KotlinLogging.logger {}
class EncounterDayService(
    private val tableService: TableService,
    private val random: Random = Random.Default,
    private val rollMaster: RollMaster = RollMaster()
) {

    fun generateEncounterDay(difficulty: Int): EncounterDay {
        return EncounterDay(
            generateWeather(),
            generateEvent(difficulty),
            generateEvent(difficulty),
            generateEvent(difficulty),
            generateEvent(difficulty),
            generateEvent(difficulty),
            generateEvent(difficulty)
        )
    }

    private fun generateWeather(): String {
        val weatherTable = tableService.get(TableGroupName("default_SpringWeather"))!!

        return rollMaster.roll(weatherTable.tables, hideDescriptor = true).joinToString("\n")
    }

    private fun generateEvent(difficulty: Int): String {
        val encounterRoll = roll()
        val discoveryRoll = roll()
        val treasureRoll  = roll()

        log.debug { "Encounter=$encounterRoll; Discovery=$discoveryRoll; Treasure=$treasureRoll" }

        return when {
            encounterRoll <= difficulty -> "Encounter!\n\n${encounter()}"

            encounterRoll == 6 -> {
                when {
                    treasureRoll == 6 -> "Treasure!\n\n${treasure()}"
                    discoveryRoll >= difficulty -> "Discovery!\n\n${discovery()}"
                    else -> "Keep on trekking!"
                }
            }

            else -> "Keep on trekking!"
        }
    }

    private fun encounter(): String {
        log.debug { "Rolling an encounter." }

        val encounter = tableService.getOneOf("encounter_")
        return rollMaster.roll(encounter.tables).joinToString("\n")
    }

    private fun discovery(): String {
        log.debug { "Rolling a discovery." }

        val discovery = tableService.getOneOf("discovery_")
        return rollMaster.roll(discovery.tables).joinToString("\n")
    }

    private fun treasure(): String {
        log.debug { "Rolling a treasure." }

        val treasure = tableService.getOneOf("treasure_")
        return rollMaster.roll(treasure.tables).joinToString("\n")
    }

    private fun roll(): Int {
        return random.nextInt(6) + 1
    }
}