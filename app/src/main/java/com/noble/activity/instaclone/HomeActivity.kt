package com.noble.activity.instaclone

import android.os.Bundle

class HomeActivity : BaseActivity(0) {
    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation()

    }
}
