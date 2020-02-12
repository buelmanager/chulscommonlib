package com.buel.firebase.login

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.buel.firebase.FireStoreManager
import com.buel.firebase.model.UserModel
import com.buel.sknmethodist.manager.firebase.AuthManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.iid.FirebaseInstanceId
import com.orhanobut.logger.log
import java.util.*

/**
 * google login and join
 * 사용자가 있는경우는 로그인 없는경우는 생성을 한다.
 */
object FirebaseLogin : GoogleApiClient.OnConnectionFailedListener {
    private val TAG: String = "FirebaseLoginActivity"
    private var mAuth: FirebaseAuth? = null
    private var mContext: Context? = null
    private var firebaseAuth: FirebaseAuth? = null

    private lateinit var googleSignInClient: GoogleSignInClient
    private var isInvokeLogOut = false
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    const val RC_SIGN_IN = 9001
    private var mOnLoginComplete: OnSuccessListener<Boolean>? = null
    /**
     * setting google login
     * onFirestoreComplete : login true or false
     */
    fun setGoogleLogin(
        tableName: String,
        auth: FirebaseAuth,
        context: Context,
        default_web_client_id: String,
        intentlogOut: Boolean,
        onFirestoreComplete: OnSuccessListener<Boolean>
    ) {
        log.i(TAG, "setGoogleLogin")
        mAuth = auth
        mContext = context
        mOnLoginComplete = onFirestoreComplete
        FireStoreManager.FIREBASE_TABLE_NAME = tableName

        //google sign
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(default_web_client_id)
            .requestEmail()
            .build()

        firebaseAuth = FirebaseAuth.getInstance()

        //google client
        googleSignInClient = GoogleSignIn.getClient(context, gso)

        if (intentlogOut) {
            signOut()
            mOnLoginComplete?.onSuccess(false)
            return
        }

        //  로그인 인터페이스 리스너
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth: FirebaseAuth ->
            //log.e( "firebaseAuth.currentUser : " + firebaseAuth.currentUser )
            //로그인 체크
            if (firebaseAuth.currentUser != null) {
                log.e(TAG, "authStateListener login")
                completeLogin()
            }
            //로그인 실패
            else {
                log.e(TAG, "authStateListener logout")
                signOut()
                mOnLoginComplete?.onSuccess(false)
            }
        }

        firebaseAuth!!.addAuthStateListener(authStateListener!!)
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        AuthManager.setUser(mAuth!!)
        AuthManager.token = acct.idToken!!

        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    checkedUserDataAndUpdate()
                } else {
                    failLogin()
                }
            }
    }

    //check user at server
    private fun checkedUserDataAndUpdate() {
        val userModels = ArrayList<UserModel>()
        FireStoreManager.read(FireStoreManager.USER, OnSuccessListener {
            val docShots: List<DocumentSnapshot> = it.documents
            var isExist = false

            for (doc: DocumentSnapshot in docShots) {
                val usermodel = doc.toObject(UserModel::class.java)
                userModels.add(usermodel!!)
                isExist = checkUserDataAndLogin(usermodel, mAuth!!.currentUser!!.uid)
                if (isExist) {
                    mOnLoginComplete?.onSuccess(true)
                    return@OnSuccessListener
                }
            }

            if (!isExist) setUserDataOnFireBase()
        })
    }

    fun checkUserDataAndLogin(userModel: UserModel, uid: String?): Boolean {
        userModel.let {
            return uid == userModel.uid
        }
    }

    private fun setUserDataOnFireBase() {

        val userModel = UserModel()
        if (mAuth == null) return

        val fUser = mAuth?.currentUser!!
        userModel.userName = fUser.displayName
        userModel.userEmail = fUser.email
        userModel.userTell = fUser.phoneNumber
        userModel.pushToken = FirebaseInstanceId.getInstance().token

        userModel.uid = fUser.uid
        userModel.permission = "no"
        userModel.userPhotoUri = fUser.photoUrl.toString()

        FireStoreManager.write(
            FireStoreManager.USER,
            userModel,
            OnSuccessListener { aBoolean ->
                if (aBoolean!!) {
                    log.i(TAG, "FireStoreManager write completeLogin")
                    completeLogin()
                }
            })
    }

    /**
     * startActivityForResult 호출함
     */
    fun signIn() {
        log.i(TAG, "signIn")
        val signInIntent = googleSignInClient.signInIntent
        (mContext as AppCompatActivity).startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signOut(onFirestoreComplete: OnSuccessListener<Boolean>? = null) {
        log.i(TAG, "signOut")
        // Firebase sign out
        mAuth?.signOut()
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener {
            onFirestoreComplete?.onSuccess(false)
        }

        if (authStateListener != null) {
            firebaseAuth!!.removeAuthStateListener(authStateListener!!)
        }
    }

    fun revokeAccess(onFirestoreComplete: OnSuccessListener<Boolean>? = null) {
        log.i(TAG, "revokeAccess")
        // Firebase sign out
        mAuth?.signOut()
        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener {
            onFirestoreComplete?.onSuccess(false)
        }
        if (authStateListener != null) {
            firebaseAuth!!.removeAuthStateListener(authStateListener!!)
        }
    }

    private fun failLogin() {

    }

    private fun completeLogin() {
        log.i(TAG, "completeLogin")
        AuthManager.setUser(mAuth!!)
        mOnLoginComplete?.onSuccess(true)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}