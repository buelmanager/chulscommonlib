package com.buel.firebase.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buel.firebase.model.UserModel
import com.buel.sknmethodist.manager.firebase.AuthManager
import com.buel.sknmethodist.manager.firebase.FireStoreManager
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
import java.util.*

/**
 * google login and join
 * 사용자가 있는경우는 로그인 없는경우는 생성을 한다.
 */
object FirebaseLogin : GoogleApiClient.OnConnectionFailedListener{
    private val TAG: String = "FirebaseLoginActivity"
    private var mAuth: FirebaseAuth? = null
    private var mContext: Context? = null
    private var firebaseAuth: FirebaseAuth? = null

    private lateinit var googleSignInClient: GoogleSignInClient
    private var isInvokeLogOut = false
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    const val RC_SIGN_IN = 9001
    private var mOnLoginComplete:OnSuccessListener<Boolean>? = null
    /**
     * setting google login
     * onFirestoreComplete : login true or false
     */
    fun setGoogleLogin(
        auth: FirebaseAuth,
        context: Context,
        default_web_client_id: String,
        onFirestoreComplete: OnSuccessListener<Boolean>
    ) {
        Log.e(TAG, "setGoogleLogin")
        mAuth = auth
        mContext = context
        mOnLoginComplete = onFirestoreComplete

        //google sign
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(default_web_client_id)
            .requestEmail()
            .build()
        firebaseAuth = FirebaseAuth.getInstance()
        //google client
        googleSignInClient = GoogleSignIn.getClient(context, gso)

        //  로그인 인터페이스 리스너
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth: FirebaseAuth ->

            Log.e(TAG, "firebaseAuth.currentUser : " + firebaseAuth.currentUser)

            //로그인 체크
            if (firebaseAuth.currentUser != null) {
                Log.e(TAG, "authStateListener login")
                checkedUserData()
            }
            //로그인 실패
            else {
                Log.e(TAG, "authStateListener logout")
                signOut()
            }
        }
        firebaseAuth!!.addAuthStateListener(authStateListener!!)
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        Log.e(TAG, "firebaseAuthWithGoogle")
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
        Log.e(TAG, "checkedUserDataAndUpdate")
        val userModels = ArrayList<UserModel>()
        FireStoreManager.read(FireStoreManager.USER, OnSuccessListener {
            val docShots: List<DocumentSnapshot> = it.documents
            var isExist = false

            for (doc: DocumentSnapshot in docShots) {
                val usermodel = doc.toObject(UserModel::class.java)
                userModels.add(usermodel!!)
                isExist = checkUserDataAndLogin(usermodel, mAuth!!.currentUser!!.uid)
                if(isExist)return@OnSuccessListener
            }

            if(!isExist)setUserDataOnFireBase()
        })
    }

    //check user at server
    private fun checkedUserData() {
        Log.e(TAG, "checkedUserData")
        val userModels = ArrayList<UserModel>()
        FireStoreManager.read(FireStoreManager.USER, OnSuccessListener {
            val docShots: List<DocumentSnapshot> = it.documents
            var isExist = false
            if (docShots.size == 0) {
                signOut()
                return@OnSuccessListener
            }

            for (doc: DocumentSnapshot in docShots) {
                val usermodel = doc.toObject(UserModel::class.java)
                userModels.add(usermodel!!)
                isExist = checkUserDataAndLogin(usermodel, mAuth!!.currentUser!!.uid)
                completeLogin()
                if(isExist)return@OnSuccessListener
            }
        })
    }

    fun checkUserDataAndLogin(userModel: UserModel, uid: String?):Boolean {
        Log.e(TAG, "checkUserDataAndLogin")
        userModel.let {
            if (uid == userModel.uid) {
                /*val tokenMap = HashMap<String, Any>()
                tokenMap["pushToken"] = AuthManager.token
                tokenMap["uid"] = uid!!
                FireStoreManager.modify(FireStoreManager.USER, tokenMap, OnSuccessListener {
                    mOnLoginComplete?.onSuccess(true)
                })*/
                return true
            }
            return false
        }
    }

    private fun setUserDataOnFireBase() {
        Log.e(TAG, "setUserDataOnFireBase")
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
                    Toast.makeText(mContext, "data upload..", Toast.LENGTH_SHORT)
                        .show()
                    completeLogin()
                }
            })
    }

    /**
     * startActivityForResult 호출함
     */
    fun signIn() {
        Log.e(TAG, "signIn")
        val signInIntent = googleSignInClient.signInIntent
        (mContext as AppCompatActivity).startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signOut(onFirestoreComplete : OnSuccessListener<Boolean>? = null) {
        Log.e(TAG, "signOut")
        // Firebase sign out
        mAuth?.signOut()
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener {
            onFirestoreComplete?.onSuccess(true)
        }
        firebaseAuth!!.removeAuthStateListener(authStateListener!!)
    }

    fun revokeAccess(onFirestoreComplete: OnSuccessListener<Boolean>? = null) {
        Log.e(TAG, "revokeAccess")
        // Firebase sign out
        mAuth?.signOut()
        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener {
            onFirestoreComplete?.onSuccess(true)
        }
        firebaseAuth!!.removeAuthStateListener(authStateListener!!)
    }

    private fun failLogin(){

    }

    private fun completeLogin(){
        AuthManager.setUser(mAuth!!)
        mOnLoginComplete?.onSuccess(true)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}