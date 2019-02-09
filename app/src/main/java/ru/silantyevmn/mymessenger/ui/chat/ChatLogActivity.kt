package ru.silantyevmn.mymessenger.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.loading.*
import kotlinx.android.synthetic.main.toolbar_chat.*
import ru.silantyevmn.mymessenger.R
import ru.silantyevmn.mymessenger.di.App
import ru.silantyevmn.mymessenger.model.entity.User
import ru.silantyevmn.mymessenger.model.repo.IRepo
import ru.silantyevmn.mymessenger.ui.newmessenger.NewMessengerActivity
import ru.silantyevmn.mymessenger.ui.image.ImageLoader
import javax.inject.Inject


class ChatLogActivity : MvpAppCompatActivity(), ChatView {

    private val PHOTO_REQUEST_CODE = 1
    private val TAG = "ChatLogActivity"
    lateinit var adapter: ChatAdapter

    lateinit var recyclerView: RecyclerView
    lateinit var sendText: EditText
    lateinit var loadingLayout: ConstraintLayout
    lateinit var loadindTextView: TextView

    @Inject
    lateinit var repo: IRepo

    @Inject
    lateinit var imageLoader: ImageLoader

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
        //initToolbar
        var toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.include_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        adapter = ChatAdapter(presenter, imageLoader)

        loadingLayout = loading_layout
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
                presenter.pushMessages(textMessage.trim())
            }

        }
        chat_image_load.setOnClickListener {
            presenter.onClickImageLoad()
        }
    }

    private fun hideKeyboard() {
        //скроем клавиатуру
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    override fun updateAdapter() {
        adapter.notifyDataSetChanged()
    }

    override fun updateAdapter(position: Int) {
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
        var imageIcon = toolbar_icon
        imageLoader.showImage(user.imagePhotoUri, imageIcon)
        toolbar_title.text = user.loginName
    }

    override fun showLoadImage() {
        Log.d(TAG, "Try to show photo selected")
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        startActivityForResult(intent, PHOTO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            //process import photo
            Log.d(TAG, "Photo was selected")
            var selectedPhotoUri = data.data
            val bitmapOrigin = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            var compressUri = presenter.getUriToBitmapCompress(bitmapOrigin);
            if (compressUri != null) {
                presenter.pushImage(compressUri)
            }

        }
    }

}




