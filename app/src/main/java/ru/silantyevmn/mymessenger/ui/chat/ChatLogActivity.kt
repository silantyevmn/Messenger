package ru.silantyevmn.mymessenger.ui.chat

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.loading.*
import ru.silantyevmn.mymessenger.R
import ru.silantyevmn.mymessenger.di.App
import ru.silantyevmn.mymessenger.model.entity.User
import ru.silantyevmn.mymessenger.model.repo.IRepo
import ru.silantyevmn.mymessenger.model.repo.Repo
import ru.silantyevmn.mymessenger.ui.NewMessengerActivity
import javax.inject.Inject


class ChatLogActivity : MvpAppCompatActivity(), ChatView {
    lateinit var adapter: ChatAdapter

    lateinit var recyclerView: RecyclerView
    lateinit var sendText: EditText
    lateinit var loadingLayout: LinearLayout
    lateinit var loadindTextView: TextView

    @Inject
    lateinit var repo: IRepo

    @InjectPresenter
    lateinit var presenter: ChatPresenter

    @ProvidePresenter
    fun providePresenter() = ChatPresenter(
        repo, intent.getStringExtra(NewMessengerActivity.FROM_USER),
        intent.getStringExtra(NewMessengerActivity.TO_USER)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        App.getInstance().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val currentUserUid = intent.getStringExtra(NewMessengerActivity.FROM_USER)
        val toUserUid = intent.getStringExtra(NewMessengerActivity.TO_USER)

        //presenter = ChatPresenter(currentUserUid, toUserUid, this);

        adapter = ChatAdapter(presenter)

        loadingLayout = loading
        loadindTextView = loading_text

        presenter.loadUsers()

        recyclerView = chat_recycler
        recyclerView.adapter = adapter

        sendText = chat_input_text

        chat_button.setOnClickListener {
            val textMessage = sendText.text.toString()
            if (textMessage.isEmpty() || textMessage.length < 1) {
                //проверка на пустое сообщение
                Toast.makeText(this, "Сообщение пустое!", Toast.LENGTH_LONG).show()
            } else {
                presenter.pushMessages(textMessage, System.currentTimeMillis())
            }

        }
    }

    override fun updateAdapter() {
        adapter.notifyDataSetChanged()
    }

    override fun updateStatus(position: Int) {
        adapter.notifyItemChanged(position)
    }

    override fun onError(textError: String) {
        loadingLayout.visibility = View.INVISIBLE
        Toast.makeText(this, textError, Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess() {
        loadingLayout.visibility = View.INVISIBLE
    }

    override fun showLoading(text: String) {
        loadingLayout.visibility = View.VISIBLE
        loadindTextView.text = text
    }

    override fun setTextMessagesClear() {
        sendText.text.clear()
    }

    override fun setScrollRecycler() {
        recyclerView.scrollToPosition(adapter.getItemCount() - 1)
    }

    override fun setTitleToolbar(user: User) {
        supportActionBar?.title = user.loginName
    }
}




