package com.example.mcatest.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.net.URL

/** Class for loading image from an URL */
class GetImageFromURL(imageView: ImageView): AsyncTask<String, Unit, Bitmap>() {
    private val imageView: ImageView
    private var bitmapImage: Bitmap? = null

    init {
        this.imageView = imageView
    }

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg urls: String?): Bitmap {
        val url = urls[0]
        bitmapImage = null
        try{
            val inputStream = URL(url).openStream()
            bitmapImage = BitmapFactory.decodeStream(inputStream)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return bitmapImage!!
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        this.imageView.setImageBitmap(bitmapImage)
    }
}