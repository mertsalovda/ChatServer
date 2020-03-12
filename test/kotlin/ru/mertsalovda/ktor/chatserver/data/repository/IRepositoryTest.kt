package ru.mertsalovda.ktor.chatserver.data.repository

import org.junit.Before
import org.junit.Test
import ru.mertsalovda.ktor.chatserver.data.model.User
import kotlin.test.assertEquals

internal class IRepositoryTest {
    lateinit var dao: UserDao

    @Before
    fun createRepository() {
        dao = MapUsersDaoImpl()
    }

    @Test
    fun insertItem() {
        val before = dao.getAll().size
        val user = User("name1")
        assertEquals(true, dao.insertItem(user))
        assertEquals(false, dao.insertItem(user))
        assertEquals(before + 1, dao.getAll().size)
    }

    @Test
    fun updateItem() {
        val user = dao.getAll()[0]
        user.name = "name2"
//        user.password = "password2"
        assertEquals(true, dao.updateItem(user))
        val updatedUser = dao.getAll()[0]
//        assertEquals(true, updatedUser.name == "name2" && updatedUser.password == "password2")
    }

    @Test
    fun deleteById() {
        val maxId = dao.getAll().maxBy { it.id }.let { it?.id ?: -1 }
        val before = dao.getAll().size
        assertEquals(false, dao.deleteById(maxId + 1))
        assertEquals(true, dao.deleteById(maxId))
        assertEquals(before - 1, dao.getAll().size)

    }

    @Test
    fun deleteItem() {
        val before = dao.getAll().size
        val user = User("name1")
        assertEquals(true, dao.insertItem(user))
        assertEquals(before + 1, dao.getAll().size)
        assertEquals(true, dao.deleteItem(user))
        assertEquals(before, dao.getAll().size)
    }

    @Test
    fun getAll() {
        assertEquals(true, dao.getAll() is List<User>)
    }

    @Test
    fun getById() {
        val maxId = dao.getAll().maxBy { it.id }.let { it?.id ?: -1 }
        assertEquals(true, dao.getById(maxId) != null)
        assertEquals(true, dao.getById(maxId + 1) == null)
    }
}