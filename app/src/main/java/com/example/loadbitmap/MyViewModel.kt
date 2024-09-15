package com.example.loadbitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MyViewModel : ViewModel() {

    private val _bitmapLiveData = MutableLiveData<Bitmap?>()
    val bitmapLiveData: LiveData<Bitmap?> get() = _bitmapLiveData

    fun loadBitmap(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bitmap = loadBitmapFromUrl(url)
                withContext(Dispatchers.Main) {
                    _bitmapLiveData.value = bitmap
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _bitmapLiveData.value = null
                }
            }
        }
    }

    private fun loadBitmapFromUrl(urlString: String): Bitmap? {
        try {
            val url = URL(urlString)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream: InputStream = connection.inputStream
                return BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
