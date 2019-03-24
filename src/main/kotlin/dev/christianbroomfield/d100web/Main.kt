package dev.christianbroomfield.d100web

import dev.christianbroomfield.d100web.resource.PrometheusResource
import dev.christianbroomfield.d100web.resource.PingResource
import dev.christianbroomfield.d100web.resource.TableResource
import dev.christianbroomfield.d100web.service.TableService
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.MetricFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.litote.kmongo.KMongo

fun main(args: Array<String>) {
    d100Server(D100Configuration()).start()
}

fun d100Server(config: D100Configuration) = D100App(config).asServer(Undertow(config.port))

object D100App {
    operator fun invoke(config: D100Configuration): HttpHandler {
        val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

        val mongodb = KMongo.createClient(config.mongo.host, config.mongo.port)

        val tableService = TableService(mongodb)
        val tableResource = TableResource(tableService)

        val pingResource = PingResource()
        val prometheusResource = PrometheusResource(registry)

        return DebuggingFilters
            .PrintRequestAndResponse()
            .then(MetricFilters.Server.RequestCounter(registry))
            .then(MetricFilters.Server.RequestTimer(registry))
            .then(ServerFilters.CatchLensFailure)
            .then(
                routes(
                    "/table" bind tableResource(),

                    "/ping" bind pingResource(),
                    "/prometheus" bind GET to prometheusResource()
                )
            )
    }
}