package com.noble.activity.instaclone.activities


import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import com.noble.activity.instaclone.R
import com.noble.activity.instaclone.models.User
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_namepass.*


class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, NamePassFragment.Listener {
    private var mEmail: String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment())
                    .commit()
        }
    }

    override fun onNext(email: String) {
        if (email.isNotEmpty()) {
            mEmail= email
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, NamePassFragment())
                    .addToBackStack(null)
                    .commit()
        } else {
            showToast("Please enter email")
        }
    }

    override fun onRegister(fullName: String, password: String) {
        if (fullName.isNotEmpty() && password.isNotEmpty()) {
            val email = mEmail
            if (email != null) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener{
                            if (it.isSuccessful) {
                                val user = makeUser(fullName, email)
                                val reference = mDatabase.child("users").child(it.result.user.uid)
                                reference.setValue(user)
                                        .addOnCompleteListener{
                                            if (it.isSuccessful) {
                                                startHomeActivity()
                                            } else {
                                                showToast("Failed to create user profile")
                                            }
                                        }
                            } else {
                                showToast("Failed to create user")
                            }
                        }

            } else {
                showToast("Email is null")
                supportFragmentManager.popBackStack()
            }
        } else {
            showToast("Please enter full name and password")
        }
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun makeUser(fullName: String, email: String): User {
        val username = makeUsername(fullName)
        return User(name = fullName, username = username, email = email)
    }

    private fun makeUsername(fullName: String) =
        fullName.toLowerCase().replace(" ", ".")

}

class EmailFragment: Fragment(){
    private lateinit var mListener: Listener
    interface Listener {
        fun onNext(email: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        next_btn.setOnClickListener{
            val email = email_input.text.toString()
            mListener.onNext(email)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }
}

class NamePassFragment: Fragment(){
    private lateinit var mListener: Listener

    interface Listener {
        fun onRegister(fullName: String, password: String)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_namepass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        register_btn.setOnClickListener {
            val fullName = full_name_input.text.toString()
            val password = password_input.text.toString()
            mListener.onRegister(fullName, password)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }
}
