package ru.silantyevmn.mymessenger.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.loading.*
import ru.silantyevmn.mymessenger.R
import ru.silantyevmn.mymessenger.di.App
import ru.silantyevmn.mymessenger.model.repo.IRepo
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    //private lateinit var auth: FirebaseAuth
    @Inject
    lateinit var repo: IRepo

    val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        App.getInstance().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //auth = FirebaseAuth.getInstance()
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

        showLoading("Проверка пользователя в базе...")
        repo.signInWithEmailAndPassword(email, pass)
            .subscribe({
                showLoadingSuccess(email)
                val intent = Intent(this, MessengerActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }, {
                showLoadingError(it.message)
            })
    }

    private fun showLoadingError(textError: String?) {
        loading.visibility = View.GONE
        Toast.makeText(this, textError, Toast.LENGTH_SHORT).show()
    }

    private fun showLoadingSuccess(email: String) {
        loading.visibility = View.GONE
        Log.d(TAG, "The user(${email})is successfully logged in to FirebaseDatabase")
    }

    private fun showLoading(text: String) {
        loading.visibility = View.VISIBLE
        loading_text.text = text
    }
}
