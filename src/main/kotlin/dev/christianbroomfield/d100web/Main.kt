package dev.christianbroomfield.d100web

import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.server.Undertow
import org.http4k.server.asServer

fun main() {
    val app = { request: Request -> Response(OK).body("Hello, ${request.query("name")}!") }

    app.asServer(Undertow(9000)).start()
}