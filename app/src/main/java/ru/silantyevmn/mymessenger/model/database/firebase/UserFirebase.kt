package ru.silantyevmn.mymessenger.model.database.firebase

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Completable
import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.entity.User


class UserFirebase : IUserDatabase {

    override fun getUserList(): Observable<List<User>> {
        return Observable.create { emitter ->
            var ref = FirebaseDatabase.getInstance().getReference("/users")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    emitter.onError(p0.toException())
                }

                override fun onDataChange(data: DataSnapshot) {
                    var tempList = ArrayList<User>()
                    data.children.forEach {
                        Log.d("getUserList ", " $it.toString()")
                        var user = it.getValue(User::class.java)
                        if (user != null) {
                            tempList.add(user)
                        }

                    }
                    emitter.onNext(tempList)
                    emitter.onComplete()
                }
            })
        }
    }

    override fun insertUser(user: User): Completable {
        return Completable.create { emitter ->
            val ref = FirebaseDatabase.getInstance().getReference("/users/${user.uid}")
            ref.setValue(user)
                .addOnSuccessListener {
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    override fun deleteUser(user: User) {
        //
    }

    override fun updateUser(user: User) {
        //
    }

    override fun getUserFromId(userId: String): Observable<User> {
        return Observable.create {
            var ref = FirebaseDatabase.getInstance().getReference("/users/$userId")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    it.onError(p0.toException())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var findUser = p0.getValue(User::class.java) ?: return
                    if (userId.equals(findUser.uid)) {
                        it.onNext(findUser)
                        it.onComplete()
                    }
                }

            })

        }
    }

}