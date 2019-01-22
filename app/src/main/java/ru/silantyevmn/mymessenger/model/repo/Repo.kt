package ru.silantyevmn.mymessenger.model.repo

import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.database.IStorageDatabase
import ru.silantyevmn.mymessenger.model.database.firebase.IAuthUser
import ru.silantyevmn.mymessenger.model.database.firebase.IChatDatabase
import ru.silantyevmn.mymessenger.model.database.firebase.IUserDatabase
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import ru.silantyevmn.mymessenger.model.entity.User

class Repo(val userDatabase: IUserDatabase, val chatDatabase: IChatDatabase,val auth:IAuthUser,val storage:IStorageDatabase) : IRepo {
    override fun signInWithEmailAndPassword(email: String, pass: String): Completable {
        return auth.signInWithEmailAndPassword(email,pass)
    }

    override fun createUserWithEmailAndPassword(email: String, pass: String): Observable<String> {
        return auth.createUserWithEmailAndPassword(email,pass)
    }

    override fun pushMessage(chatMessage: ChatMessage): Completable {
        return chatDatabase.pushMessage(chatMessage)
    }

    override fun loadMessage(currentUserUid: String, toUserUid: String): Observable<ChatMessage> {
        return chatDatabase.loadMessage(currentUserUid,toUserUid)
    }

    override fun updateMessage(chatMessage: ChatMessage): Completable {
        return chatDatabase.updateMessage(chatMessage)
    }

    override fun putFile(fileUri: Uri): Observable<String> {
        return storage.putFile(fileUri)
    }

    override fun getUserList(): Observable<List<User>> {
        return userDatabase.getUserList()
    }

    override fun insertUser(user: User): Completable {
        return userDatabase.insertUser(user)
    }

    override fun deleteUser(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateUser(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserFromId(userId: String): Observable<User> {
        return userDatabase.getUserFromId(userId)
    }
}