package dev.christianbroomfield.d100web.resource

import dev.christianbroomfield.d100web.model.encounter.Difficulty
import dev.christianbroomfield.d100web.service.AdventuringDayService
import mu.KotlinLogging
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel

private val log = KotlinLogging.logger {}
class AdventuringDayResource(
    private val adventuringDayService: AdventuringDayService
) {
    operator fun invoke() = routes(
        "/${Difficulty.Easy}" bind Method.GET to adventuringDay(Difficulty.Easy),
        "/${Difficulty.Challenging}" bind Method.GET to adventuringDay(Difficulty.Challenging),
        "/${Difficulty.Harsh}" bind Method.GET to adventuringDay(Difficulty.Harsh),
        "/${Difficulty.Unfair}" bind Method.GET to adventuringDay(Difficulty.Unfair),
        "/${Difficulty.Hellscape}" bind Method.GET to adventuringDay(Difficulty.Hellscape)
    )

    private fun adventuringDay(difficulty: Difficulty) = { _: Request ->
        val encounterDay = adventuringDayService.generateEncounterDay(difficulty.threshold)

        val renderer = HandlebarsTemplates().HotReload("src/docs")
        val view = Body.viewModel(renderer, ContentType.TEXT_HTML).toLens()

        Response(Status.OK).with(view of encounterDay)
    }
}