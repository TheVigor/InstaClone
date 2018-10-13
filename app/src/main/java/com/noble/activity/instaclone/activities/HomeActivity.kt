package com.noble.activity.instaclone.activities

import android.content.Intent
import android.os.Bundle
import android.os.RecoverySystem
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.noble.activity.instaclone.R
import com.noble.activity.instaclone.utils.FirebaseHelper
import com.noble.activity.instaclone.utils.GlideApp
import com.noble.activity.instaclone.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.feed_item.view.*
import java.text.FieldPosition

class HomeActivity : BaseActivity(0) {
    private val TAG = "HomeActivity"

    private lateinit var mFirebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation()

        mFirebaseHelper = FirebaseHelper(this)

        mFirebaseHelper.mAuth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mFirebaseHelper.mAuth.currentUser

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            mFirebaseHelper.mDatabase.child("feed-posts").child(currentUser.uid)
                    .addValueEventListener(ValueEventListenerAdapter {
                        val posts = it.children.map { it.getValue(FeedPost::class.java)!! }
                        showToast(posts.size.toString())
                        feed_recycler.adapter = FeedAdapter(posts)
                        feed_recycler.layoutManager = LinearLayoutManager(this)
                    })
        }
    }
}

class FeedAdapter(private val posts: List<FeedPost>) :RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = posts.size
    override fun onBindViewHolder(holder: FeedAdapter.ViewHolder, position: Int) {
        holder.view.post_image.loadImage(posts[position].image)
    }

    private fun ImageView.loadImage(image: String) {
        GlideApp.with(this).load(image).centerCrop().into(this)
    }
}
