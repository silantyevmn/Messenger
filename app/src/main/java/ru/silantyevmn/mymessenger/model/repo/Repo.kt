package ru.silantyevmn.mymessenger.model.repo

import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.cache.IChatCache
import ru.silantyevmn.mymessenger.model.cache.IUserCache
import ru.silantyevmn.mymessenger.model.database.IStorageDatabase
import ru.silantyevmn.mymessenger.model.database.firebase.IAuthUser
import ru.silantyevmn.mymessenger.model.database.firebase.IChatDatabase
import ru.silantyevmn.mymessenger.model.database.firebase.IUserDatabase
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import ru.silantyevmn.mymessenger.model.entity.User
import ru.silantyevmn.mymessenger.utils.NetworkStatus

class Repo(
    val userDatabase: IUserDatabase,
    val chatDatabase: IChatDatabase,
    val auth: IAuthUser,
    val storage: IStorageDatabase,
    val chatCache: IChatCache,
    val userCache: IUserCache
) : IRepo {

    override fun signInWithEmailAndPassword(email: String, pass: String): Completable {
        return auth.signInWithEmailAndPassword(email, pass)
    }

    override fun createUserWithEmailAndPassword(email: String, pass: String): Observable<String> {
        return auth.createUserWithEmailAndPassword(email, pass)
    }

    override fun pushMessage(chatMessage: ChatMessage): Completable {
        return chatDatabase.pushMessage(chatMessage)
    }

    override fun loadMessageMap(currentUserUid: String): Observable<ChatMessage> {
        if (NetworkStatus.isInternetAvailable()) {
            return Observable.create{emitter ->
                chatDatabase.loadMessageMap(currentUserUid)
                    .subscribe({
                        chatCache.insertChatToUserMap(currentUserUid,it)
                        emitter.onNext(it)
                    },{
                        emitter.onError(it)
                    })
            }

        } else  return Observable.create {
            val chatList = chatCache.getChatToUserMap(currentUserUid)
            for (chat in chatList) {
                it.onNext(chat)
            }
            it.onComplete()
        }


    }

    override fun loadMessageList(currentUserUid: String, toUserUid: String): Observable<List<ChatMessage>> {
        if (NetworkStatus.isInternetAvailable()) {
            return Observable.create { emitter ->
                chatDatabase.loadMessageList(currentUserUid, toUserUid)
                    .subscribe({
                        chatCache.addAll(currentUserUid, toUserUid, it)
                        emitter.onNext(it)
                    }, {
                        emitter.onError(it)
                    })
            }
        } else
            return Observable.just(chatCache.getChatList(currentUserUid, toUserUid))
    }

    override fun loadMessage(currentUserUid: String, toUserUid: String): Observable<ChatMessage> {
        //если есть интернет, тогда загрузим чат в кеш
        if (NetworkStatus.isInternetAvailable()) {
            return Observable.create { emitter ->
                chatDatabase.loadMessage(currentUserUid, toUserUid)
                    .subscribe({
                        chatCache.insertChat(it)
                        emitter.onNext(it)
                    }, {
                        emitter.onError(it)
                    })
            }
        } else
            return Observable.error(RuntimeException("internet offline"))
    }

    override fun updateMessage(chatMessage: ChatMessage): Completable {
        return chatDatabase.updateMessage(chatMessage)
    }

    override fun putFile(fileUri: Uri): Observable<String> {
        return storage.putFile(fileUri)
    }

    override fun getUserList(): Observable<List<User>> {
        //если есть инет, загрузим в кеш
        if (NetworkStatus.isInternetAvailable()) {
            return Observable.create { emitter ->
                userDatabase.getUserList()
                    .subscribe {
                        userCache.insertAll(it)
                        emitter.onNext(it)
                        emitter.onComplete()
                    }
            }
        } else return Observable.just(userCache.getUserList())//если нет интернета, загрузим из кеша
    }

    override fun insertUser(user: User): Completable {
        return userDatabase.insertUser(user)
    }

    override fun deleteUser(user: User) {
        //
    }

    override fun updateUser(user: User) {
        //
    }

    override fun getUserFromId(userId: String): Observable<User> {
        if (NetworkStatus.isInternetAvailable()) {
            return Observable.create { emitter ->
                userDatabase.getUserFromId(userId)
                    .subscribe {
                        userCache.insertUser(it)
                        emitter.onNext(it)
                        emitter.onComplete()
                    }
            }
        } else return Observable.just(userCache.getUserFromId(userId))//если нет интернета, загрузим из кеша
    }
}