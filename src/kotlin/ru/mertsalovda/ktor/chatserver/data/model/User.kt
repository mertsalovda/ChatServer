package ru.mertsalovda.ktor.chatserver.data.model

import java.util.*
import kotlin.properties.Delegates

data class User(var name: String) {
    var id by Delegates.notNull<Long>()
    var uid: Long = UUID.randomUUID().mostSignificantBits
    var lastTime: Long = 0
    var about = ""
    var imageUrl = ""

    constructor(id: Long, name: String) : this(name) {
        this.id = id
    }

    constructor(
        id: Long,
        name: String,
        uid: Long,
        about: String,
        imageUrl: String
    ) : this(id, name) {
        this.uid = uid
        this.about = about
        this.imageUrl = imageUrl
    }

    override fun toString(): String {
        return "User(name='$name', uid='$uid', about='$about', imageUrl='$imageUrl')"
    }


}

data class UserToken(val id: Long, val tokenFB: String)