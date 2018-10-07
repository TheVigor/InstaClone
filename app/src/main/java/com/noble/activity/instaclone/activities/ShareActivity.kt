package com.noble.activity.instaclone.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.noble.activity.instaclone.R
import com.noble.activity.instaclone.utils.CameraPictureTaker
import com.noble.activity.instaclone.utils.GlideApp
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity(2) {

    private lateinit var mCameraPictureTaker: CameraPictureTaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        
        mCameraPictureTaker = CameraPictureTaker(this)
        mCameraPictureTaker.takeCameraPicture()

        back_image.setOnClickListener{
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCameraPictureTaker.TAKE_PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            GlideApp.with(this).load(mCameraPictureTaker.mImageUri).centerCrop().into(post_image)
        }
    }
}
