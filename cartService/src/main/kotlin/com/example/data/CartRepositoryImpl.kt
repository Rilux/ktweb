package com.example.data

import com.example.data.DatabaseSingleton.dbQuery
import com.example.domain.CartRepository
import com.example.domain.model.CartDtoModel
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class CartRepositoryImpl : CartRepository {
    override suspend fun addItemToCart(newUserId: Int, newItemId: Int): Int = dbQuery {
        CartDtoModel.insert {
            it[userId] = newUserId
            it[itemId] = newItemId
        }[CartDtoModel.id]
    }

    override suspend fun getAllForUser(userId: Int): List<Int> {
        return dbQuery {
            CartDtoModel.select {
                CartDtoModel.userId eq userId
            }                .map {
                    it[CartDtoModel.itemId]
                }
        }
    }

    override suspend fun clearCartForUser(userId: Int) {
        dbQuery {
            CartDtoModel.deleteWhere {
                CartDtoModel.userId.eq(userId)
            }
        }
    }

    override suspend fun deleteItemFromCart(userId: Int, itemId: Int) {
        dbQuery {
            CartDtoModel.deleteWhere {
                CartDtoModel.userId.eq(userId)
                CartDtoModel.itemId.eq(itemId)
            }
        }
    }
}