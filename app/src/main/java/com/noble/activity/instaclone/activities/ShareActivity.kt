package com.noble.activity.instaclone.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.noble.activity.instaclone.R
import com.noble.activity.instaclone.utils.CameraPictureTaker
import com.noble.activity.instaclone.utils.FirebaseHelper
import com.noble.activity.instaclone.utils.GlideApp
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity(2) {

    private lateinit var mCameraPictureTaker: CameraPictureTaker
    private lateinit var mFirebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        mFirebaseHelper = FirebaseHelper(this)

        mCameraPictureTaker = CameraPictureTaker(this)
        mCameraPictureTaker.takeCameraPicture()

        back_image.setOnClickListener{ finish() }
        share_text.setOnClickListener{ share() }

    }

    private fun share() {
        val imageUri = mCameraPictureTaker.mImageUri
        if (imageUri != null) {
            val uid = mFirebaseHelper.mAuth.currentUser!!.uid
            var ref = mFirebaseHelper.mStorage.child("users").child(uid).child("images")
                    .child(imageUri.lastPathSegment)

            ref.putFile(imageUri).addOnCompleteListener{
                    if (it.isSuccessful) {
                        ref.downloadUrl.addOnCompleteListener {
                            mFirebaseHelper.mDatabase.child("images").child(uid).push()
                                    .setValue(it.result.toString()).addOnCompleteListener{
                                        if (it.isSuccessful) {
                                            startActivity(Intent(this,
                                                    ProfileActivity::class.java))
                                            finish()
                                        } else {
                                            showToast(it.exception!!.message!!)
                                        }
                                    }

                        }
                    } else {
                        showToast(it.exception!!.message!!)
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCameraPictureTaker.TAKE_PICTURE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                GlideApp.with(this).load(mCameraPictureTaker.mImageUri).centerCrop().into(post_image)
            } else {
                finish()
            }
        }
    }
}
