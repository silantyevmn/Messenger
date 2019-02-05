package ru.silantyevmn.mymessenger.di.module

import dagger.Module
import dagger.Provides
import ru.silantyevmn.mymessenger.model.database.IStorageDatabase
import ru.silantyevmn.mymessenger.model.database.firebase.*
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideChatDatabase(): IChatDatabase {
        return ChatFirebase()
    }

    @Singleton
    @Provides
    fun provideUserDatabase(): IUserDatabase {
        return UserFirebase()
    }

    @Singleton
    @Provides
    fun provideAuthUser(): IAuthUser {
        return AuthUserFirebase()
    }

    @Singleton
    @Provides
    fun provideStorage(): IStorageDatabase {
        return StorageFirebase()
    }

}