package dev.christianbroomfield.d100web

import com.xenomachina.argparser.ArgParser
import dev.christianbroomfield.d100web.app.App
import org.http4k.server.Undertow
import org.http4k.server.asServer

fun main(args: Array<String>) {
    ArgParser(args).parseInto(::CommandLineArgs).run {
        val config = D100Configuration(
            debug = debug,
            startup = when {
                data != null -> StartupConfiguration(enabled = true, dataDirectory = data!!)
                else -> StartupConfiguration()
            }
        )

        d100Server(config).start()
    }
}

fun d100Server(config: D100Configuration) =
    App(config).asServer(Undertow(config.port))
