package ru.silantyevmn.mymessenger.di.module

import dagger.Module
import dagger.Provides
import ru.silantyevmn.mymessenger.ui.image.ImageLoader
import ru.silantyevmn.mymessenger.ui.image.PiccassoImage
import javax.inject.Singleton

@Module
class ImageModule {

    @Singleton
    @Provides
    fun provideImageLoader(): ImageLoader {
        return PiccassoImage()
    }
}