package ru.mertsalovda.ktor.chatserver.data.model

import kotlin.properties.Delegates

data class User(val name: String, val password: String) {
    var id by Delegates.notNull<Long>()
    constructor(id: Long, name: String, password: String) : this(name, password){
        this.id = id
    }

}