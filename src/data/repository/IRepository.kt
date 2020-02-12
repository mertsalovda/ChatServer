package ru.mertsalovda.ktor.chatserver.data.repository

interface IRepository<T> {
    fun insertItem(item: T): Boolean
    fun updateItem(item: T): Boolean
    fun deleteById(id: Long): Boolean
    fun deleteItem(item: T): Boolean
    fun getAll(): List<T>
    fun getById(id: Long): T?
}