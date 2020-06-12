package dev.christianbroomfield.d100web.service

import com.mongodb.MongoClient
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoCursor
import com.mongodb.client.MongoDatabase
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.christianbroomfield.d100.model.Table
import dev.christianbroomfield.d100.model.TableHeader
import dev.christianbroomfield.d100web.model.TableGroup
import dev.christianbroomfield.d100web.model.TableGroupName
import io.kotlintest.Spec
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.shouldThrow
import io.kotlintest.specs.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import org.bson.conversions.Bson
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

class TableServiceTest : DescribeSpec() {
    private val mongoClient = mockk<MongoClient>()
    private val database = mockk<MongoDatabase>()
    private val collection = mockk<MongoCollection<TableGroup>>()

    override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
        every { mongoClient.getDatabase("tables") } returns database
        every { database.getCollection<TableGroup>() } returns collection
    }

    init {
        describe("A TableService") {
            val service = TableService(mongoClient)

            context("Getting an object with a fully-qualified name") {
                context("That does not exist") {
                    every {
                        collection.findOne(any<Bson>())
                    } returns null

                    it("Returns null") {
                        val expected = service.get(TableGroupName("unknown"))

                        assertThat(expected == null, equalTo(true))
                    }
                }

                it("Returns the TableGroup for the given object") {
                    val tableGroup = TableGroup("weather", emptyList())

                    every {
                        collection.findOne(any<Bson>())
                    } returns tableGroup

                    val expected = service.get(TableGroupName("weather"))

                    assertThat(expected, equalTo(tableGroup))
                }
            }

            context("Getting an object based on a prefix") {
                context("That does not exist") {
                    val mockIterable = mockk<FindIterable<TableGroup>>(relaxed = true)

                    every {
                        collection.find(any<Bson>())
                    } returns mockIterable

                    val mockCursor = mockk<MongoCursor<TableGroup>>(relaxed = true)

                    every {
                        mockIterable.iterator()
                    } returns mockCursor

                    every {
                        mockCursor.hasNext()
                    } returns false

                    it("Throws an exception") {
                        shouldThrow<IllegalArgumentException> {
                            service.getOneOf("unknown_")
                        }
                    }
                }

                it("Returns a randomly chosen table with the given prefix") {
                    val expected = TableGroup(
                        name = "encounter_goblins",
                        tables = listOf(Table.PreppedTable(
                            header = TableHeader(1, 1, ""),
                            results = listOf("A pack of goblins!")
                        )))

                    val mockIterable = mockk<FindIterable<TableGroup>>(relaxed = true)

                    every {
                        collection.find(any<Bson>())
                    } returns mockIterable

                    val mockCursor = mockk<MongoCursor<TableGroup>>(relaxed = true)

                    every {
                        mockIterable.iterator()
                    } returns mockCursor

                    every {
                        mockCursor.hasNext()
                    } returnsMany listOf(true, false)

                    every {
                        mockCursor.next()
                    } returns expected

                    val actual = service.getOneOf("encounter_")

                    assertThat(actual, equalTo(expected))
                }
            }
        }
    }
}
