package ru.silantyevmn.mymessenger.ui.image

import android.widget.ImageView

interface ImageLoader {
    fun showImage(uri: String, image: ImageView)
}