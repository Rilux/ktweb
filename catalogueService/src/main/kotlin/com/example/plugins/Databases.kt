package com.example.plugins

import com.example.data.CatalogueRepositoryCachedImpl
import com.example.data.CatalogueRepositoryImpl
import com.example.domain.CatalogueRepository
import com.example.domain.model.CatalogueItemExposed
import com.example.domain.model.SortStyle
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File


fun Application.configureDatabases() {
    val catalogueService: CatalogueRepository = CatalogueRepositoryCachedImpl(
        CatalogueRepositoryImpl(),
        File(environment.config.property("storage.ehcacheFilePath").getString())
    )


    routing {
        //add catalogue item
        post("/catalogue") {
            val item = call.receive<CatalogueItemExposed>()
            val id = catalogueService.addItemToCatalogue(item)
            call.respond(HttpStatusCode.Created, id)
        }

        // Delete catalogue item
        delete("/catalogue/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            catalogueService.deleteItem(id)
            call.respond(HttpStatusCode.OK)
        }

        // Get catalogue items
        get("/catalogue") {

            val sort = try {
                SortStyle.valueOf(call.request.queryParameters["sortBy"] ?: "")
            } catch (e: Exception) {
                null
            }

            val skip = try {
                call.request.queryParameters["skip"]?.toInt()
            } catch (e: Exception) {
                null
            }

            val limit = try {
                call.request.queryParameters["limit"]?.toInt()
            } catch (e: Exception) {
                null
            }

            val catalogue = catalogueService.getAllItems(skip = skip, limit = limit, sort = sort)
            if (catalogue.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, catalogue)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
