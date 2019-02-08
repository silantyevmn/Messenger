package ru.silantyevmn.mymessenger.model.database.firebase

import android.util.Log
import com.google.firebase.database.*
import io.reactivex.Completable
import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import java.util.*

class ChatFirebase : IChatDatabase {

    override fun loadMessageMap(currentUserUid: String): Observable<ChatMessage> {
        return Observable.create { emitter ->
            var ref = FirebaseDatabase.getInstance().getReference("/user-messages/${currentUserUid}")
            if (ref == null) {
                emitter.onError(throw RuntimeException("no database connection!"))
            }

            ref.addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    val map = HashMap<String, ChatMessage>()
                    for (postData in p0.children) {
                        val chatMessage = postData.getValue(ChatMessage::class.java) ?: continue
                        if (currentUserUid == chatMessage.fromUid) {
                            map[chatMessage.toUid] = chatMessage
                        } else
                            map[chatMessage.fromUid] = chatMessage
                    }
                    for(m in map){
                        emitter.onNext(m.value)
                    }
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val map = HashMap<String, ChatMessage>()
                    for (postData in p0.children) {
                        val chatMessage = postData.getValue(ChatMessage::class.java) ?: continue
                        if (currentUserUid == chatMessage.fromUid) {
                            map[chatMessage.toUid] = chatMessage
                        } else
                            map[chatMessage.fromUid] = chatMessage
                    }
                    for(m in map){
                        emitter.onNext(m.value)
                    }
                   /* val map = HashMap<String, ChatMessage>()
                    for (postData in p0.children) {
                        val chatMessage = postData.getValue(ChatMessage::class.java) ?: continue
                        if (currentUserUid == chatMessage.fromUid) {
                            map[chatMessage.toUid] = chatMessage
                        } else
                            map[chatMessage.fromUid] = chatMessage

                    }
                    emitter.onNext(map)*/
                }

                override fun onChildRemoved(p0: DataSnapshot) {}

            })
        }

    }

    override fun loadMessageList(currentUserUid: String, toUserUid: String): Observable<List<ChatMessage>> {
        return Observable.create { emitter ->
            var ref = FirebaseDatabase.getInstance().getReference("/user-messages/$currentUserUid/$toUserUid")
            //проверка на пустую переписку
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    emitter.onError(p0.toException())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        var chatList = ArrayList<ChatMessage>()
                        for (data in p0.children) {
                            var chatMessage = data.getValue(ChatMessage::class.java)
                            if (chatMessage != null) {
                                chatList.add(chatMessage)
                            }
                        }
                        emitter.onNext(chatList)
                        emitter.onComplete()
                    } else {
                        emitter.onError(RuntimeException("Чат пуст!"))
                    }
                }

            })
        }
    }

    override fun pushMessage(chatMessage: ChatMessage): Completable {
        return Completable.create { emitter ->

            var ref = FirebaseDatabase.getInstance()
                .getReference("/user-messages/${chatMessage.fromUid}/${chatMessage.toUid}").push()
            var toRef = FirebaseDatabase.getInstance()
                .getReference("/user-messages/${chatMessage.toUid}/${chatMessage.fromUid}").child(ref.key.toString())

            chatMessage.uid = ref.key.toString()

            ref.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d("Chat", "Saved message - ${ref.key}")
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }

            //chatMessage.id = toRef.key.toString()
            toRef.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d("Chat", "Saved message - ${toRef.key}")
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    override fun loadMessage(currentUserUid: String, toUserUid: String): Observable<ChatMessage> {
        return Observable.create {
            var ref = FirebaseDatabase.getInstance().getReference("/user-messages/$currentUserUid/$toUserUid")

            //проверка на пустую переписку
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    it.onError(p0.toException())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (!p0.exists()) {
                        it.onComplete()
                        //it.onError(RuntimeException("Добро пожаловать в чат!"))
                    }
                }

            })
            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(data: DataSnapshot, p1: String?) {
                    var chatMessage = data.getValue(ChatMessage::class.java)
                    if (chatMessage != null) {
                        it.onNext(chatMessage)
                        Log.d("Chat", "Message- ${chatMessage.message}")
                    } else {
                        it.onComplete()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    it.onError(p0.toException())
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(data: DataSnapshot, p1: String?) {
                    var chatMessage = data.getValue(ChatMessage::class.java)
                    if (chatMessage != null) {
                        it.onNext(chatMessage)
                        Log.d("Chat", "Message- ${chatMessage.message}")
                    } else {
                        it.onComplete()
                    }
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }

            })
        }
    }

    override fun updateMessage(chatMessage: ChatMessage): Completable {
        return Completable.create { emitter ->
            var hashMap = HashMap<String, Any>()
            hashMap[chatMessage.uid] = chatMessage
            var ref =
                FirebaseDatabase.getInstance()
                    .getReference("/user-messages/${chatMessage.fromUid}/${chatMessage.toUid}")
                    .updateChildren(hashMap)
                    .addOnSuccessListener {
                        emitter.onComplete()
                    }
                    .addOnFailureListener {
                        emitter.onError(it)
                    }
        }
    }

}