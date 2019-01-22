package ru.silantyevmn.mymessenger.model.entity

import java.io.Serializable


class ChatMessage(var id:String,val fromUid: String, val toUid: String, val text: String, val time: Long,var status:Int) : Serializable {
    constructor() : this("","", "", "", -1,0)

    override fun equals(other: Any?): Boolean {
        var tempChatMessage = other as ChatMessage
        if(tempChatMessage == null) return false
        return id.equals(tempChatMessage.id)
    }
}