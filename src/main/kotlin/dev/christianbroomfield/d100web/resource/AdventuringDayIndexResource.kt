package dev.christianbroomfield.d100web.resource

import dev.christianbroomfield.d100web.model.encounter.AdventuringDayVariant
import dev.christianbroomfield.d100web.model.encounter.AdventuringDayVariants
import dev.christianbroomfield.d100web.model.encounter.Difficulty
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
class AdventuringDayIndexResource {
    operator fun invoke() = routes(
        "/" bind Method.GET to adventuringDayVariants()
    )

    private fun adventuringDayVariants() = { _: Request ->
        val adventuringDayVariants = AdventuringDayVariants(
            listOf(
                AdventuringDayVariant(Difficulty.Easy, "For civilized and peaceful areas", "/day/${Difficulty.Easy}"),
                AdventuringDayVariant(Difficulty.Challenging, "For the frontier and dangerous wilderness", "/day/${Difficulty.Challenging}"),
                AdventuringDayVariant(Difficulty.Harsh, "For walking in enemy territory", "/day/${Difficulty.Harsh}"),
                AdventuringDayVariant(Difficulty.Unfair, "For being actively targeted or inside a hostile Demiplane", "/day/${Difficulty.Unfair}"),
                AdventuringDayVariant(Difficulty.Hellscape, "For when your party is literally trapped in hell", "/day/${Difficulty.Hellscape}")
            )
        )

        val renderer = HandlebarsTemplates().HotReload("src/docs")
        val view = Body.viewModel(renderer, ContentType.TEXT_HTML).toLens()

        Response(Status.OK).with(view of adventuringDayVariants)
    }
}
