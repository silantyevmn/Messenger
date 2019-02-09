package ru.silantyevmn.mymessenger.ui.newmessenger

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_newmessenger.*
import kotlinx.android.synthetic.main.loading.*
import ru.silantyevmn.mymessenger.R
import ru.silantyevmn.mymessenger.di.App
import ru.silantyevmn.mymessenger.model.repo.IRepo
import ru.silantyevmn.mymessenger.ui.chat.ChatLogActivity
import ru.silantyevmn.mymessenger.ui.image.ImageLoader
import ru.silantyevmn.mymessenger.ui.login.LoginActivity
import javax.inject.Inject

class NewMessengerActivity : MvpAppCompatActivity(), NewMessengerView {
    private lateinit var adapter: NewMessengerAdapter
    private val TAG = "NewMessengerActivity"
    private lateinit var loadingLayout: ConstraintLayout
    private lateinit var loadingText: TextView

    @Inject
    lateinit var repo: IRepo

    @Inject
    lateinit var imageLoader: ImageLoader

    @InjectPresenter
    lateinit var presenter: NewMessengerPresenter

    @ProvidePresenter
    fun providePresenter() = NewMessengerPresenter(repo)

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

        presenter.init(FirebaseAuth.getInstance().uid)

        val recycler = recycler_newmessenger

        loadingLayout = loading_layout
        loadingText = loading_text

        adapter = NewMessengerAdapter(presenter, imageLoader)
        recycler.adapter = adapter

    }

    override fun showChatIntent(currentUserUid: String, toUserUid: String) {
        val intent = Intent(this, ChatLogActivity::class.java)
        intent.putExtra(TO_USER, toUserUid)
        intent.putExtra(FROM_USER, currentUserUid)
        startActivity(intent)
        finish()
    }

    override fun showLoginIntent() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun updateAdapter() {
        adapter.notifyDataSetChanged()
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


