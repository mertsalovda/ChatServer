package ru.mertsalovda.ktor.chatserver.data.repository

import ru.mertsalovda.ktor.chatserver.data.model.Profile

interface ProfileDao {
    fun insertItem(item: Profile): Boolean
    fun getAll(): List<Profile>
    fun getByName(name: String): Profile?
}