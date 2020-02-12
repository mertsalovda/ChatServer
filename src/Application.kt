package ru.mertsalovda.ktor.chatserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.request.receiveText
import io.ktor.server.engine.*
import io.ktor.server.netty.Netty
import ru.mertsalovda.ktor.chatserver.data.model.User
import ru.mertsalovda.ktor.chatserver.data.repository.IRepository
import ru.mertsalovda.ktor.chatserver.data.repository.MapUsersRepositoryImpl

fun main(args: Array<String>) {
    val repository: IRepository<User> = MapUsersRepositoryImpl()

    val server = embeddedServer(Netty, host = "127.0.0.1", port = 8080) {
        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }
        install(Authentication) {
            basic {
                validate {
                    val user = repository.getAll().first { user -> user.name == it.name }
                    if (it.name == user.name && it.password == user.password) {
                        UserIdPrincipal(it.name)
                    } else throw AuthenticationException("Wrong login or password")
                }
            }
        }
        install(StatusPages) {
            exception<AuthenticationException> { exception ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to (exception.message ?: "")))
            }
            exception<RegistrationException> { exception ->
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (exception.message ?: "")))
            }
        }
        routing {
            authenticate {
                get("/users") {
                    val result = repository.getAll()
                    call.respond(HttpStatusCode.OK, result)
                }
            }

            post("/login") {
                val ul = call.receive<UserLogin>()
                val result = repository.insertItem(User(ul.name, ul.password))
                if (result) {
                    call.respond(HttpStatusCode.Created)
                } else {
                    throw RegistrationException("""Пользователь с именем "${ul.name}" уже существует.""")
                }
            }

        }

    }
    server.start(wait = true)
}

data class UserLogin(val name: String, val password: String)

class AuthenticationException(message: String) : RuntimeException(message)
class RegistrationException(message: String) : RuntimeException(message)