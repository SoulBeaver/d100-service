package dev.christianbroomfield.d100web.model.encounter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.christianbroomfield.d100web.util.fixture
import io.kotlintest.specs.DescribeSpec

class AdventuringDayVariantsTest : DescribeSpec() {
    private val mapper = ObjectMapper().registerKotlinModule()

    init {
        describe("An AdventuringDayVariants object") {
            val encounterDay = AdventuringDayVariants(
                variants = listOf(
                    AdventuringDayVariant(Difficulty.Easy, "Easy", "/${Difficulty.Easy}"),
                    AdventuringDayVariant(Difficulty.Challenging, "Challenging", "/${Difficulty.Challenging}"),
                    AdventuringDayVariant(Difficulty.Harsh, "Harsh", "/${Difficulty.Harsh}"),
                    AdventuringDayVariant(Difficulty.Unfair, "Unfair", "/${Difficulty.Unfair}"),
                    AdventuringDayVariant(Difficulty.Hellscape, "Hellscape", "/${Difficulty.Hellscape}")
                )
            )

            context("serialization") {
                it("serializes all values properly") {
                    val expected = mapper.writeValueAsString(
                        mapper.readValue(fixture("encounterVariants.json"), AdventuringDayVariants::class.java)
                    )

                    assertThat(
                        mapper.writeValueAsString(encounterDay),
                        equalTo(expected)
                    )
                }
            }

            context("deserialization") {
                it("deserializes all values properly") {
                    val expected = mapper.readValue(
                        fixture("encounterVariants.json"),
                        AdventuringDayVariants::class.java
                    )

                    assertThat(
                        encounterDay,
                        equalTo(expected)
                    )
                }
            }
        }
    }
}