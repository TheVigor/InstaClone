package com.noble.activity.instaclone

import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : BaseActivity(0) {
    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation()

        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword("the.noble.activity@gmail.com", "pass123")
                .addOnCompleteListener{
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Signin completed", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Signin failed", Toast.LENGTH_LONG).show()
                    }
                }

    }
}
