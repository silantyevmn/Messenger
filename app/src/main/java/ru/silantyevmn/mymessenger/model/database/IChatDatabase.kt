package ru.silantyevmn.mymessenger.model.database.firebase

import io.reactivex.Completable
import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.entity.ChatMessage

interface IChatDatabase {
    fun pushMessage(chatMessage: ChatMessage): Completable
    fun loadMessage(currentUserUid: String, toUserUid: String): Observable<ChatMessage>
    fun updateMessage(chatMessage: ChatMessage): Completable
}