package ru.silantyevmn.mymessenger.ui.register

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.silantyevmn.mymessenger.model.entity.User
import ru.silantyevmn.mymessenger.model.repo.IRepo

@InjectViewState
class RegisterPresenter(val repo: IRepo) : MvpPresenter<RegisterView>() {
    private lateinit var currenUserUid: String
    private lateinit var userLogin:String
    private lateinit var userPhotoUri: Uri

    fun setUserPhotoUri(userPhotoUri: Uri?) {
        if(userPhotoUri==null) return
        this.userPhotoUri = userPhotoUri
        //показать фото на экране
        viewState.showImageUserPhoto(userPhotoUri)
    }

    fun onClickLoginActivity() {
        viewState.showLoginActivity()
    }

    fun createUserWithEmailAndPassword(login:String, email: String, pass: String) {
        viewState.showLoading("Сохраняем email и pass...")
        userLogin = login
        repo.createUserWithEmailAndPassword(email, pass)
            .subscribe({
                if (it != null) {
                    currenUserUid = it
                    //сохраним фото
                    saveUserPhoto()
                } else {
                    viewState.showLoadingError("Failed to create toUser. User is null")
                }
            }, {
                viewState.showLoadingError("Failed to create toUser. ${it.message}")
            })
    }

    private fun saveUserPhoto() {
        viewState.showLoading("Сохраняем фото в базу...")
        if (userPhotoUri == null) {
            saveUser("-1")
            return
        }
        repo.putFile(userPhotoUri)
            .subscribe({
                if (it != null) {
                    saveUser(it)
                } else {
                    viewState.showLoadingError("file is null")
                }
            }, {
                viewState.showLoadingError(it.message+"")
            })
    }

    private fun saveUser(imageUserPhotoUri: String) {
        viewState.showLoading("Обновление данных пользователя...")
        var user =
            User(
                currenUserUid,
                userLogin,
                imageUserPhotoUri
            )
        repo.insertUser(user)
            .subscribe({
                viewState.showLoadingSuccess(currenUserUid)
                viewState.showMessenger()
            }, {
                viewState.showLoadingError("Error! ${it.message.toString()}")
            })
    }




}