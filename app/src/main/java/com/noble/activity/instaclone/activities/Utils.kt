package com.noble.activity.instaclone.activities

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ValueEventListenerAdapter(val handler: (DataSnapshot) -> Unit): ValueEventListener {
    override fun onCancelled(error: DatabaseError) {
    }

    override fun onDataChange(data: DataSnapshot) {
        handler(data)
    }
}

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}