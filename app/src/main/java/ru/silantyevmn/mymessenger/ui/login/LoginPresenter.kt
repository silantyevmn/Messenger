package ru.silantyevmn.mymessenger.ui.login

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.silantyevmn.mymessenger.model.repo.IRepo

@InjectViewState
class LoginPresenter(val repo: IRepo) : MvpPresenter<LoginView>() {

    fun signInWithEmailAndPassword(email: String, pass: String) {
        viewState.showLoading("Проверка пользователя в базе...")
        repo.signInWithEmailAndPassword(email, pass)
            .subscribe({
                viewState.showLoadingSuccess(email)
                viewState.showMessenger()
            }, {
                viewState.showLoadingError(it.message)
            })
    }

}