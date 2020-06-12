package dev.christianbroomfield.d100web.util

import dev.christianbroomfield.d100.model.Table
import dev.christianbroomfield.d100.model.TableHeader
import dev.christianbroomfield.d100web.model.TableGroup

fun tableGroup(name: String = "test"): TableGroup {
    return TableGroup(name,
        listOf(Table.PreppedTable(
            TableHeader(1, 1, "Test Header"),
            listOf("Test Result")
        )))
}
