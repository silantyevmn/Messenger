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
import ru.silantyevmn.mymessenger.ui.image.ImageLoader
import ru.silantyevmn.mymessenger.ui.login.LoginActivity
import ru.silantyevmn.mymessenger.ui.register.RegisterActivity
import javax.inject.Inject

class MessengerActivity : MvpAppCompatActivity(), MessengerView {
    private val TAG = "MessengerActivity"
    private var adapter = GroupAdapter<ViewHolder>()
    private lateinit var loadingLayout: ConstraintLayout
    private lateinit var loadingText: TextView

    @Inject
    lateinit var repo: IRepo

    @Inject
    lateinit var imageLoader:ImageLoader

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
            presenter.onClickItem(messengerAdapter.user.uid)
        }
    }

    override fun updateAdapter(user: User, chatMessage: ChatMessage) {
        adapter.add(MessengerAdapter(user, chatMessage,imageLoader))
    }

    override fun showChatIntent(fromUserUid:String, toUserUid:String){
        val intent = Intent(this, ChatLogActivity::class.java)
        intent.putExtra(NewMessengerActivity.TO_USER, toUserUid)
        intent.putExtra(NewMessengerActivity.FROM_USER, fromUserUid)
        startActivity(intent)
    }

    override fun showLoginIntent() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun showNewMessengerIntent() {
        val intent = Intent(this, NewMessengerActivity::class.java)
        startActivity(intent)
    }

    override fun showRegisterIntent() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
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
                presenter.onClickMenuNewMessenger()
        }
            R.id.menu_sign_out -> {
                presenter.onClickMenuSignOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showError(textError: String?) {
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
