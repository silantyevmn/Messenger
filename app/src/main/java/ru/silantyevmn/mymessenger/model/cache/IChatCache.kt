package ru.silantyevmn.mymessenger.model.cache


import ru.silantyevmn.mymessenger.model.entity.ChatMessage

interface IChatCache {
    fun getChatList(currentUserUid: String, toUserUid: String): List<ChatMessage>
    fun insertChat(chatMessage: ChatMessage)
    fun deleteChat(chatMessage: ChatMessage)
    fun updateChat(chatMessage: ChatMessage)
    fun addAll(currentUserUid: String, toUserUid: String, chatList: List<ChatMessage>)
}