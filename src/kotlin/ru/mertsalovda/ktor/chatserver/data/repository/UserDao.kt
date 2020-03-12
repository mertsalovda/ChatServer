package ru.mertsalovda.ktor.chatserver.data.repository

import ru.mertsalovda.ktor.chatserver.data.model.User

interface UserDao {
    fun insertItem(item: User): Boolean
    fun updateItem(item: User): Boolean
    fun deleteById(id: Long): Boolean
    fun deleteItem(item: User): Boolean
    fun getAll(): List<User>
    fun getById(id: Long): User?
    fun updateItemToken(id: Long, uid: Long): Boolean
    fun getAllById(id: Long): List<User>
}