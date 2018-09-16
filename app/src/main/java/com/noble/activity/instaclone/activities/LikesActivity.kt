package com.noble.activity.instaclone.activities

import android.os.Bundle
import com.noble.activity.instaclone.R

class LikesActivity : BaseActivity(3) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupBottomNavigation()
    }
}
