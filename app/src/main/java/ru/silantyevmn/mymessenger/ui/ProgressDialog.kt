package ru.silantyevmn.mymessenger.ui

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context

class ProgressDialog() {
    lateinit var progressDialog: ProgressDialog

    constructor(context: Context) : this() {
        init(context)
    }

    fun init(context: Context){
        progressDialog = ProgressDialog(context)
        // меняем стиль на индикатор
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // включаем анимацию ожидания
        progressDialog.setIndeterminate(true);
        // апрет смахивания с экрана
        progressDialog.setCancelable(false)
    }

    fun getDialog() = progressDialog

    fun setMessage(message:String){
        progressDialog.setMessage(message)
    }
    fun show(){
        progressDialog.show()
    }

    fun hide(){
        progressDialog.dismiss()
    }
}