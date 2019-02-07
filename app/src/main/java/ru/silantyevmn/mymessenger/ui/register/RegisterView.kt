package ru.silantyevmn.mymessenger.ui.register

import android.net.Uri
import com.arellomobile.mvp.MvpView

interface RegisterView : MvpView {
    fun showMessenger()
    fun showLoadingError(textError: String)
    fun showLoadingSuccess(text: String)
    fun showLoading(text: String)
    fun showImageUserPhoto(userPhotoUri: Uri)
    fun showLoginActivity()
}