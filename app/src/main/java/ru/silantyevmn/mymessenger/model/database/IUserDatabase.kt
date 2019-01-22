package ru.silantyevmn.mymessenger.model.database.firebase

import io.reactivex.Completable
import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.entity.User

interface IUserDatabase {
    fun getUserFromId(userId: String): Observable<User>
    fun getUserList(): Observable<List<User>>
    fun insertUser(user: User): Completable
    fun deleteUser(user: User)
    fun updateUser(user: User)
}