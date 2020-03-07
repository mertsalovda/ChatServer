package ru.mertsalovda.ktor.chatserver.data.repository

import org.junit.Before
import org.junit.Test
import ru.mertsalovda.ktor.chatserver.data.model.User
import kotlin.test.assertEquals

internal class IRepositoryTest {
    lateinit var repository: UserRepository

    @Before
    fun createRepository() {
        repository = MapUsersRepositoryImpl()
    }

    @Test
    fun insertItem() {
        val before = repository.getAll().size
        val user = User("name1")
        assertEquals(true, repository.insertItem(user))
        assertEquals(false, repository.insertItem(user))
        assertEquals(before + 1, repository.getAll().size)
    }

    @Test
    fun updateItem() {
        val user = repository.getAll()[0]
        user.name = "name2"
//        user.password = "password2"
        assertEquals(true, repository.updateItem(user))
        val updatedUser = repository.getAll()[0]
//        assertEquals(true, updatedUser.name == "name2" && updatedUser.password == "password2")
    }

    @Test
    fun deleteById() {
        val maxId = repository.getAll().maxBy { it.id }.let { it?.id ?: -1 }
        val before = repository.getAll().size
        assertEquals(false, repository.deleteById(maxId + 1))
        assertEquals(true, repository.deleteById(maxId))
        assertEquals(before - 1, repository.getAll().size)

    }

    @Test
    fun deleteItem() {
        val before = repository.getAll().size
        val user = User("name1")
        assertEquals(true, repository.insertItem(user))
        assertEquals(before + 1, repository.getAll().size)
        assertEquals(true, repository.deleteItem(user))
        assertEquals(before, repository.getAll().size)
    }

    @Test
    fun getAll() {
        assertEquals(true, repository.getAll() is List<User>)
    }

    @Test
    fun getById() {
        val maxId = repository.getAll().maxBy { it.id }.let { it?.id ?: -1 }
        assertEquals(true, repository.getById(maxId) != null)
        assertEquals(true, repository.getById(maxId + 1) == null)
    }
}