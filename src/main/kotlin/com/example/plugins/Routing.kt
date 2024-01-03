package com.example.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*

fun Application.configureRouting() {
    routing {
        post("/upload") { _ ->
            val multipart = call.receiveMultipart()
            var fileName: String? = null
            var text: String?= null
            try{
                multipart.forEachPart { partData ->
                    when(partData){
                        is PartData.FormItem -> {
                            if (partData.name == "text"){
                                text = partData.value
                            }
                        }
                        is PartData.FileItem ->{
                            fileName = partData.save("images")
                        }
                        is PartData.BinaryItem -> Unit
                        is PartData.BinaryChannelItem -> Unit
                    }
                }

                val imageUrl = "http://127.0.0.1:8070/images/$fileName"
                call.respond(HttpStatusCode.OK,imageUrl)
            } catch (ex: Exception) {
                File("http://127.0.0.1:8070/images/$fileName").delete()
                call.respond(HttpStatusCode.InternalServerError,"Error")
            }
        }

        get("images/{name}") {
            // get filename from request url
            val filename = call.parameters["name"]!!
            // construct reference to file
            // ideally this would use a different filename
            val file = File("images$filename")
            if(file.exists()) {
                call.respondFile(file)
            }
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}

fun PartData.FileItem.save(path: String): String {
    val fileBytes = streamProvider().readBytes()
    val fileExtension = originalFileName?.takeLastWhile { it != '.' }
    val fileName = UUID.randomUUID().toString() + "." + fileExtension
    val folder = File(path)
    folder.mkdir()
    println("Path = $path $fileName")
    File("$path$fileName").writeBytes(fileBytes)
    return fileName
}