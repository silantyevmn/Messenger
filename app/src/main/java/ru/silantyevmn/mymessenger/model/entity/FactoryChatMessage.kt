package ru.silantyevmn.mymessenger.model.entity

import java.util.*

class FactoryChatMessage {
    fun createChatMessage(fromUserUid: String, toUserUid: String, typeMessage: String, message: String,status: Int): ChatMessage {
        return ChatMessage(
            "",
            randomId(),
            fromUserUid,
            toUserUid,
            typeMessage,
            message,
            getTime(),
            status
        )
    }

    private fun getTime():Long = System.currentTimeMillis()

    private fun randomId(): String = UUID.randomUUID().toString()

}