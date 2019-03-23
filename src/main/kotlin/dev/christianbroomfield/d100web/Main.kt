package dev.christianbroomfield.d100web

import dev.christianbroomfield.d100web.resource.PingResource
import dev.christianbroomfield.d100web.resource.TableResource
import dev.christianbroomfield.d100web.service.TableService
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.litote.kmongo.KMongo

data class MongoConfiguration(
    val host: String = "127.0.0.1",
    val port: Int = 27017
)

data class D100Configuration(
    val port: Int = 9000,
    val mongo: MongoConfiguration = MongoConfiguration()
)

fun main(args: Array<String>) {
    d100Server(D100Configuration()).start()
}

fun d100Server(config: D100Configuration) = D100App(config).asServer(Undertow(config.port))

object D100App {
    operator fun invoke(config: D100Configuration): HttpHandler {
        val mongodb = KMongo.createClient(config.mongo.host, config.mongo.port)

        val tableService = TableService(mongodb)
        val tableResource = TableResource(tableService)

        val pingResource = PingResource()

        return DebuggingFilters
            .PrintRequestAndResponse()
            .then(ServerFilters.CatchLensFailure)
            .then(
                routes(
                    "/ping" bind pingResource(),
                    "/table" bind tableResource()
                )
            )
    }
}