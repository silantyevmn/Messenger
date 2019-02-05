package ru.silantyevmn.mymessenger.di.module

import dagger.Module
import dagger.Provides
import ru.silantyevmn.mymessenger.model.cache.IChatCache
import ru.silantyevmn.mymessenger.model.cache.IUserCache
import ru.silantyevmn.mymessenger.model.database.IStorageDatabase
import ru.silantyevmn.mymessenger.model.database.firebase.*
import ru.silantyevmn.mymessenger.model.repo.IRepo
import ru.silantyevmn.mymessenger.model.repo.Repo
import javax.inject.Singleton

@Module(includes = (arrayOf(DatabaseModule::class,CacheModule::class)))
class RepoModule {

    @Singleton
    @Provides
    fun provideRepo(userDatabase: IUserDatabase,
                    chatDatabase: IChatDatabase,
                    authDatabase: IAuthUser,
                    storageDatabase: IStorageDatabase,
                    chatCache: IChatCache,
                    userCache: IUserCache
    ): IRepo {
        return Repo(userDatabase, chatDatabase,authDatabase,storageDatabase,chatCache,userCache)
    }

}