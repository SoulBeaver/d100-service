package dev.christianbroomfield.d100web.resource

import dev.christianbroomfield.d100web.model.encounter.EncounterDay
import dev.christianbroomfield.d100web.service.EncounterDayService
import mu.KotlinLogging
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.routes

private val log = KotlinLogging.logger {}
class EncounterDayResource(
    private val encounterDayService: EncounterDayService
) {
    private val encounterDayLens = Body.auto<EncounterDay>().toLens()

    operator fun invoke() = routes(
        "/text/{danger:.*}" bind Method.GET to generateEncounterDayTextAdventure(),
        "/{danger:.*}" bind Method.GET to generateEncounterDayTextAdventure()
    )

    private fun generateEncounterDay() = { request: Request ->
        val encounterDay = encounterDayService.generateEncounterDay(2)

        Response(Status.OK).with(encounterDayLens of encounterDay)
    }

    private fun generateEncounterDayTextAdventure() = { request: Request ->
        val encounterDay = encounterDayService.generateEncounterDay(2)

        val text = "Weather report: ${encounterDay.weather}\n" +
                "=======================================\n" +
                "Morning:\n\n ${encounterDay.morningEvent}\n" +
                "=======================================\n" +
                "Afternoon:\n\n ${encounterDay.afternoonEvent}\n" +
                "=======================================\n" +
                "Evening:\n\n ${encounterDay.eveningEvent}\n" +
                "=======================================\n" +
                "Dusk:\n\n ${encounterDay.duskEvent}\n" +
                "=======================================\n" +
                "Midnight:\n\n ${encounterDay.midnightEvent}\n" +
                "=======================================\n" +
                "Predawn:\n\n ${encounterDay.predawnEvent}\n" +
                "=======================================\n"

        Response(Status.OK).body(text)
    }
}