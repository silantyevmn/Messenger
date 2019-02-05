package ru.silantyevmn.mymessenger.model.database.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.database.IStorageDatabase
import java.util.*


class StorageFirebase : IStorageDatabase {
    override fun putFile(fileUri: Uri): Observable<String> {
        return Observable.create { emitter ->
            if (fileUri == null) emitter.onError(RuntimeException("File is null"))
            val fileName = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")
            ref.putFile(fileUri!!)
                .addOnSuccessListener {
                    Log.d("putFile", "Successfully upload image: ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("putFile", "File location: $it")
                        emitter.onNext(it.toString())
                        emitter.onComplete()
                    }
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }

    }

}