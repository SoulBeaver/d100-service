package dev.christianbroomfield.d100web.model.encounter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.christianbroomfield.d100web.util.fixture
import io.kotlintest.specs.DescribeSpec

class EncounterVariantsTest : DescribeSpec() {
    private val mapper = ObjectMapper().registerKotlinModule()

    init {
        describe("An EncounterVariants object") {
            val encounterDay = EncounterVariants(
                variants = listOf(
                    EncounterVariant(Difficulty.Easy, "Easy", "/${Difficulty.Easy}"),
                    EncounterVariant(Difficulty.Challenging, "Challenging", "/${Difficulty.Challenging}"),
                    EncounterVariant(Difficulty.Harsh, "Harsh", "/${Difficulty.Harsh}"),
                    EncounterVariant(Difficulty.Unfair, "Unfair", "/${Difficulty.Unfair}"),
                    EncounterVariant(Difficulty.Hellscape, "Hellscape", "/${Difficulty.Hellscape}")
                )
            )

            context("serialization") {
                it("serializes all values properly") {
                    val expected = mapper.writeValueAsString(
                        mapper.readValue(fixture("encounterVariants.json"), EncounterVariants::class.java)
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
                        EncounterVariants::class.java
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