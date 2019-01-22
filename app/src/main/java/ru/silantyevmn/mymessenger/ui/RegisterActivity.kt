package ru.silantyevmn.mymessenger.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.loading.*
import ru.silantyevmn.mymessenger.R
import ru.silantyevmn.mymessenger.di.App
import ru.silantyevmn.mymessenger.model.entity.User
import ru.silantyevmn.mymessenger.model.repo.IRepo
import javax.inject.Inject

class RegisterActivity : AppCompatActivity() {
    @Inject
    lateinit var repo: IRepo

    private lateinit var currenUserUid: String

    val TAG: String = "RegisterActivity"
    val PHOTO_REQUEST_CODE: Int = 0
    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        App.getInstance().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        button_register.setOnClickListener {
            performRegister()
        }
        register_text.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        select_photo_button_register.setOnClickListener {
            Log.d(TAG, "Try to show photo selected")
            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            startActivityForResult(intent, PHOTO_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            //process import photo
            Log.d(TAG, "Photo was selected")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            selected_photo_image_register.setImageBitmap(bitmap)
            select_photo_button_register.alpha = 0f
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

        showLoading("Сохранение пользователя...")
        repo.createUserWithEmailAndPassword(email, pass)
            .subscribe({
                if (it != null) {
                    currenUserUid = it
                    Log.d(TAG, " The toUser is successfully logged in, id: $currenUserUid")
                    uploadImageToFirebaseStore()
                } else {
                    showLoadingError("Failed to create toUser. User is null")
                }
            }, {
                showLoadingError("Failed to create toUser. ${it.message}")
            })
    }

    private fun uploadImageToFirebaseStore() {
        showLoading("Сохраняем фото в базу...")
        if (selectedPhotoUri == null) {
            saveUserToFirebaseDatabase("-1")
            return
        }
        repo.putFile(selectedPhotoUri!!)
            .subscribe({
                if (it != null) {
                    saveUserToFirebaseDatabase(it)
                } else {
                    showLoadingError("file is null")
                }

            }, {
                showLoadingError(it.message)
            })
    }

    private fun saveUserToFirebaseDatabase(imageUserPhotoUri: String) {
        showLoading("Обновление данных пользователя...")
        var user =
            User(
                currenUserUid,
                login_edittext_register.text.toString(),
                imageUserPhotoUri
            )
        repo.insertUser(user)
            .subscribe({
                Log.d(TAG, "Finale we saved the toUser to FirebaseDatabase")
                showLoadingSuccess(currenUserUid)

                val intent = Intent(this, MessengerActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }, {
                showLoadingError("Error! ${it.message.toString()}")
            })
    }

    private fun showLoadingError(textError: String?) {
        loading.visibility = View.GONE
        Toast.makeText(this, textError, Toast.LENGTH_SHORT).show()
    }

    private fun showLoadingSuccess(text: String) {
        loading.visibility = View.GONE
        Log.d(TAG, text)
    }

    private fun showLoading(text: String) {
        loading.visibility = View.VISIBLE
        loading_text.text = text
    }
}
