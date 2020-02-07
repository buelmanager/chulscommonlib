package com.buel.sknmethodist.manager.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


object AuthManager {
    val TAG:String = "AUTH_MANAGER"
    val IS_DEBUG:Boolean = false
    private val IS_DEBUG_USER_IMG : String = "https://lh3.googleusercontent.com/-LnTXDHrQQyU/AAAAAAAAAAI/AAAAAAAAAW0/tZbdVUjNr_A/s96-c/photo.jpg"
    private val IS_DEBUG_USER_EMAIL : String = "buelmanager@gmail.com"

    var firebaseUser: FirebaseUser? = null
    var type = ""
    var status = ""
    var level = USER_LEVEL.LOW_LEVEL
    var firebaseAuth: FirebaseAuth? = null

    fun setUser(auth: FirebaseAuth) {
        Log.e(TAG , "USER SETTING !! ")
        Log.e(TAG , auth.currentUser.toString())
        firebaseAuth = auth
        firebaseUser = auth.currentUser
    }

    fun getEmail():String
    {
        return if(!IS_DEBUG) firebaseUser!!.email!! else IS_DEBUG_USER_EMAIL
    }

    fun getPhotoUri(): Uri
    {
        return if(!IS_DEBUG) firebaseUser!!.photoUrl!! else Uri.parse(IS_DEBUG_USER_IMG)
    }

    enum class STATUS {
        HAPPY,
        SAD,
        ANGRY,
    }

    enum class USER_LEVEL {
        SYSTEM_LEVEL,
        HIGH_LEVEL,
        LOW_LEVEL
    }
}