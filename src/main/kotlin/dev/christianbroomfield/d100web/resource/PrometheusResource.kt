package dev.christianbroomfield.d100web.resource

import io.micrometer.prometheus.PrometheusMeterRegistry
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class PrometheusResource(private val registry: PrometheusMeterRegistry) {
    operator fun invoke() = metrics()

    private fun metrics(): HttpHandler = { request: Request ->
        val metrics = registry.scrape()

        metrics.byteInputStream().use {
            Response(Status.OK).body(it, metrics.length.toLong())
        }
    }
}