package dev.christianbroomfield.d100web.resource

import dev.christianbroomfield.d100web.model.encounter.Difficulty
import dev.christianbroomfield.d100web.model.encounter.EncounterVariant
import dev.christianbroomfield.d100web.model.encounter.EncounterVariants
import dev.christianbroomfield.d100web.service.EncounterDayService
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
class EncounterDayResource(
    private val encounterDayService: EncounterDayService
) {
    operator fun invoke() = routes(
        "/" bind Method.GET to encounterDayVariants(),
        "/${Difficulty.Easy}" bind Method.GET to encounterDay(Difficulty.Easy),
        "/${Difficulty.Challenging}" bind Method.GET to encounterDay(Difficulty.Challenging),
        "/${Difficulty.Harsh}" bind Method.GET to encounterDay(Difficulty.Harsh),
        "/${Difficulty.Unfair}" bind Method.GET to encounterDay(Difficulty.Unfair),
        "/${Difficulty.Hellscape}" bind Method.GET to encounterDay(Difficulty.Hellscape)
    )

    private fun encounterDayVariants() = { request: Request ->
        val encounterVariants = EncounterVariants(
            listOf(
                EncounterVariant(Difficulty.Easy, "For civilized and peaceful areas", "/day/${Difficulty.Easy}"),
                EncounterVariant(Difficulty.Challenging, "For the frontier and dangerous wilderness", "/day/${Difficulty.Challenging}"),
                EncounterVariant(Difficulty.Harsh, "For walking in enemy territory", "/day/${Difficulty.Harsh}"),
                EncounterVariant(Difficulty.Unfair, "For being actively targeted or inside a hostile Demiplane", "/day/${Difficulty.Unfair}"),
                EncounterVariant(Difficulty.Hellscape, "For when your party is literally trapped in hell", "/day/${Difficulty.Hellscape}")
            )
        )

        val renderer = HandlebarsTemplates().HotReload("src/docs")
        val view = Body.viewModel(renderer, ContentType.TEXT_HTML).toLens()

        Response(Status.OK).with(view of encounterVariants)
    }

    private fun encounterDay(difficulty: Difficulty) = { request: Request ->
        val encounterDay = encounterDayService.generateEncounterDay(difficulty.threshold)

        val renderer = HandlebarsTemplates().HotReload("src/docs")
        val view = Body.viewModel(renderer, ContentType.TEXT_HTML).toLens()

        Response(Status.OK).with(view of encounterDay)
    }
}