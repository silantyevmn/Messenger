package ru.silantyevmn.mymessenger.ui.newmessenger

import com.arellomobile.mvp.MvpView

interface NewMessengerView : MvpView {
    fun showError(textError: String?)
    fun showLoadingSuccess()
    fun showLoading(text: String)
    fun updateAdapter()
    fun showChatIntent(currentUserUid: String, toUserUid: String)
    fun showLoginIntent()
}