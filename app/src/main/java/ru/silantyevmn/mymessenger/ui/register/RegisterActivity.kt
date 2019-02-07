package ru.silantyevmn.mymessenger.ui.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.loading.*
import ru.silantyevmn.mymessenger.R
import ru.silantyevmn.mymessenger.di.App
import ru.silantyevmn.mymessenger.model.repo.IRepo
import ru.silantyevmn.mymessenger.ui.MessengerActivity
import ru.silantyevmn.mymessenger.ui.login.LoginActivity
import javax.inject.Inject


class RegisterActivity : MvpAppCompatActivity(), RegisterView {
    private val TAG: String = "RegisterActivity"
    private val PHOTO_REQUEST_CODE: Int = 0
    lateinit var loadingView: ConstraintLayout
    lateinit var loadingTextView :TextView

    @InjectPresenter
    lateinit var presenter: RegisterPresenter

    @Inject
    lateinit var repo: IRepo

    @ProvidePresenter
    fun providePresenter() = RegisterPresenter(repo)

    override fun onCreate(savedInstanceState: Bundle?) {
        App.getInstance().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        button_register.setOnClickListener {
            performRegister()
        }

        register_text.setOnClickListener {
            presenter.onClickLoginActivity()
        }
        select_photo_button_register.setOnClickListener {
            Log.d(TAG, "Try to show photo selected")
            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            startActivityForResult(intent, PHOTO_REQUEST_CODE)
        }
        loadingView = loading_layout
        loadingView.setOnClickListener {

        }
        loadingTextView = loading_text
    }

    override fun showLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "Photo was selected")
            presenter.setUserPhotoUri(data.data)
            /* //selectedPhotoUri = data.data
             val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
             selected_photo_image_register.setImageBitmap(bitmap)
             select_photo_button_register.alpha = 0f*/
        }

    }

    override fun showImageUserPhoto(userPhotoUri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, userPhotoUri)
        selected_photo_image_register.setImageBitmap(bitmap)
        select_photo_button_register.alpha = 0f
    }

    private fun performRegister() {
        val email = email_edittext_register.text.toString()
        val pass = pass_edittext_register.text.toString()
        val login = login_edittext_register.text.toString()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, " Please enter the email/password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "email: $email")
        Log.d(TAG, "password: $pass")

        presenter.createUserWithEmailAndPassword(login, email, pass)
    }

    override fun showLoadingError(textError: String) {
        loadingView.visibility = View.GONE
        Toast.makeText(this, textError, Toast.LENGTH_SHORT).show()
    }

    override fun showLoadingSuccess(text: String) {
        loadingView.visibility = View.GONE
        Log.d(TAG, text)
    }

    override fun showLoading(text: String) {
        loadingView.visibility = View.VISIBLE
        loadingTextView.text = text
    }

    override fun showMessenger() {
        val intent = Intent(this, MessengerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
