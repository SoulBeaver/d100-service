package dev.christianbroomfield.d100web.model.encounter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.christianbroomfield.d100web.util.fixture
import io.kotlintest.specs.DescribeSpec

class AdventuringDayTest : DescribeSpec() {
    private val mapper = ObjectMapper().registerKotlinModule()

    init {
        describe("An AdventuringDay object") {
            val encounterDay = AdventuringDay(
                weather = "Some lovely weather here!",
                events = listOf(
                    Event(TimeOfDay.Morning, EventType.Nothing, emptyList()),
                    Event(TimeOfDay.Afternoon, EventType.Encounter, listOf("A pack of goblins!")),
                    Event(TimeOfDay.Evening, EventType.Discovery, listOf("An abandoned castle!")),
                    Event(TimeOfDay.Dusk, EventType.Treasure, listOf("A sparkling dagger!"))
                )
            )

            context("serialization") {
                it("serializes all values properly") {
                    val expected = mapper.writeValueAsString(
                        mapper.readValue(fixture("encounterDay.json"), AdventuringDay::class.java)
                    )

                    assertThat(
                        mapper.writeValueAsString(encounterDay),
                        equalTo(expected))
                }
            }

            context("deserialization") {
                it("deserializes all values properly") {
                    val expected = mapper.readValue(
                        fixture("encounterDay.json"),
                        AdventuringDay::class.java
                    )

                    assertThat(
                        encounterDay,
                        equalTo(expected))
                }
            }
        }
    }
}
