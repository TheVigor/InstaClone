package com.noble.activity.instaclone.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.noble.activity.instaclone.R
import com.noble.activity.instaclone.utils.FirebaseHelper
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity : AppCompatActivity() {
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        mFirebase = FirebaseHelper(this)

        sign_out_text.setOnClickListener { mFirebase.mAuth.signOut() }
        back_image.setOnClickListener { finish() }
    }
}
