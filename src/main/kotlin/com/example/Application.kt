package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8081) {
        routing {
            get("/{...}") {
                val client = HttpClient(CIO)
                val path = call.request.path()
                val response: HttpResponse = when {
                    path.startsWith("/catalogue") -> client.get("http://localhost:8090")
                    path.startsWith("/cart") -> client.get("http://localhost:808}")
                    else -> client.get("http://localhost:8081/notfound")
                }
                call.respondText(response.bodyAsText(), contentType = response.contentType(), status = response.status)
            }
        }
    }.start(wait = true)
    EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
