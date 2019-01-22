package ru.silantyevmn.mymessenger.ui.chat

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.silantyevmn.mymessenger.di.App
import ru.silantyevmn.mymessenger.model.cache.UserCache
import ru.silantyevmn.mymessenger.model.database.firebase.ChatFirebase
import ru.silantyevmn.mymessenger.model.database.firebase.IUserDatabase
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import ru.silantyevmn.mymessenger.model.entity.User
import ru.silantyevmn.mymessenger.model.database.firebase.UserFirebase
import ru.silantyevmn.mymessenger.model.repo.IRepo
import ru.silantyevmn.mymessenger.model.repo.Repo
import javax.inject.Inject

@InjectViewState
class ChatPresenter(val repo: IRepo,val currentUserUid: String, val toUserUid: String) :
    MvpPresenter<ChatView>() {

    lateinit var currentUser: User
    lateinit var toUser: User

    var chatList = ArrayList<ChatMessage>()

    fun loadUsers() {
        viewState.showLoading("Идет загрузка...")
        repo.getUserFromId(currentUserUid)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                currentUser = it
                repo.getUserFromId(toUserUid)
                    .subscribe {
                        toUser = it
                        viewState.setTitleToolbar(toUser)
                        loadChat()
                    }
            }, {
                viewState.onError(it.message.toString())
            })
    }

    fun loadChat() {
        repo.loadMessage(currentUserUid, toUserUid)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                if (it.toUid.equals(currentUserUid) && it.status == 1) {
                    var tempChat = it
                    tempChat.status = 2 //меняю на статус получено
                    repo.updateMessage(tempChat).subscribe()//обновляю в базе
                }
                if (chatList.contains(it)) {
                    Log.d("Chat_presenter", " true ${it.id}")
                    var position = chatList.indexOf(it)
                    chatList.set(position, it)
                    viewState.updateStatus(position)
                    //нужно отловить статус
                } else {
                    chatList.add(it)
                    viewState.updateAdapter()
                }

            }
            .subscribe({
                viewState.setScrollRecycler()
                viewState.onSuccess()
                //chatView.setAdapter(chatMessage)
            }, {
                viewState.onError(it.message.toString())
            })
    }

    fun pushMessages(textMessage: String, currentTimeMillis: Long) {
        var status = 1 //статус отправки
        val chatMessage = ChatMessage(
            "",
            currentUserUid,
            toUserUid,
            textMessage,
            currentTimeMillis,
            status
        )
        viewState.setTextMessagesClear()
/*        chatList.add(chatMessage)
        chatView.updateAdapter()
        chatView.setTextMessagesClear()
        chatView.setScrollRecycler()*/

        //chatView.showLoading("Сохранение данных...")
        repo.pushMessage(chatMessage)
            .subscribe({
               /* chatMessage.status = 1
                repo.updateMessages(chatMessage).subscribe()*/
            }, {
                viewState.onError(it.message.toString())
            })
    }

}