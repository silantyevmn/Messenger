package ru.silantyevmn.mymessenger.di

import dagger.Component
import ru.silantyevmn.mymessenger.di.module.DatabaseModule
import ru.silantyevmn.mymessenger.di.module.RepoModule
import ru.silantyevmn.mymessenger.ui.LoginActivity
import ru.silantyevmn.mymessenger.ui.MessengerActivity
import ru.silantyevmn.mymessenger.ui.NewMessengerActivity
import ru.silantyevmn.mymessenger.ui.RegisterActivity
import ru.silantyevmn.mymessenger.ui.chat.ChatLogActivity
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(DatabaseModule::class, RepoModule::class))
interface AppComponent {
    fun inject(chatActivity: ChatLogActivity)
    fun inject(messengerActivity: MessengerActivity)
    fun inject(newMessengerActivity: NewMessengerActivity)
    fun inject(logActivity: LoginActivity)
    fun inject(registerActivity: RegisterActivity)
}