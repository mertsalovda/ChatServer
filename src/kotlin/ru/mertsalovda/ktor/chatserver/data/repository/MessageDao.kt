package ru.mertsalovda.ktor.chatserver.data.repository

import ru.mertsalovda.ktor.chatserver.data.model.Message

interface MessageDao {

    fun getAll(): List<Message>

    fun insertItem(item: Message): Boolean

    fun getAllForId(recipientId: Long): List<Message>

    fun deleteById(id: Long): Boolean

}
