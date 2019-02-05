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

    override fun loadMessageList(currentUserUid: String, toUserUid: String): Observable<List<ChatMessage>> {
        if (NetworkStatus.isInternetAvailable()) {
            return Observable.create { emitter ->
                chatDatabase.loadMessageList(currentUserUid, toUserUid)
                    .subscribe({
                        chatCache.addAll(currentUserUid,toUserUid,it)
                        emitter.onNext(it)
                    }, {
                        emitter.onError(it)
                    })
            }
        } else
            return Observable.just(chatCache.getChatList(currentUserUid,toUserUid))
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
        /*//нужно проверку написать
        return Observable.create { emitter ->
            val list = chatCache.getChatList()
            for (chat in list) {
                if (chat.fromUid.equals(currentUserUid) && chat.toUid.equals(toUserUid)) {
                    emitter.onNext(chat)
                }
            }
            chatDatabase.loadMessage(currentUserUid, toUserUid)
                .subscribe({
                    if (!list.contains(it)) {
                        chatCache.insertChat(it)
                        emitter.onNext(it)
                    } else {
                        if (it.typeMessage.equals("image")) {
                            //if(it.message )
                        }
                        chatCache.updateChat(it)
                    }
                }, {
                    emitter.onError(it)
                })
        }*/
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
                    .map {
                        userCache.insertAll(it)
                    }
                    .subscribe {
                        emitter.onNext(userCache.getUserList())
                        emitter.onComplete()
                    }
            }
        } else return Observable.just(userCache.getUserList())//если нет интернета, загрузим из кеша
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
        if (NetworkStatus.isInternetAvailable()) {
            return Observable.create { emitter ->
                userDatabase.getUserFromId(userId)
                    .map {
                        userCache.insertUser(it)
                    }
                    .subscribe {
                        val findUser = userCache.getUserFromId(userId)
                        if(findUser!=null) {
                            emitter.onNext(findUser)
                        }
                        emitter.onComplete()
                    }
            }
        } else return Observable.just(userCache.getUserFromId(userId))//если нет интернета, загрузим из кеша
        //return userDatabase.getUserFromId(userId)
    }
}