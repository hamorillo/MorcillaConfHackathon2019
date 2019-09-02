package com.bankmorcillaconf.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        bindView()
        initializeFirebaseAuth()
    }

    private fun bindView() {
        loginButton.setOnClickListener {
            loginWithMailAndPassword(mailEditText.text.toString(), passwordEditText.text.toString())
        }
    }

    private fun initializeFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun loginWithMailAndPassword(mail: String, pass: String) {
        firebaseAuth.signInWithEmailAndPassword(mail, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("LOGIN", "signInWithEmail:success")
                    openHomeActivity()
                } else {
                    Log.w("LOGIN", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this@LoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun openHomeActivity() {
        startActivity(HomeActivity.newIntent(this))
    }
}
