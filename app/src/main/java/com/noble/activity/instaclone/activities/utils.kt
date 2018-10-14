package com.noble.activity.instaclone.activities

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.DataSnapshot

import com.noble.activity.instaclone.R
import com.noble.activity.instaclone.models.User
import com.noble.activity.instaclone.utils.GlideApp

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun coordinateButtonAndInputs(btn: Button, vararg inputs: EditText) {
    val watcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            btn.isEnabled = inputs.all {it.text.isNotEmpty()}
        }

    }
    inputs.forEach {it.addTextChangedListener(watcher)}
    btn.isEnabled = inputs.all {it.text.isNotEmpty()}

}

fun ImageView.loadUserPhoto(photoUrl: String?) =
    ifNotDestroyed {
        GlideApp.with(this).load(photoUrl).fallback(R.drawable.android_picture).into(this)
    }


fun Editable.toStringOrNull(): String? {
    var str = toString()
    return if (str.isEmpty()) null else str
}

fun ImageView.loadImage(image: String?) =
    ifNotDestroyed {
        GlideApp.with(this).load(image).centerCrop().into(this)
    }


private fun View.ifNotDestroyed(block: () -> Unit) {
    if (!(context as Activity).isDestroyed) {
        block()
    }
}

fun <T> task(block: (TaskCompletionSource<T>) -> Unit): Task<T> {
    val taskSource = TaskCompletionSource<T>()
    block(taskSource)
    return taskSource.task
}

fun DataSnapshot.asUser(): User? = getValue(User::class.java)?.copy(uid = key!!)



