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
import ru.mertsalovda.ktor.chatserver.data.model.Profile
import ru.mertsalovda.ktor.chatserver.data.model.User
import ru.mertsalovda.ktor.chatserver.data.model.UserToken
import ru.mertsalovda.ktor.chatserver.data.repository.*

fun main() {
    val userDao: UserDao = MapUsersDaoImpl()
    val profileDao: ProfileDao = MapProfileDaoImpl()
    val repoMessages: MessageDao = MapMessagesDao()
    val port = if (System.getenv("SERVER_PORT").isNullOrEmpty()) "8080" else System.getenv("SERVER_PORT")
    val server = embeddedServer(Netty, host = "127.0.0.1", port = port.toInt()) {
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
                    val profile = profileDao.getAll().first { profile -> profile.name == it.name }
                    if (it.name == profile.name && it.password == profile.password) {
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
            get("/") {
                call.respond(HttpStatusCode.OK, "Hello!")
            }
            authenticate {
                get("/users") {
                    val result = userDao.getAll()
                    call.respond(HttpStatusCode.OK, result)
                    call.info(result)
                }
                post("/user/token") {
                    val userToken = call.receive<UserToken>()
                    call.info(userToken)
                    val result = true
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
                    post {
                        val message = call.receive<Message>()
                        call.info(message)
                        val result = repoMessages.insertItem(message)
                        if (result) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            throw ExistException("Что-то пошло не так.")
                        }
                    }
                }

                post("/authorization") {
                    val user = call.receive<User>()
                    val result = userDao.getUserByName(user.name)
                    if (result != null) {
                        call.respond(HttpStatusCode.OK, result)
                    } else {
                        throw AuthenticationException("Ошибка авторизации.")
                    }
                }
            }

            post("/registration") {
                val newProfile = call.receive<Profile>()
                call.info(newProfile)
                val result = profileDao.insertItem(newProfile)
                if (result) {
                    val user = User(newProfile.name)
                    userDao.insertItem(user)
                    call.respond(HttpStatusCode.Created)
                } else {
                    throw RegistrationException("""Пользователь с именем "${newProfile.name}" уже существует.""")
                }
            }


        }

    }
    server.start(wait = true)
}

fun ApplicationCall.info(msg: Any) {
    application.environment.log.info(msg.toString())
}
