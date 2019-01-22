package ru.silantyevmn.mymessenger.di

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import io.paperdb.Paper

class App: Application() {
    companion object {
        private lateinit var component: AppComponent
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: App
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
        //private lateinit var database: AppDatabase
        fun getInstance(): App = instance
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this
        component = DaggerAppComponent.create()
        Paper.init(this)
    }

    fun getContext(): Context = context
    //fun getDatabase(): AppDatabase = database
    fun getComponent(): AppComponent = component

}