package com.example.mcatest.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import com.example.mcatest.Constants
import com.example.mcatest.R
import com.example.mcatest.databinding.ActivityImageBinding
import com.example.mcatest.dto.MainDTO
import com.example.mcatest.utils.GetImageFromURL

class ImageActivity : AppCompatActivity() {
    /** view  binding object */
    private var binding: ActivityImageBinding? = null
    private  var currentItem: MainDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(this.binding?.root)

        // check for the data
        if (this.intent.hasExtra(Constants.ACTIVITY_IMAGE_FULLSCREEN_SEND_DATA_CODE))
            this.currentItem = intent.getSerializableExtra(Constants.ACTIVITY_IMAGE_FULLSCREEN_SEND_DATA_CODE) as MainDTO

        // Set up the UI
        setUpUI()

        // back pressed
        findViewById<ImageView>(R.id.iv_toolbarBack).setOnClickListener { backPressed() }
    }

    /** Method for setting up UI*/
    private fun setUpUI(){
        findViewById<TextView>(R.id.tv_toolbarTitle).setText(R.string.toolbar_title_text_item_image_view)
        findViewById<Button>(R.id.btn_toolbarSave).visibility = View.INVISIBLE
        if (this.currentItem?.changedImageURIString != null){
            this.binding?.ivItemImageFull?.setImageBitmap(Constants.tList[this.currentItem?.position!!].bitmap)
        }
        else
            GetImageFromURL(this.binding?.ivItemImageFull!!).execute(this.currentItem?.url)
    }

    private fun backPressed(){
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null)
            binding = null
    }
}