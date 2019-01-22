package ru.silantyevmn.mymessenger.model.database

import android.net.Uri
import io.reactivex.Observable

interface IStorageDatabase {
    fun putFile(fileUri: Uri): Observable<String>
}