package com.example.mcatest

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.example.mcatest.dto.MainDTO
import com.example.mcatest.dto.TempDTO

object Constants {

    /** CONST variable containing the key for the data sent to ItemDetailsActivity */
    const val ACTIVITY_ITEM_DETAILS_SEND_DATA_CODE = "itemDetails"
    /** CONST variable containing the key for the data sent to ImageActivity */
    const val ACTIVITY_IMAGE_FULLSCREEN_SEND_DATA_CODE= "imageFullscreen"
    /** delete item position */
    var TEMP_ITEM: MainDTO? = null
    /** Temporary list for saving the bitmap to corresponding item position */
    var tList: ArrayList<TempDTO> = arrayListOf()

    /** Storage permission request code */
    var STORAGE_PERMISSION_REQUEST_CODE = 100

    /** Function for checking the network connectivity */
    fun isNetworkAvailable(context: Context): Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetworkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when{
            activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    /** Method for checking permissions */
    fun checkPermissionStorage(activity: Activity): Boolean{
        return if (ContextCompat.checkSelfPermission(activity.baseContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                showRequestRationaleDialog(activity)
            } else{
                ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
            }
            false
        } else{
            true
        }
    }

    /** Show custom permission rationale dialog */
    private fun showRequestRationaleDialog(activity: Activity){
        val alertDialogBuilder = AlertDialog.Builder(activity.baseContext)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setTitle("Permission Needed")
        alertDialogBuilder.setMessage("External Storage Permission is Needed")
        alertDialogBuilder.setPositiveButton("YES"){dialog, i ->
            ActivityCompat.requestPermissions(activity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
            dialog.dismiss()
        }
        alertDialogBuilder.show()
    }
}