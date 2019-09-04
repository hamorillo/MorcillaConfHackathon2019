package com.bankmorcillaconf.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bankmorcillaconf.app.model.User
import com.bankmorcillaconf.app.repository.UserRepository
import com.bankmorcillaconf.app.repository.UserRepository.Companion.staticUser
import com.bankmorcillaconf.app.util.ResultListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.login_activity.*
import java.util.*

class LoginActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LoginActivity"
    }

    private lateinit var firebaseAuth: FirebaseAuth
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        bindView()
        initializeFirebaseAuth()
    }

    override fun onResume() {
        super.onResume()
        alreadySignIn()
    }

    private fun alreadySignIn() {
        firebaseAuth.currentUser?.email?.let {
            updateUserWithTokenPush(it)
        }
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
                    updateUserWithTokenPush(mail)
                } else {
                    Log.w("LOGIN", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this@LoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun updateUser(mail: String, tokenPush: String?) {
        userRepository.createOrUpdateUserMe(User(mail, Date().time, tokenPush), ResultListener(
            onSuccess = {
                staticUser = it
                openHomeActivity()
            },
            onError = {
                Toast.makeText(
                    this@LoginActivity, "User creation failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        ))
    }

    private fun updateUserWithTokenPush(mail: String) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                }

                val token = task.result?.token
                updateUser(mail, token)
                removePreviousUsesOfToken(token, mail)
            }
    }

    private fun removePreviousUsesOfToken(token: String?, newUserMail: String) {
        if (token != null) {
            userRepository.getAllUsers(ResultListener(
                onSuccess = { users ->
                    val userWithSameToken = users.firstOrNull { it.tokenPush == token && it.email != newUserMail }
                    if (userWithSameToken != null) {
                        userRepository.createOrUpdateUserMe(
                            userWithSameToken.copy(tokenPush = null), ResultListener(
                                onSuccess = {
                                    Log.i(TAG, "Removed token from user: ${userWithSameToken.email}")
                                },
                                onError = {
                                    Log.e(TAG, "Error removing token push from user: ${userWithSameToken.email} ")
                                })
                        )
                    }
                },
                onError = {
                    Log.e(TAG, "error getting users")
                }
            ))
        }
    }

    private fun openHomeActivity() {
        startActivity(HomeActivity.newIntent(this))
    }
}
