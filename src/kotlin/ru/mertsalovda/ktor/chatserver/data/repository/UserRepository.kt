package ru.mertsalovda.ktor.chatserver.data.repository

import ru.mertsalovda.ktor.chatserver.data.model.User

interface UserRepository {
    fun insertItem(item: User): Boolean
    fun updateItem(item: User): Boolean
    fun deleteById(id: Long): Boolean
    fun deleteItem(item: User): Boolean
    fun getAll(): List<User>
    fun getById(id: Long): User?
    fun updateItemToken(id: Long, token: String): Boolean
    fun getAllForId(id: Long): List<User>
}