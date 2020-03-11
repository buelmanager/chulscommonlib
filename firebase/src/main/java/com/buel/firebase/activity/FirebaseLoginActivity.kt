package com.buel.firebase.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.buel.bcom.setCenterCropGlide
import com.buel.firebase.R
import com.buel.firebase.login.FirebaseLogin
import com.buel.firebase.login.FirebaseLogin.RC_SIGN_IN
import com.buel.firebase.login.FirebaseLogin.revokeAccess
import com.buel.sknmethodist.manager.firebase.AuthManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class FirebaseLoginActivity : FirebaseBaseActivity(),
    View.OnClickListener {

    private val TAG: String = "FirebaseLoginActivity"
    private var auth: FirebaseAuth? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private var isInvokeLogOut = false

    private var m_login_img_url: String = ""
    private var m_table_name: String = ""
    private var default_web_client_id: String = ""
    private var intentlogOut: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //google client id : null is return
        default_web_client_id = intent.getStringExtra("default_web_client_id") ?: return
        intentlogOut = intent.extras?.getBoolean("log_out") ?: false
        m_table_name = intent.getStringExtra("table_name") ?: return
        m_login_img_url = intent.getStringExtra("log_img") ?: ""

        Log.e("buel" , "m_login_img_url : " + m_login_img_url)
        if(m_login_img_url.isNullOrBlank()){
            setCenterCropGlide(main_img,R.drawable.defaultimg)
        }else{
            setCenterCropGlide(main_img,m_login_img_url)
        }

        sign_in.setOnClickListener(this)
        initGoogleLogin()
    }

    private fun initGoogleLogin() {

        showProgressDialog()
        /**
         * 로그인 세팅
         */
        auth = FirebaseAuth.getInstance()

        FirebaseLogin.setGoogleLogin(
            m_table_name,
            auth!!,
            this,
            name_txt.text.toString(),
            phone_txt.text.toString(),
            default_web_client_id,
            intentlogOut,
            OnSuccessListener {
                if (it) updateUI(auth?.currentUser)
                else updateUI(null)
            })
    }

    /**
     * 로그인 onActivityResult
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                /**
                 * 로그인 onActivityResult firebaseAuthWithGoogle
                 */
                FirebaseLogin.firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                updateUI(null)
            }
        } else {

            var intent = Intent()
            intent.putExtra("login", "fail")
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            AuthManager.setUser(auth!!)
            if (isInvokeLogOut) {
                revokeAccess()
            } else {
                hideProgressDialog()
                var intent = Intent()
                intent.putExtra("login", "complete")
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onClick(v: View) {
        isInvokeLogOut = false

        when (v.id) {
            R.id.sign_in -> {

                showProgressDialog()
                FirebaseLogin.signIn()
            }
        }
    }
}
