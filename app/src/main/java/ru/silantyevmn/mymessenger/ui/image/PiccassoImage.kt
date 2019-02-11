package ru.silantyevmn.mymessenger.ui.image

import android.widget.ImageView
import com.squareup.picasso.Picasso
import ru.silantyevmn.mymessenger.R

class PiccassoImage : ImageLoader {
    override fun showImage(uri: String, image: ImageView) {
        Picasso.get().load(uri)
            .placeholder(R.drawable.placeholder)
            .into(image)
    }
}