package com.noble.activity.instaclone.activities

import android.content.Intent
import android.os.Bundle
import com.noble.activity.instaclone.R
import com.noble.activity.instaclone.models.User
import com.noble.activity.instaclone.utils.FirebaseHelper
import com.noble.activity.instaclone.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(4) {

    private lateinit var mFirebaseHelper: FirebaseHelper

    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupBottomNavigation()

        edit_profile_btn.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        mFirebaseHelper = FirebaseHelper(this)
        mFirebaseHelper.currentUserReference().addValueEventListener(ValueEventListenerAdapter{
            mUser = it.getValue(User::class.java)!!
            profile_image.loadUserPhoto(mUser.photo)
            username_text.text = mUser.username
        })
    }
}
