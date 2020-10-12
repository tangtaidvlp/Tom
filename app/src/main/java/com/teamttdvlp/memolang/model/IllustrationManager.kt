package com.teamttdvlp.memolang.model

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import com.teamttdvlp.memolang.view.helper.systemOutLogging
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class IllustrationManager
@Inject
constructor
    (private var app: Application) {

    fun saveFile(b: Bitmap, picName: String?) {
        Thread(Runnable {
            var fos: FileOutputStream? = null
            try {
                fos = app.openFileOutput(picName, Context.MODE_PRIVATE)
                b.compress(Bitmap.CompressFormat.PNG, 100, fos)
            } catch (e: FileNotFoundException) {
                systemOutLogging("file not found")
                e.printStackTrace()
            } catch (e: IOException) {
                systemOutLogging("io exception")
                e.printStackTrace()
            } catch (e: Exception) {
                systemOutLogging("Other exception")
                e.printStackTrace()
            } finally {
                fos?.close()
            }
        }).start()
    }

    fun loadBitmap(picName: String?, onGetBitmap: (Exception?, Bitmap?) -> Unit) {
        LoadImageTask(onGetBitmap).execute(picName)
    }

    private inner class LoadImageTask(var onGetBitmap: (Exception?, Bitmap?) -> Unit) :
        AsyncTask<String, Unit, Bitmap?>() {

        private var exception: Exception? = null

        override fun doInBackground(vararg params: String?): Bitmap? {
            val picName = params[0]
            var bitmap: Bitmap? = null
            var fileInputStream: FileInputStream? = null
            try {
                fileInputStream = app.openFileInput(picName)
                bitmap = BitmapFactory.decodeStream(fileInputStream)
            } catch (e: FileNotFoundException) {
                systemOutLogging("file not found")
                exception = e
                e.printStackTrace()
            } catch (e: IOException) {
                systemOutLogging("io exception")
                exception = e
                e.printStackTrace()
            } catch (e: Exception) {
                systemOutLogging("Another error")
                exception = e
                e.printStackTrace()
            } finally {
                fileInputStream?.close()
            }

            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            onGetBitmap(exception, result)
        }
    }

}