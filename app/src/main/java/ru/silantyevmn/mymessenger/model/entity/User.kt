package ru.silantyevmn.mymessenger.model.entity

import java.io.Serializable


class User(val uid: String, val loginName: String, val imagePhotoUri: String): Serializable {
    constructor(): this("","","")
}