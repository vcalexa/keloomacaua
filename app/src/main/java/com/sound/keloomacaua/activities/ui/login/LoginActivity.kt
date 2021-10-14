package com.sound.keloomacaua.activities.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.sound.keloomacaua.R
import com.sound.keloomacaua.activities.ui.game.LobbyActivity

class LoginActivity : AppCompatActivity() {
    lateinit var registerButton: Button
    lateinit var usernameEditText: EditText
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val auth = FirebaseAuth.getInstance()
        registerButton = findViewById(R.id.register)
        usernameEditText = findViewById(R.id.username)
        registerButton.setOnClickListener {
            registerButton.isEnabled = false
            //register
            val username = usernameEditText.text.toString()
            auth.signInAnonymously().addOnCompleteListener { authResultTask: Task<AuthResult> ->
                if (authResultTask.isSuccessful) {
                    val profileChange = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    val loggedInUser = authResultTask.result?.user
                        ?: throw IllegalStateException("authResult is successful but user is null.")
                    loggedInUser.updateProfile(profileChange)
                        .addOnCompleteListener { task: Task<Void?> ->
                            if (task.isSuccessful) {
                                startGameListActivity()
                            } else {
                                showLoginFailed()
                            }
                        }
                } else {
                    showLoginFailed()
                }
            }
        }

        //autologin if user is already signed in
        val user = auth.currentUser
        if (user != null) {
            startGameListActivity()
        }
    }

    private fun showLoginFailed() {
        Toast.makeText(applicationContext, R.string.login_failed, Toast.LENGTH_SHORT).show()
        registerButton.isEnabled = true
    }

    private fun startGameListActivity() {
        val intent = Intent(applicationContext, LobbyActivity::class.java)
        startActivity(intent)
        registerButton.isEnabled = true
    }
}