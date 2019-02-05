package ru.silantyevmn.mymessenger.ui.login

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.loading.*
import ru.silantyevmn.mymessenger.R
import ru.silantyevmn.mymessenger.di.App
import ru.silantyevmn.mymessenger.model.repo.IRepo
import ru.silantyevmn.mymessenger.ui.MessengerActivity
import javax.inject.Inject

class LoginActivity : MvpAppCompatActivity(), LoginView {
    lateinit var loadingLayout: ConstraintLayout

    @Inject
    lateinit var repo: IRepo

    val TAG = "RegisterActivity"

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    @ProvidePresenter
    fun providePresenter() = LoginPresenter(repo)

    override fun onCreate(savedInstanceState: Bundle?) {
        App.getInstance().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loadingLayout = loading_layout
        //заполняем поля тестовыми данными
        email_edittext_register.setText("putin@mail.ru")
        pass_edittext_register.setText("123456")

        button_register.setOnClickListener {
            performRegister()
        }

        back_new_register.setOnClickListener {
            finish()
        }


    }

    private fun performRegister() {
        val email = email_edittext_register.text.toString()
        val pass = pass_edittext_register.text.toString()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, " Please enter the email/password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "email: $email")
        Log.d(TAG, "password: $pass")

        presenter.signInWithEmailAndPassword(email, pass);
    }

    override fun showMessenger() {
        val intent = Intent(this, MessengerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun showLoadingError(textError: String?) {
        loadingLayout.visibility = View.GONE
        Toast.makeText(this, textError, Toast.LENGTH_SHORT).show()
    }

    override fun showLoadingSuccess(email: String) {
        loadingLayout.visibility = View.GONE
        Log.d(TAG, "The user(${email})is successfully logged in to FirebaseDatabase")
    }

    override fun showLoading(text: String) {
        loadingLayout.visibility = View.VISIBLE
        loading_text.text = text
    }
}
