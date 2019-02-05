package ru.silantyevmn.mymessenger.model

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.*

class FileManager {
    fun getUriToBitmapCompress(bitmapOrigin: Bitmap?):Uri?{
        if(bitmapOrigin==null) return null
        // Вычисляем ширину и высоту изображения
        var halfWidth = bitmapOrigin.getWidth();
        var halfHeight = bitmapOrigin.getHeight();
        if(halfWidth>640) halfWidth = 640;
        if(halfHeight>480) halfHeight = 480;
        // режим картинку под нужные размеры
        val bmHalf = Bitmap.createScaledBitmap(
            bitmapOrigin, halfWidth,
            halfHeight, false
        );

        //создаем файл
        var tempFile = createNewFile(".jpeg")

        try {
            var fos = FileOutputStream(tempFile)
            bmHalf.compress(Bitmap.CompressFormat.JPEG, 50, fos)

            fos.flush()
            fos.close()
        } catch (e: Exception) {
            Log.e("Compress Error", e.toString())
        }
        return Uri.fromFile(tempFile)
    }

    private fun createNewFile(suffix:String): File? {
        var fileName = UUID.randomUUID().toString()
        return File.createTempFile(fileName, suffix)
    }

    fun deleteFile(message: String)= File(message).delete()
}