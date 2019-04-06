package dev.christianbroomfield.d100web

data class D100Configuration(
    val debug: Boolean = false,
    val port: Int = 9000,
    val mongo: MongoConfiguration = MongoConfiguration(),
    val startup: StartupConfiguration = StartupConfiguration()
)

data class MongoConfiguration(
    val host: String = "127.0.0.1",
    val port: Int = 27017
)

data class StartupConfiguration(
    val enabled: Boolean = false,
    val clearCollection: Boolean = true,
    val dataDirectory: String = ""
)