package ru.mertsalovda.ktor.chatserver.data.repository

import ru.mertsalovda.ktor.chatserver.data.model.Message

class MapMessagesDao : MessageDao {

    val mapMessages = mutableMapOf(
        1L to Message(1, 2, "Hello world!", 1234566789L)
    )


    override fun getAll(): List<Message> = mapMessages.toList().map { it.second }

    override fun insertItem(item: Message): Boolean {
        val maxId = mapMessages.maxBy { it.key }?.key ?: 0L
        mapMessages[maxId + 1] = item
        return true
    }

    override fun getAllForId(recipientId: Long): List<Message> = getAll().filter { it.recipientId == recipientId }

    override fun deleteById(id: Long): Boolean {
        val removed = mapMessages.remove(id)
        return removed != null
    }
}