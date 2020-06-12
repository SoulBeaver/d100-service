package dev.christianbroomfield.d100web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.equalTo
import dev.christianbroomfield.d100web.model.TableGroup
import dev.christianbroomfield.d100web.util.EmbeddedMongoDB
import dev.christianbroomfield.d100web.util.answerShouldBe
import dev.christianbroomfield.d100web.util.fixture
import dev.christianbroomfield.d100web.util.tableGroup
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.specs.DescribeSpec
import org.http4k.client.OkHttp
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status
import org.litote.kmongo.getCollection

class ServerTest : DescribeSpec() {
    private val mapper = ObjectMapper().registerKotlinModule()

    private val embeddedMongod = EmbeddedMongoDB()

    private val port = 8888
    private val client = OkHttp()
    private val server =
        d100Server(D100Configuration(port = port, mongo = MongoConfiguration(host = "localhost", port = 12345)))

    override fun listeners(): List<TestListener> {
        return listOf(embeddedMongod)
    }

    override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
        server.start()
    }

    override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
        server.stop()
    }

    init {
        describe("querying the tables resource") {
            context("no tables exist") {
                it("returns an empty list") {
                    val response = client(Request(GET, "http://localhost:$port/table"))

                    response.answerShouldBe("[]")
                }
            }

            it("returns all existing tables") {
                val collection = collection()

                collection.insertOne(tableGroup("1"))
                collection.insertOne(tableGroup("2"))
                collection.insertOne(tableGroup("3"))

                val response = client(Request(GET, "http://localhost:$port/table"))

                assertThat(
                    mapper.readValue(response.bodyString(), List::class.java),
                    equalTo(mapper.readValue(fixture("tableGroups.json"), List::class.java))
                )
            }
        }

        describe("querying a specific tableGroup") {
            context("the tableGroup doesn't exist") {
                it("returns 404 NOT FOUND") {
                    val response = client(Request(GET, "http://localhost:$port/table/test"))

                    assertThat(response.status, equalTo(Status.NOT_FOUND))
                }
            }

            it("returns the tableGroup") {
                val collection = collection()
                collection.insertOne(tableGroup())

                val response = client(Request(GET, "http://localhost:$port/table/test"))

                assertThat(
                    mapper.readValue(response.bodyString(), TableGroup::class.java),
                    equalTo(tableGroup())
                )
            }
        }

        describe("creating a new tableGroup") {
            it("creates a new table") {
                val response = client(Request(POST, "http://localhost:$port/table").body(fixture("tableGroup.json")))

                assertThat(
                    mapper.readValue(response.bodyString(), TableGroup::class.java),
                    equalTo(mapper.readValue(fixture("tableGroup.json"), TableGroup::class.java))
                )
            }
        }

        describe("deleting a tableGroup") {
            context("the tableGroup doesn't exist") {
                it("returns 404 NOT FOUND") {
                    val response = client(Request(DELETE, "http://localhost:$port/table/foo"))

                    assertThat(response.status, equalTo(Status.NOT_FOUND))
                }
            }

            it("deletes the tableGroup") {
                val collection = collection()
                collection.insertOne(tableGroup())

                val response = client(Request(DELETE, "http://localhost:$port/table/test"))

                assertThat(
                    mapper.readValue(response.bodyString(), TableGroup::class.java),
                    equalTo(tableGroup())
                )
            }
        }

        describe("sending a ping") {
            it("returns pong") {
                client(Request(GET, "http://localhost:$port/ping")).answerShouldBe("pong")
            }
        }

        describe("scraping metrics") {
            it("returns a list of all metrics that includes a count and timer") {
                val response = client(Request(GET, "http://localhost:$port/prometheus"))

                assertThat(response.status, equalTo(Status.OK))
                assertThat(response.bodyString(), containsSubstring("# HELP http_server_request_count_total Total number of server requests"))
                assertThat(response.bodyString(), containsSubstring("# HELP http_server_request_latency_seconds_max Timing of server requests"))
                assertThat(response.bodyString(), containsSubstring("# HELP http_server_request_latency_seconds Timing of server requests"))
            }
        }
    }

    private fun collection() = embeddedMongod.mongoClient!!.getDatabase("tables").getCollection<TableGroup>()
}
