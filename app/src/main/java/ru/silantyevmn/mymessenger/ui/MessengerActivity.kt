package ru.silantyevmn.mymessenger.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messenger.*
import kotlinx.android.synthetic.main.home_item_row.view.*
import ru.silantyevmn.mymessenger.R
import ru.silantyevmn.mymessenger.di.App
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import ru.silantyevmn.mymessenger.model.repo.IRepo
import ru.silantyevmn.mymessenger.ui.chat.ChatLogActivity
import ru.silantyevmn.mymessenger.ui.register.RegisterActivity
import javax.inject.Inject

class MessengerActivity : AppCompatActivity() {
    var adapter = GroupAdapter<ViewHolder>()
    var homeUserList = HashMap<String, ChatMessage>()

    @Inject
    lateinit var repo: IRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        App.getInstance().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messenger)

        var toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.include_toolbar)
        setSupportActionBar(toolbar)

        verifyUserIsLoginIn()

        home_recyclerview.adapter = adapter

        loadUsers()

        adapter.setOnItemClickListener { item, view ->
            var homeAdapter = item as HomeMessagerAdapter
            var currentUserUid = FirebaseAuth.getInstance().uid
            if (homeAdapter != null) {
                var partnerUserUid: String
                if (homeAdapter.chatMessage.fromUid == currentUserUid)
                    partnerUserUid = homeAdapter.chatMessage.toUid
                else
                    partnerUserUid = homeAdapter.chatMessage.fromUid

                val intent = Intent(this, ChatLogActivity::class.java)
                intent.putExtra(NewMessengerActivity.TO_USER, partnerUserUid)
                intent.putExtra(NewMessengerActivity.FROM_USER, currentUserUid)
                startActivity(intent)
            }
        }

    }

    private fun updateAdapter() {
        adapter.clear()
        homeUserList.forEach { key, chatMessage ->
            adapter.add(HomeMessagerAdapter(repo, chatMessage))
        }
    }

    private fun loadUsers() {
        val currentUid = FirebaseAuth.getInstance().uid
        var ref = FirebaseDatabase.getInstance().getReference("/user-messages/${currentUid.toString()}")
        if (ref == null) return

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                for (postData in p0.children) {
                    val chatMessage = postData.getValue(ChatMessage::class.java) ?: continue
                    if (currentUid == chatMessage.fromUid) {
                        homeUserList[chatMessage.toUid] = chatMessage
                    } else
                        homeUserList[chatMessage.fromUid] = chatMessage

                }
                updateAdapter()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                for (postData in p0.children) {
                    val chatMessage = postData.getValue(ChatMessage::class.java) ?: continue
                    if (currentUid == chatMessage.fromUid) {
                        homeUserList[chatMessage.toUid] = chatMessage
                    } else
                        homeUserList[chatMessage.fromUid] = chatMessage
                }
                updateAdapter()
            }

            override fun onChildRemoved(p0: DataSnapshot) {}

        })


    }

    private fun verifyUserIsLoginIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_messager, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessengerActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

class HomeMessagerAdapter(val repo: IRepo, val chatMessage: ChatMessage) : Item<ViewHolder>() {

    override fun getLayout() = R.layout.home_item_row

    override fun bind(viewHolder: ViewHolder, position: Int) {
        if(chatMessage.typeMessage.equals("text")) {
            viewHolder.itemView.home_item_messages.text = chatMessage.message
        } else if(chatMessage.typeMessage.equals("image")) {
            viewHolder.itemView.home_item_messages.text = "Получено изображение"
        } else {
            throw RuntimeException("Неизвестный тип")
        }

        var partnerUserId: String
        if (chatMessage.fromUid == FirebaseAuth.getInstance().uid)
            partnerUserId = chatMessage.toUid
        else
            partnerUserId = chatMessage.fromUid

        repo.getUserFromId(partnerUserId)
            .subscribe {
                Picasso.get()
                    .load(it.imagePhotoUri)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.itemView.home_item_user_photo);
                viewHolder.itemView.home_item_username.text = it.loginName
            }
    }
}

