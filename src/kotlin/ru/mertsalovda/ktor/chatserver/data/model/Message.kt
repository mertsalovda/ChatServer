package ru.mertsalovda.ktor.chatserver.data.model

data class Message(val authorID: Long, val recipientId: Long, val text: String, val date: Long)