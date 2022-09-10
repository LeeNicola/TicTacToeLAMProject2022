package com.example.prova3

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {
    private var email: EditText? = null
    private  var username:EditText? = null
    private  var password:EditText? = null
    private var register: Button? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        register = findViewById(R.id.register)

        email = findViewById(R.id.email)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)

        progressBar = findViewById(R.id.progressBar)

        register?.setOnClickListener {
            userRegistration()
        }
    }

    private fun userRegistration() {
        val emailString = email!!.text.toString().trim { it <= ' ' }
        val usernameString = username!!.text.toString().trim { it <= ' ' }
        val passwordString = password!!.text.toString().trim { it <= ' ' }
        if (emailString.isEmpty()) {
            email!!.error = "Email required"
            email!!.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email!!.error = "Email not valid"
            email!!.requestFocus()
            return
        }
        if (usernameString.isEmpty()) {
            username!!.error = "Username required"
            username!!.requestFocus()
            return
        }
        if (passwordString.isEmpty()) {
            password!!.error = "Password required"
            password!!.requestFocus()
            return
        }
        if (passwordString.length < 6) {
            password!!.error = "Password should be at least 6 characters"
            password!!.requestFocus()
            return
        }
        progressBar!!.visibility = View.VISIBLE
        firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(emailString, usernameString, wins = 0)
                    reference.child("Users") .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user).addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "User has been registered",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressBar!!.visibility = View.GONE
                            } else {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Failed to register",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressBar!!.visibility = View.GONE
                            }
                        }
                } else {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Failed to register",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    progressBar!!.visibility = View.GONE
                }
            }
    }
}