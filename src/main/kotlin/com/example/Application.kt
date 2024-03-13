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
import io.ktor.util.*

fun main(args: Array<String>) {

    embeddedServer(Netty, port = 8081) {
        routing {
            // Handle GET requests
            get("/{...}") {
                forwardRequest(call, HttpMethod.Get)
            }

            // Handle POST requests
            post("/{...}") {
                forwardRequest(call, HttpMethod.Post)
            }

            // Handle PUT requests
            put("/{...}") {
                forwardRequest(call, HttpMethod.Put)
            }

            // Handle DELETE requests
            delete("/{...}") {
                forwardRequest(call, HttpMethod.Delete)
            }
        }
    }.start(wait = true)
    EngineMain.main(args)
}

@OptIn(InternalAPI::class)
suspend fun forwardRequest(call: ApplicationCall, method: HttpMethod) {
    val client = HttpClient(CIO)
    val path = call.request.path()
    val targetUrl = when {
        path.startsWith("/catalogue") -> "http://localhost:8090${path.removePrefix("/service1")}"
        path.startsWith("/cart") -> "http://localhost:8080${path.removePrefix("/service2")}"
        else -> null
    }

    // Initialize receivedContent outside the lambda
    val receivedContent = if (method == HttpMethod.Post || method == HttpMethod.Put) {
        call.receiveText() // Safe to call here
    } else {
        null
    }

    if (targetUrl != null) {
        val requestBuilder: HttpRequestBuilder.() -> Unit = {
            this.method = method
            this.url(targetUrl)
            headers {
                // Forward all received headers to the target service
                call.request.headers.forEach { key, values ->
                    values.forEach { value ->
                        append(key, value)
                    }
                }
            }

            // Add received content to the request if applicable
            if (receivedContent != null) {
                this.body = receivedContent
            }
        }

        try {
            val response: HttpResponse = client.request(requestBuilder)
            call.respondBytes(response.readBytes(), contentType = response.contentType(), status = response.status)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.ServiceUnavailable, "Failed to forward request: ${e.message}")
        }
    } else {
        call.respond(HttpStatusCode.NotFound, "Service not found")
    }
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
