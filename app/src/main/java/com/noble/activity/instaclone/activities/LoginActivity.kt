package com.noble.activity.instaclone.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.noble.activity.instaclone.R
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : AppCompatActivity(), KeyboardVisibilityEventListener, TextWatcher, View.OnClickListener {
    private val TAG = "LoginActivity"
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        KeyboardVisibilityEvent.setEventListener(this, this)
        login_btn.isEnabled = false

        email_input.addTextChangedListener(this)
        password_input.addTextChangedListener(this)

        login_btn.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen) {
            scroll_view.scrollTo(0, scroll_view.bottom)
            create_account_text.visibility = View.GONE
        } else {
            scroll_view.scrollTo(0,scroll_view.top)
            create_account_text.visibility = View.VISIBLE
        }

    }

    override fun afterTextChanged(s: Editable?) {
        login_btn.isEnabled =
                validate(email_input.text.toString(), password_input.text.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onClick(v: View) {
        val email = email_input.text.toString()
        val password = password_input.text.toString()

        if (validate(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                if (it.isSuccessful) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
            }
        } else {
            showToast("Please enter email and password")
        }
    }

    private fun validate(email: String, password: String) =
            email.isNotEmpty() && password.isNotEmpty()
}
