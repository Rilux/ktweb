package com.example.data

import com.example.domain.CatalogueRepository
import com.example.domain.model.CatalogueItem
import com.example.domain.model.CatalogueItemExposed
import com.example.domain.model.SortStyle
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class CatalogueRepositoryImpl : CatalogueRepository {
    override suspend fun addItemToCatalogue(item: CatalogueItemExposed): Int = DatabaseSingleton.dbQuery {
        CatalogueDtoModel.insert {
            it[title] = item.title
            it[description] = item.description
            it[imagePath] = Url(item.imageUrl).pathSegments.last()
        }[CatalogueDtoModel.id]
    }

    override suspend fun getAllItems(skip: Int?, limit: Int?, sort: SortStyle?): List<CatalogueItem> {
        return DatabaseSingleton.dbQuery {
            CatalogueDtoModel.selectAll()
                .map {
                    CatalogueItem(
                        it[CatalogueDtoModel.id],
                        it[CatalogueDtoModel.title],
                        it[CatalogueDtoModel.description],
                        "http://127.0.0.1:8070/images" + "/" + it[CatalogueDtoModel.imagePath],
                    )
                }
        }
    }

    override suspend fun getCatalogueItem(id: Int): CatalogueItem? {
        return DatabaseSingleton.dbQuery {
            CatalogueDtoModel.select {
                CatalogueDtoModel.id eq id
            }.map {
                    CatalogueItem(
                        it[CatalogueDtoModel.id],
                        it[CatalogueDtoModel.title],
                        it[CatalogueDtoModel.description],
                        "http://127.0.0.1:8070/images" + "/" + it[CatalogueDtoModel.imagePath],
                    )
                }.firstOrNull()
        }
    }

    override suspend fun deleteItem(id: Int) {
        DatabaseSingleton.dbQuery {
            CatalogueDtoModel.deleteWhere {
                this.id.eq(id)
            }
        }
    }
}