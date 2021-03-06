package com.buel.firebase.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.os.PersistableBundle
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import com.buel.firebase.R

open class FirebaseBaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState, persistentState)
    }
    @VisibleForTesting
    val progressDialog by lazy {
        ProgressDialog(this)
    }
    fun showProgressDialog() {
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }
    fun toast(str: String) {
        Toast.makeText(
            this,
            str,
            Toast.LENGTH_SHORT
        ).show()
    }
}