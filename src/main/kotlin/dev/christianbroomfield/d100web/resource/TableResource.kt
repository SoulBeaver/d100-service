package dev.christianbroomfield.d100web.resource

import dev.christianbroomfield.d100web.model.TableGroup
import dev.christianbroomfield.d100web.model.TableGroupName
import dev.christianbroomfield.d100web.service.TableService
import mu.KotlinLogging
import org.http4k.core.Body
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.string
import org.http4k.routing.bind
import org.http4k.routing.routes

private val log = KotlinLogging.logger {}

class TableResource(private val service: TableService) {
    private val pathTableGroupName = Path.string().of("name")

    private val tableGroupsLens = Body.auto<List<TableGroup>>().toLens()
    private val tableGroupLens = Body.auto<TableGroup>().toLens()

    operator fun invoke() = routes(
        "/" bind GET to getTableGroups(),
        "/{name:.*}" bind GET to getTableGroup(),
        "/" bind POST to createTableGroup(),
        "/{name:.*}" bind DELETE to deleteTableGroup()
    )

    private fun getTableGroups() = { request: Request ->
        val tables = service.getAll()

        Response(OK).with(tableGroupsLens of tables)
    }

    private fun getTableGroup() = { request: Request ->
        val tableGroupname = TableGroupName(pathTableGroupName(request))

        service.get(tableGroupname)?.let {
            Response(OK).with(tableGroupLens of it)
        } ?: Response(NOT_FOUND)
    }

    private fun createTableGroup() = { request: Request ->
        val tableGroup = tableGroupLens(request)

        Response(OK).with(tableGroupLens of service.create(tableGroup))
    }

    private fun deleteTableGroup() = { request: Request ->
        val tableGroupName = TableGroupName(pathTableGroupName(request))

        service.delete(tableGroupName)?.let {
            Response(OK).with(tableGroupLens of it)
        } ?: Response(NOT_FOUND)
    }
}
