package ru.silantyevmn.mymessenger.ui.messenger

import com.arellomobile.mvp.MvpView
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import ru.silantyevmn.mymessenger.model.entity.User

interface MessengerView : MvpView {
    fun showError(textError: String?)
    fun showLoadingSuccess()
    fun showLoading(text: String)
    fun showLoginIntent()
    fun updateAdapter(user: User, chatMessage: ChatMessage)
    fun showChatIntent(currentUserUid: String, toUserUid: String)
    fun showNewMessengerIntent()
    fun showRegisterIntent()
}