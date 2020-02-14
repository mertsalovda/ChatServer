package ru.mertsalovda.ktor.chatserver.data.model

import kotlin.properties.Delegates

data class User(var name: String, var password: String) {
    var id by Delegates.notNull<Long>()
    var chatToken: String = ""
    var tokenFB = ""
    var description = ""
    var imageBase64 = ""

    constructor(id: Long, name: String, password: String) : this(name, password) {
        this.id = id
    }

    constructor(
        id: Long,
        name: String,
        password: String,
        chatToken: String,
        tokenFB: String,
        description: String,
        imageBase64: String
    ) : this(id, name, password) {
        this.tokenFB = tokenFB
        this.description = description
        this.imageBase64 = imageBase64
    }

    override fun toString(): String {
        return "User(name='$name', password='$password', chatToken='$chatToken', tokenFB='$tokenFB', description='$description', imageBase64='$imageBase64')"
    }


}

data class UserToken(val id: Long, val tokenFB: String)