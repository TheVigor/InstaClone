package com.noble.activity.instaclone.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.noble.activity.instaclone.R
import com.noble.activity.instaclone.models.User
import com.noble.activity.instaclone.utils.CameraPictureTaker
import com.noble.activity.instaclone.utils.FirebaseHelper
import com.noble.activity.instaclone.utils.ValueEventListenerAdapter
import com.noble.activity.instaclone.views.PasswordDialog
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {

    private lateinit var mUser: User
    private lateinit var mPendingUser: User

    private lateinit var mFirebaseHelper: FirebaseHelper

    private lateinit var mCameraPictureTaker: CameraPictureTaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        back_image.setOnClickListener{
            finish()
        }

        save_image.setOnClickListener{
            updateProfile()
        }

        mCameraPictureTaker = CameraPictureTaker(this)

        change_photo_text.setOnClickListener { mCameraPictureTaker.takeCameraPicture() }

        mFirebaseHelper = FirebaseHelper(this)

        mFirebaseHelper.currentUserReference().addListenerForSingleValueEvent(
                ValueEventListenerAdapter {
                    mUser = it.getValue(User::class.java)!!

                    name_input.setText(mUser.name)
                    username_input.setText(mUser.username)
                    website_input.setText(mUser.website)
                    bio_input.setText(mUser.bio)
                    email_input.setText(mUser.email)
                    phone_input.setText(mUser.phone?.toString())
                    profile_image.loadUserPhoto(mUser.photo)
                })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCameraPictureTaker.TAKE_PICTURE_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {

            val uid = mFirebaseHelper.mAuth.currentUser!!.uid
            val ref = mFirebaseHelper.mStorage.child("users/$uid/photo")

            ref.putFile(mCameraPictureTaker.mImageUri!!).addOnCompleteListener{ it ->
                if(it.isSuccessful) {
                    ref.downloadUrl.addOnCompleteListener {
                        val photoUrl = it.result.toString()
                        mFirebaseHelper.updateUserPhoto(photoUrl) {
                            mUser = mUser.copy(photo = photoUrl)
                            profile_image.loadUserPhoto(mUser.photo)
                        }
                    }

                } else {
                    showToast(it.exception!!.message!!)
                }
            }
        }
    }

    private fun updateProfile() {

        mPendingUser = readInputs()
        val error = validate(mPendingUser)
        if (error == null) {
            if (mPendingUser.email == mUser.email) {
                updateUser(mPendingUser)
            } else {
                PasswordDialog().show(supportFragmentManager, "password_dialog")

            }
        } else {
            showToast(error)
        }
    }

    private fun readInputs(): User {
        return User(
                name = name_input.text.toString(),
                username = username_input.text.toString(),
                email = email_input.text.toString(),
                website = website_input.text.toStringOrNull(),
                bio = bio_input.text.toStringOrNull(),
                phone = phone_input.text.toString().toLongOrNull()
        )
    }

    private fun validate(user: User): String? =
        when {
            user.name.isEmpty() -> "Please enter name"
            user.username.isEmpty() -> "Please enter username"
            user.email.isEmpty() -> "Please enter email"
            else -> null

        }

    private fun updateUser(user: User) {
        val updatesMap = mutableMapOf<String, Any?>()

        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.username != mUser.username) updatesMap["username"] = user.username
        if (user.website != mUser.website) updatesMap["website"] = user.website
        if (user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone

        mFirebaseHelper.updateUser(updatesMap){
            showToast("Profile saved")
            finish()
        }

    }

    override fun onPasswordConfirm(password: String) {
        if (password.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mFirebaseHelper.reauthenticate(credential){
                mFirebaseHelper.updateEmail(mPendingUser.email){
                    updateUser(mPendingUser)
                }
            }
        } else {
            showToast("You should enter your password")
        }
    }

    private fun DatabaseReference.updateUserPhoto(uid: String, photoUrl: String,
                                                  onSuccess: () -> Unit) {
        child("users/$uid/photo").setValue(photoUrl).addOnCompleteListener{
            if (it.isSuccessful){
                onSuccess()
            }else{
                showToast(it.exception!!.message!!)
            }
        }
    }

    private fun FirebaseUser.updateEmail(email: String, onSuccess: () -> Unit) {
        updateEmail(email).addOnCompleteListener{
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }

    private fun FirebaseUser.reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
        reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }

    private fun DatabaseReference.updateUser(uid: String, updates: Map<String, Any?>, onSuccess: () -> Unit) {
        child("users").child(uid).updateChildren(updates)
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    showToast(it.exception!!.message!!)
                }
            }
    }
}

