package dev.christianbroomfield.d100web.service

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.christianbroomfield.d100.RollMaster
import dev.christianbroomfield.d100.model.Table
import dev.christianbroomfield.d100.model.TableHeader
import dev.christianbroomfield.d100web.model.TableGroup
import dev.christianbroomfield.d100web.model.TableGroupName
import dev.christianbroomfield.d100web.model.encounter.Difficulty
import dev.christianbroomfield.d100web.model.encounter.EncounterDay
import dev.christianbroomfield.d100web.model.encounter.Event
import dev.christianbroomfield.d100web.model.encounter.EventType
import dev.christianbroomfield.d100web.model.encounter.TimeOfDay
import io.kotlintest.specs.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import kotlin.random.Random

class EncounterDayServiceTest : DescribeSpec() {
    private val tableService = mockk<TableService>()
    private val rollMaster = RollMaster(object : Random() {
        override fun nextBits(bitCount: Int): Int = 0

        // Always return the first value of the Table results- they aren't under
        // test here.
        override fun nextInt(until: Int): Int {
            return 0
        }
    })
    private val random = object : Random() {
        // Each Time Of Day rolls three times to generate an event
        // On an encounter roll <= the Difficulty threshold, an encounter happens (Easy is 1)
        // On an encounter roll of 6 and on a discovery roll of 6, a discovery happens
        // On an encounter roll of 6 and on a treasure roll of 10, a treasure is found
        // If both discovery and treasure are 6 and 10 respectively, choose the treasure
        private val answers = listOf(
            0, 0, 0, // Encounter
            4, 0, 0, // Nothing
            5, 5, 0, // Discovery
            4, 5, 9, // Nothing
            5, 4, 8, // Nothing
            5, 5, 9) // Treasure
        private var answerIdx = 0

        override fun nextBits(bitCount: Int): Int = 0

        override fun nextInt(until: Int): Int {
            return answers[answerIdx++]
        }
    }

    init {
        describe("An EncounterDayService") {
            val service = EncounterDayService(
                tableService,
                random,
                rollMaster
            )

            context("Generating an Adventuring Day") {
                every {
                    tableService.get(TableGroupName("default_SpringWeather"))
                } returns TableGroup(
                    "default_SpringWeather",
                    listOf(Table.PreppedTable(
                        header = TableHeader(1, 8, "Spring Weather"),
                        results = listOf("Mild", "", "", "", "", "", "", "")
                    )))

                every {
                    tableService.getOneOf("encounter_")
                } returns TableGroup(
                    "encounter_goblins",
                    listOf(Table.PreppedTable(
                        header = TableHeader(1, 8, "Goblins"),
                        results = listOf("A pack of goblins!", "", "", "", "", "", "", "")
                    )))

                every {
                    tableService.getOneOf("discovery_")
                } returns TableGroup(
                    "discovery_castles",
                    listOf(Table.PreppedTable(
                        header = TableHeader(1, 8, "Castles"),
                        results = listOf("An abandoned castle!", "", "", "", "", "", "", "")
                    )))

                every {
                    tableService.getOneOf("treasure_")
                } returns TableGroup(
                    "treasure_daggers",
                    listOf(Table.PreppedTable(
                        header = TableHeader(1, 8, "Daggers"),
                        results = listOf("A sparkling dagger!", "", "", "", "", "", "", "")
                    )))

                val adventuringDay = service.generateEncounterDay(Difficulty.Easy.threshold)

                it("has one of each type of event and mild weather") {
                    val expected = EncounterDay(
                        weather = "Mild",
                        events = listOf(
                            Event(TimeOfDay.Morning, EventType.Encounter, listOf("A pack of goblins!")),
                            Event(TimeOfDay.Afternoon, EventType.Nothing, emptyList()),
                            Event(TimeOfDay.Evening, EventType.Discovery, listOf("An abandoned castle!")),
                            Event(TimeOfDay.Dusk, EventType.Nothing, emptyList()),
                            Event(TimeOfDay.Midnight, EventType.Nothing, emptyList()),
                            Event(TimeOfDay.Predawn, EventType.Treasure, listOf("A sparkling dagger!"))
                        )
                    )

                    assertThat(adventuringDay, equalTo(expected))

                    verifyAll {
                        tableService.get(TableGroupName("default_SpringWeather"))
                        tableService.getOneOf("encounter_")
                        tableService.getOneOf("discovery_")
                        tableService.getOneOf("treasure_")
                    }
                }
            }
        }
    }
}