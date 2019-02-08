package ru.silantyevmn.mymessenger.model.cache.paper

import io.paperdb.Paper
import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.cache.IChatCache
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import java.util.*

class ChatCachePaper : IChatCache {
    private val BOOK = "ChatCachePaper"

    override fun addAll(currentUserUid: String, toUserUid: String, chatList: List<ChatMessage>) {
        deleteBook(currentUserUid, toUserUid)
        writeBook(currentUserUid, toUserUid, chatList)
    }

    override fun insertChatToUserMap(currentUserUid: String, chatMessage: ChatMessage) {
        val bookList = readBook(currentUserUid)
        if (bookList.contains(chatMessage)) return
        else bookList.add(chatMessage)
        writeBook(currentUserUid, bookList);
    }

    override fun getChatToUserMap(currentUserUid: String): List<ChatMessage> {
        val bookList = readBook(currentUserUid)
        return bookList
    }

    override fun getChatList(currentUserUid: String, toUserUid: String): ArrayList<ChatMessage> {
        return readBook(currentUserUid, toUserUid)
    }

    override fun insertChat(chatMessage: ChatMessage) {
        var bookList = readBook(chatMessage.fromUid, chatMessage.toUid)
        if (!bookList.contains(chatMessage)) {
            bookList.add(chatMessage)
            writeBook(chatMessage.fromUid, chatMessage.toUid, bookList)
        }
    }

    override fun deleteChat(chatMessage: ChatMessage) {
        var bookList = readBook(chatMessage.fromUid, chatMessage.toUid)
        if (bookList.contains(chatMessage)) {
            bookList.remove(chatMessage)
            writeBook(chatMessage.fromUid, chatMessage.toUid, bookList)
        }
    }

    override fun updateChat(chatMessage: ChatMessage) {
        var bookList = readBook(chatMessage.fromUid, chatMessage.toUid)
        if (bookList.contains(chatMessage)) {
            val position = bookList.indexOf(chatMessage)
            bookList.set(position, chatMessage)
            writeBook(chatMessage.fromUid, chatMessage.toUid, bookList)
        }
    }

    private fun deleteBook(currentUserUid: String, toUserUid: String) {
        Paper.book(BOOK).delete(currentUserUid + toUserUid)
    }

    private fun writeBook(currentUserUid: String, toUserUid: String, bookList: List<ChatMessage>) {
        Paper.book(BOOK).write(currentUserUid + toUserUid, bookList)
    }

    private fun writeBook(currentUserUid: String, bookList: List<ChatMessage>) {
        Paper.book(BOOK).write(currentUserUid, bookList)
    }

    private fun readBook(currentUserUid: String): ArrayList<ChatMessage> {
        var list = ArrayList<ChatMessage>()
        try {
            list = Paper.book(BOOK).read(currentUserUid)
        } catch (e: IllegalStateException) {
            list = ArrayList()
        } finally {
            return list
        }

    }

    private fun readBook(currentUserUid: String, toUserUid: String): ArrayList<ChatMessage> {
        var list = ArrayList<ChatMessage>()
        try {
            list = Paper.book(BOOK).read(currentUserUid + toUserUid)
        } catch (e: IllegalStateException) {
            list = ArrayList()
        } finally {
            return list
        }

    }
}