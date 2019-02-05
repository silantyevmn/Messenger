package ru.silantyevmn.mymessenger.ui.chat

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.silantyevmn.mymessenger.model.FileManager
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import ru.silantyevmn.mymessenger.model.entity.FactoryChatMessage
import ru.silantyevmn.mymessenger.model.entity.User
import ru.silantyevmn.mymessenger.model.repo.IRepo

@InjectViewState
class ChatPresenter(val repo: IRepo, val currentUserUid: String, val toUserUid: String) :
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
        repo.loadMessageList(currentUserUid,toUserUid)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                chatList = it as ArrayList<ChatMessage>
                viewState.updateAdapter()
                viewState.setScrollRecycler()
                viewState.onSuccess()
            }, {
                viewState.onError(it.message.toString())
            })

        //подпишемся на сообщения
        repo.loadMessage(currentUserUid, toUserUid)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.toUid.equals(currentUserUid) && it.status == 1) {
                    var tempChat = it
                    tempChat.status = 2 //меняю на статус получено
                    if(!it.uid.isEmpty()) {
                        repo.updateMessage(tempChat).subscribe()//обновляю в базе
                    }
                    Log.d("Test","it.toUid.equals(currentUserUid) && it.status == 1 message ${it.message}")
                }
                if (chatList.contains(it)) {
                    var position = chatList.indexOf(it)
                    Log.d("Test","chatList.contains(it)2 message ${chatList.get(position).message}")
                    //меняем только статус
                    if (it.status == 2) {
                        chatList[position].status = it.status
                        viewState.updateAdapter(position)
                    }
                    //chatList.set(position, it)
                    //viewState.updateAdapter(position)
                    //нужно отловить статус
                } else {
                    Log.d("Test","else message ${it.message}")
                    chatList.add(it)
                    viewState.updateAdapter()
                }
                viewState.setScrollRecycler()
                viewState.onSuccess()
            }, {
                viewState.onError(it.message.toString())
            })
    }

    fun pushMessages(textMessage: String) {
        var tempChatMessage = FactoryChatMessage().createChatMessage(currentUserUid, toUserUid, "text", textMessage, 1)
        chatList.add(tempChatMessage)
        viewState.updateAdapter()
        viewState.setTextMessagesClear()//очищяем текст
        viewState.setScrollRecycler()

        //указать статус загрузки на фото
        repo.pushMessage(tempChatMessage)
            .subscribe({
                //снять статус загрузки
            }, {
                viewState.onError(it.message.toString())
            })
    }

    fun onClickImageLoad() {
        viewState.showLoadImage()
    }

    fun pushImage(uri: Uri) {
        //сохраняем в адаптер и отображаем
        val tempChatMessage =
            FactoryChatMessage().createChatMessage(currentUserUid, toUserUid, "image", uri.toString(), 1)
        chatList.add(tempChatMessage);
        Log.d("Test","записал 1 раз message ${tempChatMessage.message}")
        viewState.updateAdapter()
        viewState.setScrollRecycler()

        //сохраняем файл на сервере
        repo.putFile(uri)
            .subscribe({
                tempChatMessage.message = it
                repo.pushMessage(tempChatMessage)
                    .subscribe({
                        //загрузили
                    }, {
                        viewState.onError(it.message.toString())
                    })
            }, {
                viewState.onError(it.message.toString())
            })
    }

    fun getUriToBitmapCompress(bitmapOrigin: Bitmap?): Uri? {
        return FileManager().getUriToBitmapCompress(bitmapOrigin)
    }

}