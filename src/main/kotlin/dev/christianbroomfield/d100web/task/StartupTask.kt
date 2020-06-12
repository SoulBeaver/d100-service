package dev.christianbroomfield.d100web.task

import com.beust.klaxon.Klaxon
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import dev.christianbroomfield.d100web.model.TableGroup
import java.io.File
import mu.KotlinLogging
import org.litote.kmongo.getCollection

private val log = KotlinLogging.logger {}

object StartupTask {
    operator fun invoke(mongodb: MongoClient, dataDirectoryPath: String) {
        val tableGroupDatabase = mongodb.getDatabase("tables")
        val tableGroupCollection = tableGroupDatabase.getCollection<TableGroup>()

        tableGroupCollection.drop()

        with(File(dataDirectoryPath)) {
            parseDir(tableGroupCollection, this, "default")
            parseDir(tableGroupCollection, this.resolve("encounter"), "encounter")
            parseDir(tableGroupCollection, this.resolve("discovery"), "discovery")
            parseDir(tableGroupCollection, this.resolve("treasure"), "treasure")
        }
    }

    private fun parseDir(collection: MongoCollection<TableGroup>, dir: File, prefix: String) {
        require(dir.listFiles().isNotEmpty()) {
            "Cannot create an adventuring day without entries for $prefix! No tables found in ${dir.absolutePath}"
        }

        for (file in dir.listFiles()) {
            log.info("Reading file ${file.name}")

            if (file.isDirectory) continue

            val tableGroup = TableGroup(
                name = "${prefix}_${file.nameWithoutExtension}",
                tables = Klaxon().parseArray(file)!!
            )

            collection.insertOne(tableGroup)
        }
    }
}
