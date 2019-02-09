package ru.silantyevmn.mymessenger.ui.newmessenger

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import ru.silantyevmn.mymessenger.R
import ru.silantyevmn.mymessenger.model.entity.User
import ru.silantyevmn.mymessenger.ui.image.ImageLoader


class NewMessengerAdapter(val presenter: NewMessengerPresenter, val imageLoader: ImageLoader) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return presenter.userList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item_row, parent, false)
        return MyViewHolder(view, imageLoader, presenter)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bind(presenter.userList.get(position))
            holder.itemView.setOnClickListener {
                presenter.onClickItem(position)
            }
        }
    }

    private class MyViewHolder(itemView: View, val imageLoader: ImageLoader, val presenter: NewMessengerPresenter) :
        RecyclerView.ViewHolder(itemView){
        private val image: CircleImageView
        private val name: TextView

        init {
            image = itemView.findViewById(R.id.item_user_row_image)
            name = itemView.findViewById(R.id.item_user_row_name)
        }

        fun bind(user: User) {
            name.text = user.loginName
            imageLoader.showImage(user.imagePhotoUri, image)
        }
    }
}