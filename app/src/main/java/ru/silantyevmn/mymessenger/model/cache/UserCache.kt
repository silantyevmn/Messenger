package ru.silantyevmn.mymessenger.model.cache

import io.paperdb.Paper
import io.reactivex.Observable
import ru.silantyevmn.mymessenger.model.entity.User

class UserCache : IUserCache {
    override fun getUserList(): Observable<List<User>> {
        var users = Paper.book().read<ArrayList<User>>("users")
        if (users == null) {
            users = ArrayList()
        }
        return Observable.just(users)
    }

    override fun getUserFromId(userId: String): Observable<User?> {
        var users = Paper.book().read<ArrayList<User>>("users")
        if (users == null) {
            users = ArrayList()
        }
        //бежим по коллекции
        for (user in users) {
            if (user.uid.equals(userId)) {
                return Observable.just(user)
            }
        }
        return Observable.just(null)
    }

    override fun insertUser(user: User) {
        var users = Paper.book().read<ArrayList<User>>("users")
        if (users == null) {
            users = ArrayList()
        }
        if (users.contains(user)) {
            return
        } else {
            users.add(user)
        }
        Paper.book().write("users", users)
    }

    override fun deleteUser(user: User) {
        var users = Paper.book().read<ArrayList<User>>("users")
        if (users == null) {
            users = ArrayList()
        }
        if (users.contains(user)) {
            users.remove(user)
        } else {
            return
        }
        Paper.book().write("users", users)
    }

    override fun updateUser(user: User) {
        var users = Paper.book().read<ArrayList<User>>("users")
        if (users == null) {
            users = ArrayList()
        }
        if (users.contains(user)) {
            users.set(users.indexOf(user), user)
        } else {
            return
        }
        Paper.book().write("users", users)
    }
}
