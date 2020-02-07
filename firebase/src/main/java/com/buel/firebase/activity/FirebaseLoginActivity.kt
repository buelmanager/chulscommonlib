package com.buel.firebase.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.buel.firebase.R
import com.buel.sknmethodist.manager.firebase.AuthManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class FirebaseLoginActivity : BasePageActivity(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private val TAG: String = "FirebaseLoginActivity"
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var googleSignInClient: GoogleSignInClient
    private var parent_view: View? = null
    private var isInvokeLogOut = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        parent_view = findViewById<View>(android.R.id.content)

        val default_web_client_id = intent.getStringExtra("default_web_client_id")

        if(default_web_client_id.isNullOrBlank()){
            Log.e(TAG,"isNullOrBlank")
            toast("default_web_client_id is isNullOrBlank")
            //finish()
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(default_web_client_id)
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        Log.e(TAG,"onCreate end")
        sign_in.setOnClickListener(this)
        sign_out.setOnClickListener(this)
        disconnectButton.setOnClickListener(this)

        if(intent.extras?.getBoolean("log_out") == true){
            signOut()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        AuthManager.setUser(auth)
        updateUI(currentUser)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e(TAG,"requestCode : " + requestCode)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            Log.e(TAG,"task : " + data.toString()!!)
            Log.e(TAG,"task : " + task.toString()!!)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                Log.e(TAG,"account : " + account!!)

                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                updateUI(null)
            }
        }else{
            finish()
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        //showProgressDialog()

        Log.e(TAG,"firebaseAuthWithGoogle")
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        //Snackbar.make(parent_view!!, "signInWithCredential:success", Snackbar.LENGTH_SHORT).show()
                        val user = auth.currentUser

                        Log.e(TAG,"isSuccessful")

                        updateUI(user)
                    } else {

                        Log.e(TAG,"isSuccessful not")

                        // If sign in fails, display a message to the user.
                        //Snackbar.make(parent_view!!, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                    //hideProgressDialog()
                }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //updateUI(null)
        }
    }

    private fun revokeAccess() {
        // Firebase sign out
        auth.signOut()

        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(this) {
            //updateUI(null)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        //hideProgressDialog()
        if (user != null) {
            AuthManager.setUser(auth)

            if (isInvokeLogOut) {
                toast("logout")
                revokeAccess()
            } else {
                auth.currentUser!!.displayName?.let { toast(it + " 님 로그인되었스니다.") }
                finish()
                //로그인 -->>
            }
        }else{

        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
       // Snackbar.make(parent_view!!, "Google Play Services error.", Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    fun toast(str:String){
        Toast.makeText(
            this,
            str,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onClick(v: View) {
        isInvokeLogOut = false

        when (v.id) {
            R.id.sign_in -> signIn()
            R.id.sign_out -> signOut()
            R.id.disconnectButton -> revokeAccess()
        }
    }
}
