package com.example.data

import org.jetbrains.exposed.sql.Table

object CatalogueDtoModel : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", length = 100)
    val description = varchar("description", 10000)
    val imagePath = varchar("image_path", 100)

    override val primaryKey = PrimaryKey(id)
}
