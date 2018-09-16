package com.noble.activity.instaclone.activities

import android.os.Bundle
import com.noble.activity.instaclone.R

class ShareActivity : BaseActivity(2) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupBottomNavigation()
    }
}
