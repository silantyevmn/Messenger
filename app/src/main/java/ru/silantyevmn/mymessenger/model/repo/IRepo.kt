package ru.silantyevmn.mymessenger.model.repo

import io.reactivex.Completable
import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.database.IStorageDatabase
import ru.silantyevmn.mymessenger.model.database.firebase.IAuthUser
import ru.silantyevmn.mymessenger.model.database.firebase.IChatDatabase
import ru.silantyevmn.mymessenger.model.database.firebase.IUserDatabase
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import ru.silantyevmn.mymessenger.model.entity.User

interface IRepo :IAuthUser,IChatDatabase,IStorageDatabase,IUserDatabase{
/*    fun getUserFromId(userId: String): Observable<User>
    fun loadChat(currentUserUid: String, toUserUid: String): Observable<ChatMessage>
    fun pushMessages(chatMessage: ChatMessage): Completable
    fun updateMessages(chatMessage: ChatMessage): Completable
    fun getUsers():Observable<List<User>>*/
}