package dev.christianbroomfield.d100web.resource

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class PingResource {
    operator fun invoke() = ping()

    private fun ping(): HttpHandler = { request: Request ->
        Response(Status.OK).body("pong")
    }
}
