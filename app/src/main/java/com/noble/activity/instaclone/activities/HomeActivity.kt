package com.noble.activity.instaclone.activities

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.noble.activity.instaclone.R
import com.noble.activity.instaclone.utils.FirebaseHelper
import com.noble.activity.instaclone.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(0) {
    private val TAG = "HomeActivity"

    private lateinit var mFirebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation()

        mFirebaseHelper = FirebaseHelper(this)

        sign_out_text.setOnClickListener{
            mFirebaseHelper.mAuth.signOut()
        }
        mFirebaseHelper.mAuth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        mFirebaseHelper.mDatabase.child("feed-posts").child(mFirebaseHelper.mAuth.currentUser!!.uid)
                .addValueEventListener(ValueEventListenerAdapter{
                    val posts = it.children.map{ it.getValue(FeedPost::class.java)!!}
                    showToast(posts.size.toString())
                })

    }

    override fun onStart() {
        super.onStart()
        if (mFirebaseHelper.mAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
