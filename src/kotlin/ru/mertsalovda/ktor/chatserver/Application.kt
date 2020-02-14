package ru.mertsalovda.ktor.chatserver

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.server.engine.*
import io.ktor.server.netty.Netty
import org.slf4j.event.Level
import ru.mertsalovda.ktor.chatserver.data.exeptions.AuthenticationException
import ru.mertsalovda.ktor.chatserver.data.exeptions.ExistException
import ru.mertsalovda.ktor.chatserver.data.exeptions.RegistrationException
import ru.mertsalovda.ktor.chatserver.data.model.Message
import ru.mertsalovda.ktor.chatserver.data.model.User
import ru.mertsalovda.ktor.chatserver.data.model.UserToken
import ru.mertsalovda.ktor.chatserver.data.repository.MapMessagesRepository
import ru.mertsalovda.ktor.chatserver.data.repository.MapUsersRepositoryImpl
import ru.mertsalovda.ktor.chatserver.data.repository.UserRepository
import ru.mertsalovda.ktor.chatserver.data.repository.MessageRepository

fun main() {
    val repository: UserRepository = MapUsersRepositoryImpl()
    val repoMessages: MessageRepository = MapMessagesRepository()

    val server = embeddedServer(Netty, host = "127.0.0.1", port = 8080) {
        install(CallLogging) {
            level = Level.INFO
        }
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
            exception<ExistException> { exception ->
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (exception.message ?: "")))
            }
        }
        routing {
            authenticate {
                get("/users") {
                    val result = repository.getAll()
                    call.respond(HttpStatusCode.OK, result)
                    call.info(result)
                }
                post("/user/token") {
                    val userToken = call.receive<UserToken>()
                    call.info(userToken)
                    val result = repository.updateItemToken(userToken.id, userToken.tokenFB)
                    if (result) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        throw ExistException("Пользователь не существует.")
                    }
                }
                route("/messages") {

                    get {
                        val userToken = call.receive<UserToken>()
                        val result = repoMessages.getAllForId(userToken.id)
                        call.respond(HttpStatusCode.OK, result)
                        call.info(userToken)
                        call.info(result)
                    }
                    post{
                        val message = call.receive<Message>()
                        call.info(message)
                        val result = repoMessages.insertItem(message)
                        if (result){
                            call.respond(HttpStatusCode.OK)
                        } else {
                            throw ExistException("Что-то пошло не так.")
                        }
                    }
                }
            }

            post("/login") {
                val user = call.receive<User>()
                call.info(user)
                val result = repository.insertItem(user)
                if (result) {
                    repository.getById(user.id)?.let { call.respond(HttpStatusCode.Created, it.chatToken) }
                } else {
                    throw RegistrationException("""Пользователь с именем "${user.name}" уже существует.""")
                }
            }

        }

    }
    server.start(wait = true)
}

fun ApplicationCall.info(msg: Any) {
    application.environment.log.info(msg.toString())
}
