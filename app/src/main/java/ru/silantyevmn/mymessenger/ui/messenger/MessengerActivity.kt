package ru.silantyevmn.mymessenger.ui.messenger

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messenger.*
import kotlinx.android.synthetic.main.loading.*
import ru.silantyevmn.mymessenger.R
import ru.silantyevmn.mymessenger.di.App
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import ru.silantyevmn.mymessenger.model.entity.User
import ru.silantyevmn.mymessenger.model.repo.IRepo
import ru.silantyevmn.mymessenger.ui.NewMessengerActivity
import ru.silantyevmn.mymessenger.ui.chat.ChatLogActivity
import ru.silantyevmn.mymessenger.ui.register.RegisterActivity
import javax.inject.Inject

class MessengerActivity : MvpAppCompatActivity(), MessengerView {
    val TAG = "MessengerActivity"
    var adapter = GroupAdapter<ViewHolder>()
    lateinit var loadingLayout: ConstraintLayout
    lateinit var loadingText: TextView

    @Inject
    lateinit var repo: IRepo

    @InjectPresenter
    lateinit var presenter: MessengerPresenter

    @ProvidePresenter
    fun providePresenter() = MessengerPresenter(repo)

    override fun onCreate(savedInstanceState: Bundle?) {
        App.getInstance().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messenger)
        
        var toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.include_toolbar)
        setSupportActionBar(toolbar)

        loadingLayout = loading_layout
        loadingText = loading_text

        home_recyclerview.adapter = adapter

        presenter.init(FirebaseAuth.getInstance().uid)

        adapter.setOnItemClickListener { item, view ->
            var messengerAdapter = item as MessengerAdapter
            var currentUserUid = presenter.currentUserUid
            if (messengerAdapter != null) {
                val intent = Intent(this, ChatLogActivity::class.java)
                intent.putExtra(NewMessengerActivity.TO_USER, messengerAdapter.user.uid)
                intent.putExtra(NewMessengerActivity.FROM_USER, currentUserUid)
                startActivity(intent)
            }
        }
    }

    override fun updateAdapter(user: User, chatMessage: ChatMessage) {
        adapter.add(MessengerAdapter(user, chatMessage))
    }

    override fun showLoginActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
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

    override fun showLoadingError(textError: String?) {
        loadingLayout.visibility = View.GONE
        Toast.makeText(this, textError, Toast.LENGTH_SHORT).show()
    }

    override fun showLoadingSuccess() {
        loadingLayout.visibility = View.GONE
        Log.d(TAG, "Loading success")
    }

    override fun showLoading(text: String) {
        loadingLayout.visibility = View.VISIBLE
        loadingText.text = text
    }
}
