package com.noble.activity.instaclone.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraPictureTaker(private val activity: Activity) {
    val TAKE_PICTURE_REQUEST_CODE = 1

    private val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

    var mImageUri: Uri? = null

    fun takeCameraPicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity.packageManager) != null) {
            val imageFile = createImageFile()
            mImageUri = FileProvider.getUriForFile(
                    activity,
                    "com.noble.activity.instaclone.fileprovider",
                    imageFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
            activity.startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE)
        }
    }

    private fun createImageFile(): File {
        val storageDir: File = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${simpleDateFormat.format(Date())}_",
                ".jpg",
                storageDir
        )
    }
}