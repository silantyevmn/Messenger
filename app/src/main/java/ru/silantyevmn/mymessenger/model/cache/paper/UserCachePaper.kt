package ru.silantyevmn.mymessenger.model.cache.paper

import io.paperdb.Paper
import ru.silantyevmn.mymessenger.model.cache.IUserCache
import ru.silantyevmn.mymessenger.model.entity.User

class UserCachePaper : IUserCache {
    private val BOOK = "UserCachePaper"
    private val KEY = "UserList"

    override fun insertAll(bookList: List<User>) {
        deleteBook()
        writeBook(bookList)
    }

    override fun getUserList(): List<User> = readBook()

    override fun getUserFromId(userId: String): User? {
        var bookList = readBook()
        //бежим по коллекции
        for (user in bookList) {
            if (user.uid.equals(userId)) {
                return user
            }
        }
        return null
    }

    override fun insertUser(user: User) {
        var bookList = readBook()
        if (bookList.contains(user)) {
            return
        } else {
            bookList.add(user)
        }
        writeBook(bookList)
    }

    override fun deleteUser(user: User) {
        var bookList = readBook()
        if (bookList.contains(user)) {
            bookList.remove(user)
        } else {
            return
        }
        writeBook(bookList)
    }

    override fun updateUser(user: User) {
        var bookList = readBook()
        if (bookList.contains(user)) {
            bookList.set(bookList.indexOf(user), user)
        } else {
            return
        }
        writeBook(bookList)
    }

    private fun deleteBook() {
        Paper.book(BOOK).delete(KEY)
    }

    private fun writeBook(bookList: List<User>) {
        Paper.book(BOOK).write(KEY, bookList)
    }

    private fun readBook(): ArrayList<User> {
        var users = Paper.book(BOOK).read<ArrayList<User>>(KEY)
        if (users == null) {
            users = ArrayList()
        }
        return users
    }
}
