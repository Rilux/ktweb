package com.example

import com.example.data.DatabaseSingleton
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    DatabaseSingleton.init(environment.config)
    configureHTTP()
    configureSerialization()
    configureRouting()
}
