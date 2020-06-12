package dev.christianbroomfield.d100web.model

import dev.christianbroomfield.d100.model.Table

data class TableGroup(val name: String, val tables: List<Table.PreppedTable>)
