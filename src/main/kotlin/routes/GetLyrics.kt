package com.lrc.server.routes

import com.lrc.server.services.ExternalLyricsServer
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.lang.Exception

fun Application.getLyrics(externalLyricsServer: ExternalLyricsServer) {
    routing {
        get("api/v1/tracks/{id}/lyrics") {
            val trackId = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "error" to "id is null"
            )

            try {
                externalLyricsServer.getLyrics(trackId.toInt())!!.let {
                    call.respond(
                        it.toString(),
                    )
                }
            } catch (ex: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "error" to "error was happened, after send request on external api\n${ex.message}"
                )
            }
        }
    }
}