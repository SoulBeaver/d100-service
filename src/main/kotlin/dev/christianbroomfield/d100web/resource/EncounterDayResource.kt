package dev.christianbroomfield.d100web.resource

import dev.christianbroomfield.d100web.model.encounter.Difficulty
import dev.christianbroomfield.d100web.model.encounter.EncounterDay
import dev.christianbroomfield.d100web.service.EncounterDayService
import mu.KotlinLogging
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel

private val log = KotlinLogging.logger {}
class EncounterDayResource(
    private val encounterDayService: EncounterDayService
) {
    operator fun invoke() = routes(
        "/" bind Method.GET to encounterDayVariants(),
        "/experimental" bind Method.GET to encounterDay(),
        "/${Difficulty.Easy}" bind Method.GET to generateEncounterDayTextAdventure(Difficulty.Easy),
        "/${Difficulty.Challenging}" bind Method.GET to generateEncounterDayTextAdventure(Difficulty.Challenging),
        "/${Difficulty.Harsh}" bind Method.GET to generateEncounterDayTextAdventure(Difficulty.Harsh),
        "/${Difficulty.Unfair}" bind Method.GET to generateEncounterDayTextAdventure(Difficulty.Unfair),
        "/${Difficulty.Hellscape}" bind Method.GET to generateEncounterDayTextAdventure(Difficulty.Hellscape)
    )

    private fun encounterDayVariants() = { request: Request ->
        val difficultyList = "Please choose from the following list to customize the challenge of your adventuring day:\n\n" +
                "Easy - For civilized and peaceful areas:  http://www.theadventuringday/day/Easy\n\n" +
                "Challenging - For the frontier and dangerous wilderness:  http://www.theadventuringday/day/Challenging\n\n" +
                "Harsh - For walking in enemy territory:  http://www.theadventuringday/day/Harsh\n\n" +
                "Unfair - For being actively targeted or insile a hostile Demiplane:  http://www.theadventuringday/day/Unfair\n\n" +
                "Hellscape - For when your party is literally trapped in hell:  http://www.theadventuringday/day/Hellscape"

        Response(Status.OK).body(difficultyList)
    }

    private fun generateEncounterDayTextAdventure(difficulty: Difficulty) = { request: Request ->
        val encounterDay = encounterDayService.generateEncounterDay(difficulty.threshold)

        val text = "Weather report:${encounterDay.weather}\n" +
                "=======================================\n" +
                "Morning:\n\n${encounterDay.morningEvent}\n" +
                "=======================================\n" +
                "Afternoon:\n\n${encounterDay.afternoonEvent}\n" +
                "=======================================\n" +
                "Evening:\n\n${encounterDay.eveningEvent}\n" +
                "=======================================\n" +
                "Dusk:\n\n${encounterDay.duskEvent}\n" +
                "=======================================\n" +
                "Midnight:\n\n${encounterDay.midnightEvent}\n" +
                "=======================================\n" +
                "Predawn:\n\n${encounterDay.predawnEvent}\n" +
                "=======================================\n"

        Response(Status.OK).body(text)
    }

    private fun encounterDay() = { request: Request ->
        val encounterDay = encounterDayService.generateEncounterDay(Difficulty.Unfair.threshold)

        val renderer = HandlebarsTemplates().HotReload("src/docs")
        val view = Body.viewModel(renderer, ContentType.TEXT_HTML).toLens()

        Response(Status.OK).with(view of encounterDay)
    }
}