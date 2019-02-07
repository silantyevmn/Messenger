package ru.silantyevmn.mymessenger.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_newmessenger.*
import ru.silantyevmn.mymessenger.R
import ru.silantyevmn.mymessenger.di.App
import ru.silantyevmn.mymessenger.model.entity.User
import ru.silantyevmn.mymessenger.model.repo.IRepo
import ru.silantyevmn.mymessenger.ui.chat.ChatLogActivity
import javax.inject.Inject

class NewMessengerActivity : AppCompatActivity() {
    lateinit var fromUser: User
    lateinit var adapter: GroupAdapter<ViewHolder>

    @Inject
    lateinit var repo: IRepo

    companion object {
        val TO_USER = "TO_USER"
        val FROM_USER = "FROM_USER"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.getInstance().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newmessenger)

        var toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.include_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Select User"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val recycler = recycler_newmessenger


        adapter = GroupAdapter()
        recycler.adapter = adapter


        fetchUsers()

        adapter.setOnItemClickListener { item, view ->
            val userItem = item as UserAdapterViewHolder
            if (userItem != null) {
                val intent = Intent(this, ChatLogActivity::class.java)
                intent.putExtra(TO_USER, userItem.user.uid)
                intent.putExtra(FROM_USER, fromUser.uid)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun fetchUsers() {
        repo.getUserList()
            .subscribe { listUser ->
                for (user in listUser) {
                    if (user.uid.equals(FirebaseAuth.getInstance().uid)){
                        fromUser = user
                        continue
                    }
                    adapter.add(UserAdapterViewHolder(user))
                }
            }
    }

}

class UserAdapterViewHolder(var user: User) : Item<ViewHolder>() {
    lateinit var image: CircleImageView
    lateinit var name: TextView

    override fun createViewHolder(itemView: View): ViewHolder {
        image = itemView.findViewById(R.id.item_user_image)
        name = itemView.findViewById(R.id.item_user_name)
        return super.createViewHolder(itemView)
    }

    override fun getLayout() = R.layout.user_item_row

    override fun bind(viewHolder: ViewHolder, position: Int) {
        name.setText(user.loginName)
        Picasso.get()
            .load(user.imagePhotoUri)
            .placeholder(R.drawable.placeholder)
            .into(image);
    }
}


