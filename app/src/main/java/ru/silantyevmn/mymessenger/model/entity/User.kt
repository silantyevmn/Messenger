package ru.silantyevmn.mymessenger.model.entity

import java.io.Serializable


class User(val uid: String, val loginName: String, val imagePhotoUri: String): Serializable {
    constructor(): this("","","")

    override fun equals(other: Any?): Boolean {
        var tempUser = other as User
        if(tempUser == null) return false
        return uid.equals(tempUser.uid)
    }
}