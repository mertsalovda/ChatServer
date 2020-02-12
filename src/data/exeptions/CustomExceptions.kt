package ru.mertsalovda.ktor.chatserver.data.exeptions

class AuthenticationException(message: String) : RuntimeException(message)
class RegistrationException(message: String) : RuntimeException(message)