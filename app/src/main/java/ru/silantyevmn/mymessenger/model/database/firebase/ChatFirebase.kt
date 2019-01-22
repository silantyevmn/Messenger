package ru.silantyevmn.mymessenger.model.database.firebase

import android.util.Log
import com.google.firebase.database.*
import io.reactivex.Completable
import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.entity.ChatMessage

class ChatFirebase : IChatDatabase {
    override fun pushMessage(chatMessage: ChatMessage): Completable {
        return Completable.create { emitter ->
            var ref = FirebaseDatabase.getInstance()
                .getReference("/user-messages/${chatMessage.fromUid}/${chatMessage.toUid}").push()
            var toRef = FirebaseDatabase.getInstance()
                .getReference("/user-messages/${chatMessage.toUid}/${chatMessage.fromUid}").child(ref.key.toString())

            chatMessage.id = ref.key.toString()
            ref.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d("Chat", "Saved message - ${ref.key}")
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }

            chatMessage.id = toRef.key.toString()
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
                        it.onError(RuntimeException("Добро пожаловать в чат!"))
                    }
                }

            })
            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(data: DataSnapshot, p1: String?) {
                    var chatMessage = data.getValue(ChatMessage::class.java)
                    if (chatMessage != null) {
                        it.onNext(chatMessage)
                        Log.d("Chat", "Message- ${chatMessage.text}")
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
                        Log.d("Chat", "Message- ${chatMessage.text}")
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
            hashMap[chatMessage.id] = chatMessage
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