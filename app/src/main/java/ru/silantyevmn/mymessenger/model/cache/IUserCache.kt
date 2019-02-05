package ru.silantyevmn.mymessenger.model.cache

import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.entity.User

interface IUserCache {
    fun getUserList(): List<User>
    fun getUserFromId(userId:String): User?
    fun insertUser(user: User)
    fun deleteUser(user: User)
    fun updateUser(user: User)
    fun insertAll(users: List<User>)
}