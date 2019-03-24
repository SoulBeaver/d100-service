package dev.christianbroomfield.d100web

data class D100Configuration(
    val debug: Boolean = false,
    val port: Int = 9000,
    val mongo: MongoConfiguration = MongoConfiguration()
)

data class MongoConfiguration(
    val host: String = "127.0.0.1",
    val port: Int = 27017
)