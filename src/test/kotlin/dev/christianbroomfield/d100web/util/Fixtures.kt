package dev.christianbroomfield.d100web.util

import java.lang.IllegalArgumentException
import java.net.URL
import java.nio.charset.Charset

fun fixture(filename: String, charset: Charset = Charsets.UTF_8): String {
    return resource(filename).readText(charset).trim()
}

private fun resource(name: String): URL {
    val contextClassLoader = Thread.currentThread().contextClassLoader
    return contextClassLoader.getResource(name) ?: throw IllegalArgumentException("Resource $name could not be found.")
}