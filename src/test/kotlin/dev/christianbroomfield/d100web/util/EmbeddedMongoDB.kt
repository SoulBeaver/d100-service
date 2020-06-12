package dev.christianbroomfield.d100web.util

import com.mongodb.MongoClient
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener
import io.kotlintest.extensions.TopLevelTest
import org.litote.kmongo.KMongo

class EmbeddedMongoDB : TestListener {
    private lateinit var mongodExecutable: MongodExecutable
    private lateinit var mongod: MongodProcess
    var mongoClient: MongoClient? = null

    override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
        val starter = MongodStarter.getDefaultInstance()

        val host = "localhost"
        val port = 12345

        val mongodConfig = MongodConfigBuilder()
            .version(Version.Main.PRODUCTION)
            .net(Net(host, port, Network.localhostIsIPv6()))
            .build()

        this.mongodExecutable = starter.prepare(mongodConfig)
        this.mongod = mongodExecutable.start()
        this.mongoClient = KMongo.createClient(host, port)
    }

    override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
        mongoClient!!.dropDatabase("tables")

        this.mongod.stop()
        this.mongodExecutable.stop()
    }
}
