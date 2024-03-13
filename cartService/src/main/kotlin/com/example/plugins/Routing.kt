package com.example.plugins

import com.example.connection.RetrofitHelper
import com.example.data.CartRepositoryCachedImpl
import com.example.data.CartRepositoryImpl
import com.example.domain.CartRepository
import com.example.domain.model.CartModelWithDetailsExposed
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {

    val service = RetrofitHelper.getService()

    val cartDb: CartRepository = CartRepositoryCachedImpl(
        CartRepositoryImpl(),
        File(environment.config.property("storage.ehcacheFilePath").getString())
    )

    routing {

        put("/cart/{userId}/{cartItemId}") {
            val userId = call.parameters["userId"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val cartItemId = call.parameters["cartItemId"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val item = cartDb.addItemToCart(userId, cartItemId)
            call.respond(HttpStatusCode.Accepted, item)
        }

        get("/cart/{userId}") {
            val userId = call.parameters["userId"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val items = cartDb.getAllForUser(userId)
            val resultList = mutableListOf<CartModelWithDetailsExposed>()
            items.forEach {
                val catalogueItem = service.getCatalogueItem(it)
                if(catalogueItem != null) {
                    val item = CartModelWithDetailsExposed(
                        id = it,
                        title = catalogueItem.title,
                        description = catalogueItem.description,
                        imageUrl = catalogueItem.imageUrl,
                    )
                    resultList.add(item)
                }
            }

            call.respond(HttpStatusCode.OK, resultList)
        }

        delete("/cart/{userId}") {
            val userId = call.parameters["userId"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            cartDb.clearCartForUser(userId)
            call.respond(HttpStatusCode.OK)
        }

        delete("/cart/{userId}/{cartItemId}") {
            val userId = call.parameters["userId"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val cartItemId = call.parameters["cartItemId"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            cartDb.deleteItemFromCart(userId, cartItemId)
            call.respond(HttpStatusCode.OK)
        }
    }
}
