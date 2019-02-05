package ru.silantyevmn.mymessenger.model.database.firebase

import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable
import io.reactivex.Observable


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