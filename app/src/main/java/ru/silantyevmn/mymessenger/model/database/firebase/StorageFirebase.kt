package ru.silantyevmn.mymessenger.model.database.firebase

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Completable
import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.cache.UserCache
import ru.silantyevmn.mymessenger.model.database.IStorageDatabase
import ru.silantyevmn.mymessenger.model.entity.User
import ru.silantyevmn.mymessenger.ui.MessengerActivity
import java.lang.RuntimeException
import java.util.*


class StorageFirebase : IStorageDatabase {
    override fun putFile(fileUri: Uri): Observable<String> {
        return Observable.create{emitter->
            if (fileUri == null) emitter.onError(RuntimeException("File is null"))
            val fileName = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")
            ref.putFile(fileUri!!)
                .addOnSuccessListener {
                    Log.d("putFile", "Successfully upload image: ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("putFile", "File location: $it")
                        emitter.onNext(it.toString())
                    }
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }

    }

}