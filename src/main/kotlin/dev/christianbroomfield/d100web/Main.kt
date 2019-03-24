package dev.christianbroomfield.d100web

import com.xenomachina.argparser.ArgParser
import dev.christianbroomfield.d100web.app.App
import org.http4k.server.Undertow
import org.http4k.server.asServer

fun main(args: Array<String>) {
    ArgParser(args).parseInto(::CommandLineArgs).run {
        val config = when {
            debug -> D100Configuration(debug = true)
            else -> D100Configuration()
        }

        d100Server(config).start()
    }
}

fun d100Server(config: D100Configuration) = App(config).asServer(Undertow(config.port))