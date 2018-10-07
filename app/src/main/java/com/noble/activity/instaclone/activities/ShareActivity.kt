package com.noble.activity.instaclone.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.firebase.database.ServerValue
import com.noble.activity.instaclone.R
import com.noble.activity.instaclone.models.User
import com.noble.activity.instaclone.utils.CameraPictureTaker
import com.noble.activity.instaclone.utils.FirebaseHelper
import com.noble.activity.instaclone.utils.GlideApp
import com.noble.activity.instaclone.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_share.*
import java.sql.Timestamp
import java.util.*

class ShareActivity : BaseActivity(2) {

    private lateinit var mCameraPictureTaker: CameraPictureTaker
    private lateinit var mFirebaseHelper: FirebaseHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        mFirebaseHelper = FirebaseHelper(this)

        mCameraPictureTaker = CameraPictureTaker(this)
        mCameraPictureTaker.takeCameraPicture()

        back_image.setOnClickListener{ finish() }
        share_text.setOnClickListener{ share() }

        mFirebaseHelper.currentUserReference().addValueEventListener(ValueEventListenerAdapter{
           mUser = it.getValue(User::class.java)!!
        })

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
                            val imageDownloadUrl = it.result.toString()
                            mFirebaseHelper.mDatabase.child("images").child(uid).push()
                                    .setValue(imageDownloadUrl).addOnCompleteListener{
                                        if (it.isSuccessful) {
                                            mFirebaseHelper.mDatabase.child("feed-posts").child(uid)
                                                    .push()
                                                    .setValue(mkFeedPost(uid, imageDownloadUrl))
                                                    .addOnCompleteListener{
                                                        if (it.isSuccessful) {
                                                            startActivity(Intent(this,
                                                                    ProfileActivity::class.java))
                                                            finish()
                                                        }
                                                    }

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

    fun mkFeedPost(uid: String, imageDownloadUrl: String): FeedPost {
        return FeedPost(
                uid = uid,
                username = mUser.username,
                image = imageDownloadUrl,
                caption = capture_input.text.toString(),
                photo = mUser.photo
        )
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

data class FeedPost(val uid: String = "", val username: String = "", val photo: String? = null,
                    val image: String = "",
                    val likesCount: Int = 0, val commentsCount: Int = 0, val caption: String = "",
                    val comments: List<Comment> = emptyList(),
                    val timestamp: Any = ServerValue.TIMESTAMP) {
    fun timestampDate(): Date = Date(timestamp as Long)
}

data class Comment(val uid: String, val username: String, val text: String)
