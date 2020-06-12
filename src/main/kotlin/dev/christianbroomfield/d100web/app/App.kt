package dev.christianbroomfield.d100web.app

import dev.christianbroomfield.d100web.D100Configuration
import dev.christianbroomfield.d100web.resource.AdventuringDayIndexResource
import dev.christianbroomfield.d100web.resource.AdventuringDayResource
import dev.christianbroomfield.d100web.resource.PingResource
import dev.christianbroomfield.d100web.resource.PrometheusResource
import dev.christianbroomfield.d100web.resource.TableResource
import dev.christianbroomfield.d100web.service.AdventuringDayService
import dev.christianbroomfield.d100web.service.TableService
import dev.christianbroomfield.d100web.task.StartupTask
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import mu.KotlinLogging
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.HttpTransaction
import org.http4k.core.Method
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.MetricFilters
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.litote.kmongo.KMongo

private val log = KotlinLogging.logger {}
object App {
    operator fun invoke(config: D100Configuration): HttpHandler {
        val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

        val mongodb = KMongo.createClient(config.mongo.host, config.mongo.port)

        val tableService = TableService(mongodb)
        val tableResource = TableResource(tableService)

        val encounterDayService = AdventuringDayService(tableService)
        val adventuringDayResource = AdventuringDayResource(encounterDayService)
        val adventuringDayIndexResource = AdventuringDayIndexResource()

        val pingResource = PingResource()
        val prometheusResource = PrometheusResource(registry)

        if (config.startup.enabled) {
            StartupTask(mongodb, config.startup.dataDirectory)
        }

        return assembleFilters(config, registry).then(
            routes(
                "/" bind adventuringDayIndexResource(),
                "/day" bind adventuringDayResource(),
                "/table" bind tableResource(),

                "/ping" bind pingResource(),
                "/prometheus" bind Method.GET to prometheusResource()
            )
        )
    }

    private fun assembleFilters(config: D100Configuration, registry: MeterRegistry): Filter {
        val filter = when {
            config.debug -> DebuggingFilters
                .PrintRequestAndResponse()
                .then(ServerFilters.CatchLensFailure)

            else -> ServerFilters.CatchLensFailure
        }

        return filter
            .then(MetricFilters.Server.RequestCounter(registry))
            .then(MetricFilters.Server.RequestTimer(registry))
            .then(ResponseFilters.ReportHttpTransaction { tx: HttpTransaction ->
                log.info { "${tx.request.uri} ${tx.response.status}; took ${tx.duration.toMillis()}ms" }
            })
    }
}
