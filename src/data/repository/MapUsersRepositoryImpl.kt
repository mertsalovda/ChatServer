package ru.mertsalovda.ktor.chatserver.data.repository

import ru.mertsalovda.ktor.chatserver.data.model.User

class MapUsersRepositoryImpl : IRepository<User> {

    val mapUsers = mutableMapOf(
        1L to User(1, "admin", "admin"),
        2L to User(2, "test", "test")
    )

    override fun insertItem(item: User): Boolean {
        val maxId = mapUsers.maxBy { it.value.id }.let { it?.value?.id ?: 0 }
        item.id = maxId + 1
        return if (mapUsers[item.id] == null) {
            mapUsers[item.id] = item
            true
        } else {
            false
        }
    }

    override fun updateItem(item: User): Boolean {
        return mapUsers.replace(item.id, item) != null
    }

    override fun deleteById(id: Long): Boolean {
        return mapUsers.remove(id) != null
    }

    override fun deleteItem(item: User): Boolean {
        return mapUsers.remove(item.id, item)
    }

    override fun getAll(): List<User> {
        return mapUsers.toList().map { it.second }
    }

    override fun getById(id: Long): User? {
        return mapUsers[id]
    }
}