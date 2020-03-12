package ru.mertsalovda.ktor.chatserver.data.repository

import ru.mertsalovda.ktor.chatserver.data.model.Profile

class MapProfileDaoImpl : ProfileDao {

    private val mapProfiles = mutableMapOf(
        1L to Profile("admin", "admin"),
        2L to Profile("test", "test")
    )

    override fun insertItem(item: Profile): Boolean {
        for (profile in mapProfiles) {
            if (profile.value.name == item.name) return false
        }
        val id = mapProfiles.keys.last() + 1
        mapProfiles[id] = item
        return true
    }

    override fun getByName(name: String): Profile? {
        return getAll().first { it.name == name }
    }

    override fun getAll(): List<Profile> {
        return mapProfiles.toList().map { it.second }
    }
}