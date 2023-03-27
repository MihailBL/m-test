package com.example.mcatest.dto

import android.graphics.Bitmap
import android.graphics.drawable.Icon
import java.io.Serializable

data class MainDTO(
    val albumId: Int,
    val id: Int,
    var title: String,
    val url: String,
    val thumbnailUrl: String,
    var changedImageURIString: String? = null,
    var toDelete: Boolean = false,
    var position: Int = 0
): java.io.Serializable