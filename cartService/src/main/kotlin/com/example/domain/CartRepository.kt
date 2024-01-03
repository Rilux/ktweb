package com.example.domain

interface CartRepository {
    suspend fun addItemToCart(newUserId: Int, newItemId: Int) : Int

    suspend fun getAllForUser(userId: Int): List<Int>

    suspend fun clearCartForUser(userId: Int)

    suspend fun deleteItemFromCart(userId: Int, itemId: Int)

}