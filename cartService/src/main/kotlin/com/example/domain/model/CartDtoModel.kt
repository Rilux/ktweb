package com.example.domain.model

import org.jetbrains.exposed.sql.Table

object CartDtoModel : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val itemId = integer("item_id")

    override val primaryKey = PrimaryKey(id)
}
