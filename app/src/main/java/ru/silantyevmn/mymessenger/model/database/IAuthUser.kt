package ru.silantyevmn.mymessenger.model.database.firebase

import io.reactivex.Completable
import io.reactivex.Observable

interface IAuthUser {
    fun signInWithEmailAndPassword(email: String, pass: String): Completable
    fun createUserWithEmailAndPassword(email: String, pass: String): Observable<String>
}