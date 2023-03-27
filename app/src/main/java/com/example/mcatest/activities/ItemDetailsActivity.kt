package com.example.mcatest.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.example.mcatest.Constants
import com.example.mcatest.R
import com.example.mcatest.databinding.ActivityItemDetailsBinding
import com.example.mcatest.databinding.CustomDialogBinding
import com.example.mcatest.dto.MainDTO
import com.example.mcatest.utils.GetImageFromURL
import java.io.IOException

class ItemDetailsActivity : AppCompatActivity(){
    /** view binding object */
    private var binding: ActivityItemDetailsBinding? = null
    private var currentItem: MainDTO? = null

    /** Activity Result Object for handling image chooser */
    private val openStorage: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if (result.resultCode == Activity.RESULT_OK){
                val  dataIntent = result.data
                if (dataIntent != null && dataIntent.data != null){
                    val imageUri = dataIntent.data
                    var bitmapImage: Bitmap? = null
                    try{
                        bitmapImage = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                    this.binding?.ivItemDetailsImage?.setImageBitmap(bitmapImage)
                    this.currentItem?.changedImageURIString = imageUri.toString()
                    Constants.tList[this.currentItem?.position!!].bitmap = bitmapImage
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityItemDetailsBinding.inflate(layoutInflater)
        setContentView(this.binding?.root)

        // Check the intent data
        if (intent.hasExtra(Constants.ACTIVITY_ITEM_DETAILS_SEND_DATA_CODE))
            this.currentItem = this.intent.getSerializableExtra(Constants.ACTIVITY_ITEM_DETAILS_SEND_DATA_CODE) as MainDTO

        // populate UI
        if (this.currentItem != null)
            populateUI()

        // Set up the toolbar
        setUpToolbar()

        // Back clicked
        findViewById<ImageView>(R.id.iv_toolbarBack).setOnClickListener {
            Constants.TEMP_ITEM = null
            finish() }

        // Image clicked
        this.binding?.ivItemDetailsImage?.setOnClickListener { showImageFullScreen() }

        // Choose image clicked
        this.binding?.btnChooseImage?.setOnClickListener {
            if (Constants.checkPermissionStorage(this))
                imageChooser()
        }

        // delete clicked
        this.binding?.btnDelete?.setOnClickListener {
            customWarningDialog()
        }

        // save clicked
        findViewById<Button>(R.id.btn_toolbarSave).setOnClickListener {
            updateChanges()
        }
    }

    /** Method for populating the view */
    private fun populateUI(){
        val imageView = this.binding?.ivItemDetailsImage
        val title = this.binding?.tvItemDetailsTitle
        val tilTitle = this.binding?.tilItemDetailsTitle

        if (Constants.tList[this.currentItem?.position!!].bitmap != null)
            imageView?.setImageBitmap(Constants.tList[currentItem?.position!!].bitmap)
        else
            GetImageFromURL(imageView!!).execute(this.currentItem?.url)

        title?.hint = this.currentItem?.title
        title?.setOnClickListener {
            if (title.isFocused){
                tilTitle?.hint = "CHANGE TITLE"
            }
        }
    }

    /** Method for setting up the toolbar */
    private fun setUpToolbar(){
        val toolbarTitle = findViewById<TextView>(R.id.tv_toolbarTitle)
        val toolbarBack = findViewById<ImageView>(R.id.iv_toolbarBack)
        val toolbarSave = findViewById<Button>(R.id.btn_toolbarSave)

        toolbarTitle.setText(R.string.toolbar_title_text_item_details_activity)
        toolbarBack.visibility = View.VISIBLE
        toolbarSave.visibility = View.VISIBLE
    }

    /** Method for showing fullscreen image when clicked */
    private fun showImageFullScreen(){
        val intent = Intent(this@ItemDetailsActivity, ImageActivity::class.java)
        intent.putExtra(Constants.ACTIVITY_IMAGE_FULLSCREEN_SEND_DATA_CODE, this.currentItem)
        startActivity(intent)
    }

    /** Method for launching image chooser */
    private fun imageChooser(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        this.openStorage.launch(intent)
    }

    /** Button delete clicked. Method to delete item from recycler view */
    private fun deleteItem(){
        Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show()
        this.currentItem?.toDelete = true
        Constants.TEMP_ITEM = this.currentItem
        finish()
    }

    /** Warning Dialog */
    private fun customWarningDialog(){
        val dialog = Dialog(this)
        val dialogBinding = CustomDialogBinding.inflate(layoutInflater)

        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)

        dialogBinding.btnDecline.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnConfirm.setOnClickListener {
            dialog.dismiss()
            deleteItem()
        }
        dialog.show()
    }

    /** Save button clicked. Method to save and update the changes for an item */
    private fun updateChanges(){
        if (this.binding?.tvItemDetailsTitle?.text?.isEmpty() == true)
            this.currentItem?.title = this.binding?.tvItemDetailsTitle?.hint.toString()
        else
            this.currentItem?.title = this.binding?.tvItemDetailsTitle?.text.toString()
        Constants.TEMP_ITEM = currentItem
        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this.binding != null)
            this.binding = null
    }
}