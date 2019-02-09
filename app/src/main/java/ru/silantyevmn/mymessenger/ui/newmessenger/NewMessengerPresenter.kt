package ru.silantyevmn.mymessenger.ui.newmessenger

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.silantyevmn.mymessenger.model.entity.User
import ru.silantyevmn.mymessenger.model.repo.IRepo

@InjectViewState
class NewMessengerPresenter(val repo: IRepo) : MvpPresenter<NewMessengerView>() {
    lateinit var currentUserUid: String
    var userList = ArrayList<User>()

    fun init(currentUserUid: String?) {
        this.currentUserUid = currentUserUid!!
        if (currentUserUid == null) {
            viewState.showLoginIntent()
        } else {
            loadAllUser()
        }
    }

    private fun loadAllUser() {
        viewState.showLoading("Загрузка пользователей")
        repo.getUserList()
            .subscribe({
                userList = it as ArrayList<User>
                viewState.updateAdapter()
                viewState.showLoadingSuccess()
            }, {
                viewState.showError(it.message)
            })
    }

    fun onClickItem(position: Int) {
        val toUser = userList.get(position)
        viewState.showChatIntent(currentUserUid, toUser.uid)
    }


}