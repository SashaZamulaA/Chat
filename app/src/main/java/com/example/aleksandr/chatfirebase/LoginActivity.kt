package com.example.aleksandr.chatfirebase

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        textView_back_to_registration.setOnClickListener {
            finish()
        }

        login_button_login.setOnClickListener {
            performLogin()
        }
    }

     private fun performLogin(){
            val email = email_edittext_login.text.toString()
            val password = password_edittext_login.text.toString()
            Log.d("Login", "Attempt login with email/pw: $email/***")

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill out email/pw.", Toast.LENGTH_SHORT).show()

            }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{

                    if (!it.isSuccessful) {
                        return@addOnCompleteListener
                    }

                        val intent = Intent(this@LoginActivity, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

