package dev.christianbroomfield.d100web.service

import com.mongodb.MongoClient
import dev.christianbroomfield.d100web.model.TableGroup
import dev.christianbroomfield.d100web.model.TableGroupName
import org.litote.kmongo.getCollection
import org.litote.kmongo.findOne
import org.litote.kmongo.eq
import mu.KotlinLogging

private val log = KotlinLogging.logger {}
class TableService(private val client: MongoClient) {
    private val tableGroupDatabase = client.getDatabase("tables")
    private val tableGroupCollection = tableGroupDatabase.getCollection<TableGroup>()

    fun getAll(): List<TableGroup> {
        log.debug { "Getting all tables." }

        return tableGroupCollection.find().toList()
    }

    fun get(tableGroupName: TableGroupName): TableGroup? {
        log.debug { "Getting table with id $tableGroupName." }

        return tableGroupCollection.findOne(TableGroup::name eq tableGroupName.name)
    }

    fun create(table: TableGroup): TableGroup {
        log.debug { "Creating a new table with payload $table." }

        tableGroupCollection.insertOne(table)

        return table
    }

    fun delete(tableGroupName: TableGroupName): TableGroup? {
        log.debug { "Deleting table with id $tableGroupName" }

        return tableGroupCollection.findOneAndDelete(TableGroup::name eq tableGroupName.name)
    }
}