package ru.silantyevmn.mymessenger.model.database.firebase

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Completable
import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.cache.UserCache
import ru.silantyevmn.mymessenger.model.entity.User
import ru.silantyevmn.mymessenger.ui.MessengerActivity


class AuthUserFirebase : IAuthUser {

    override fun signInWithEmailAndPassword(email: String, pass: String):Completable{
        return Completable.create {emitter->
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        emitter.onComplete()
                    }
                }
                .addOnFailureListener{
                    emitter.onError(it)
                }
        }

    }
    override fun createUserWithEmailAndPassword(email: String, pass: String):Observable<String>{
        return Observable.create{ emitter ->
            val ref = FirebaseAuth.getInstance()
            ref.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        emitter.onNext(ref.uid.toString())
                        emitter.onComplete()
                    }
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

}