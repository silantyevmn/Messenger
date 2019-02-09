package ru.silantyevmn.mymessenger.ui.chat

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import ru.silantyevmn.mymessenger.R
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import ru.silantyevmn.mymessenger.ui.image.ImageLoader
import ru.silantyevmn.mymessenger.utils.DateTimeManager


class ChatAdapter(val presenter: ChatPresenter, val imageLoader: ImageLoader) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolderRight(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val message: TextView
        private val time: TextView
        private val imageUser: CircleImageView
        private val status: ImageView
        private val messageImage: ImageView
        private val messageImageLayout: RelativeLayout

        init {
            message = itemView.findViewById(R.id.chat_item_right_messages_text)
            time = itemView.findViewById(R.id.chat_item_right_time)
            imageUser = itemView.findViewById(R.id.chat_item_right_image)
            status = itemView.findViewById(R.id.chat_item_right_status)
            messageImage = itemView.findViewById(R.id.chat_item_right_message_image_image)
            messageImageLayout = itemView.findViewById(R.id.chat_item_right_message_image)
        }

        fun bind(chatMessage: ChatMessage) {
            if (chatMessage.typeMessage.equals("text")) {
                message.text = chatMessage.message
                message.visibility = View.VISIBLE
                messageImageLayout.visibility = View.GONE
            } else if (chatMessage.typeMessage.equals("image")) {
                message.visibility = View.GONE
                messageImageLayout.visibility = View.VISIBLE

                imageLoader.showImage(chatMessage.message, messageImage)
            }
            time.text = DateTimeManager().getTime(chatMessage.time)
            when (chatMessage.status) {
                0 -> {
                }
                1 -> status.setImageResource(R.drawable.ic_done_black_24dp)
                2 -> status.setImageResource(R.drawable.ic_done_all_black_24dp)
            }
            imageLoader.showImage(presenter.currentUser.imagePhotoUri, imageUser)
        }
    }

    inner class ViewHolderLeft(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val message: TextView
        private val time: TextView
        private val imageUser: CircleImageView
        private val status: ImageView
        private val messageImage: ImageView

        init {
            message = itemView.findViewById(R.id.chat_item_left_messages)
            time = itemView.findViewById(R.id.chat_item_left_time)
            imageUser = itemView.findViewById(R.id.chat_item_left_image)
            status = itemView.findViewById(R.id.chat_item_left_status)
            messageImage = itemView.findViewById(R.id.chat_item_left_message_image)
        }

        fun bind(chatMessage: ChatMessage) {
            if (chatMessage.typeMessage.equals("text")) {
                message.text = chatMessage.message
                message.visibility = View.VISIBLE
                messageImage.visibility = View.GONE
            } else if (chatMessage.typeMessage.equals("image")) {
                message.visibility = View.GONE
                messageImage.visibility = View.VISIBLE
                imageLoader.showImage(chatMessage.message, messageImage)
            }
            time.text = DateTimeManager().getTime(chatMessage.time)
            when (chatMessage.status) {
                0 -> {
                }
                1 -> status.setImageResource(R.drawable.ic_done_black_24dp)
                2 -> status.setImageResource(R.drawable.ic_done_all_black_24dp)
            }
            imageLoader.showImage(presenter.toUser.imagePhotoUri, imageUser)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chatMessage = presenter.chatList[position]
        return if (chatMessage.fromUid == presenter.currentUserUid) {
            0
        } else {
            1
        }
    }

    override fun getItemCount(): Int {
        return presenter.chatList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val viewRight = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_right, parent, false)
                return ViewHolderRight(viewRight)
            }
            1 -> {
                val viewLeft = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_left, parent, false)
                return ViewHolderLeft(viewLeft)
            }
            else -> throw RuntimeException("Not null viewType!")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                val viewHolderRight = holder as ViewHolderRight
                viewHolderRight.bind(presenter.chatList[position])
            }
            1 -> {
                val viewHolderLeft = holder as ViewHolderLeft
                viewHolderLeft.bind(presenter.chatList[position])
            }
        }
    }
}