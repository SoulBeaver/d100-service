package dev.christianbroomfield.d100web.service

import com.mongodb.MongoClient
import dev.christianbroomfield.d100web.model.TableGroup
import dev.christianbroomfield.d100web.model.TableGroupName
import kotlin.random.Random
import mu.KotlinLogging
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.regex

private val log = KotlinLogging.logger {}

class TableService(private val client: MongoClient) {
    private val tableGroupDatabase = client.getDatabase("tables")
    private val tableGroupCollection = tableGroupDatabase.getCollection<TableGroup>()

    fun getAll(): List<TableGroup> {
        return tableGroupCollection.find().toList().also {
            log.debug { "getAll: $it" }
        }
    }

    fun get(tableGroupName: TableGroupName): TableGroup? {
        return tableGroupCollection.findOne(TableGroup::name eq tableGroupName.name).also {
            log.debug { "get $tableGroupName: $it" }
        }
    }

    fun getOneOf(prefix: String): TableGroup {
        val matchingTables = tableGroupCollection.find(TableGroup::name regex prefix).toList()
        return matchingTables[Random.nextInt(matchingTables.size)].also {
            log.debug { "getOneOf for $prefix: $it" }
        }
    }

    fun create(table: TableGroup): TableGroup {
        tableGroupCollection.insertOne(table)

        return table.also {
            log.debug { "create $table: $it" }
        }
    }

    fun delete(tableGroupName: TableGroupName): TableGroup? {
        return tableGroupCollection.findOneAndDelete(TableGroup::name eq tableGroupName.name).also {
            log.debug { "delete $tableGroupName: $it" }
        }
    }
}
