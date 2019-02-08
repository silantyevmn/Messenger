package ru.silantyevmn.mymessenger.ui.messenger

import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.home_item_row.view.*
import ru.silantyevmn.mymessenger.R
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import ru.silantyevmn.mymessenger.model.entity.User

class MessengerAdapter(val user: User, val chatMessage: ChatMessage) : Item<ViewHolder>() {

    override fun getLayout() = R.layout.home_item_row

    override fun bind(viewHolder: ViewHolder, position: Int) {
        if (chatMessage.typeMessage.equals("text")) {
            viewHolder.itemView.home_item_messages.text = chatMessage.message
        } else if (chatMessage.typeMessage.equals("image")) {
            viewHolder.itemView.home_item_messages.text = "Получено изображение"
        } else {
            throw RuntimeException("Неизвестный тип")
        }
        Picasso.get()
            .load(user.imagePhotoUri)
            .placeholder(R.drawable.placeholder)
            .into(viewHolder.itemView.home_item_user_photo);

        viewHolder.itemView.home_item_username.text = user.loginName
    }
}