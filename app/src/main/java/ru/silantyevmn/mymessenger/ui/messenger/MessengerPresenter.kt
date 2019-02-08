package ru.silantyevmn.mymessenger.ui.messenger

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import ru.silantyevmn.mymessenger.model.repo.IRepo

@InjectViewState
class MessengerPresenter(val repo: IRepo) : MvpPresenter<MessengerView>() {
    lateinit var currentUserUid: String
    val chatList = ArrayList<ChatMessage>()

    fun init(currentUserUid: String?) {
        this.currentUserUid = currentUserUid!!
        if (currentUserUid == null) {
            viewState.showLoginActivity()
        } else {
            loadChatToCurrentUser()
        }
    }

    private fun loadChatToCurrentUser() {
        viewState.showLoading("Просмотр последних сообщений")
        repo.loadMessageMap(currentUserUid)
            .subscribe({
                loadUser(it)
            }, {
                viewState.showLoadingError(it.message)
            })
    }

    private fun loadUser(chatMessage: ChatMessage) {
        viewState.showLoading("Загрузка пользователей")
        var partnerUserUid: String
        if (currentUserUid == chatMessage.fromUid) {
            partnerUserUid = chatMessage.toUid
        } else
            partnerUserUid = chatMessage.fromUid
        repo.getUserFromId(partnerUserUid)
            .subscribe({
                if (!chatList.contains(chatMessage)) {
                    chatList.add(chatMessage)
                    viewState.updateAdapter(it, chatMessage)
                }
                viewState.showLoadingSuccess()
            }, {
                viewState.showLoadingError(it.message)
            })
    }


}