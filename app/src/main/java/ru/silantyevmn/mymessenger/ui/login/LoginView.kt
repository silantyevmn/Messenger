package ru.silantyevmn.mymessenger.ui.login

import com.arellomobile.mvp.MvpView

interface LoginView : MvpView {
    fun showMessenger()
    fun showLoadingError(textError: String?)
    fun showLoadingSuccess(email: String)
    fun showLoading(text: String)
}