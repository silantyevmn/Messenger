package ru.silantyevmn.mymessenger.di.module

import dagger.Module
import dagger.Provides
import ru.silantyevmn.mymessenger.model.cache.paper.ChatCachePaper
import ru.silantyevmn.mymessenger.model.cache.IChatCache
import ru.silantyevmn.mymessenger.model.cache.IUserCache
import ru.silantyevmn.mymessenger.model.cache.paper.UserCachePaper
import javax.inject.Singleton

@Module
class CacheModule {
    @Singleton
    @Provides
    fun provideChatCache(): IChatCache {
        return ChatCachePaper()
    }

    @Singleton
    @Provides
    fun provideUserCache(): IUserCache {
        return UserCachePaper()
    }
}