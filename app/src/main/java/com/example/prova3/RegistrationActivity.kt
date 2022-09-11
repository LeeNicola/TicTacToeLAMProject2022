package com.example.prova3

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {
    private lateinit var email : EditText
    private lateinit var username :EditText
    private lateinit var password :EditText
    private lateinit var register : Button
    private lateinit var progressBar : ProgressBar
    private lateinit var backBtn : Button
    private var firebaseAuth = Firebase.auth
    private var reference = Firebase.database.reference
    private val USERS = "Users"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        register = findViewById(R.id.register)
        email = findViewById(R.id.email)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressBar)
        backBtn = findViewById(R.id.back)


        register.setOnClickListener {
            userRegistration()
        }

        backBtn.setOnClickListener(){
            finish()
        }
    }

    private fun userRegistration() {
        val emailString = email.text.toString().trim { it <= ' ' }
        val usernameString = username.text.toString().trim { it <= ' ' }
        val passwordString = password.text.toString().trim { it <= ' ' }
        if (emailString.isEmpty()) {
            email.error = getString(R.string.email_req)
            email.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.error = getString(R.string.inv_email)
            email.requestFocus()
            return
        }
        if (usernameString.isEmpty()) {
            username.error = getString(R.string.username_req)
            username.requestFocus()
            return
        }
        if (passwordString.isEmpty()) {
            password.error = getString(R.string.pwd_req)
            password.requestFocus()
            return
        }
        if (passwordString.length < 6) {
            password.error = getString(R.string.inv_pass)
            password.requestFocus()
            return
        }
        itemsVisibility(false)
        firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(emailString, usernameString, wins = 0)
                    reference.child(USERS) .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user).addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    getString(R.string.reg_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                itemsVisibility(true)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    getString(R.string.reg_fail),
                                    Toast.LENGTH_SHORT
                                ).show()
                                itemsVisibility(true)
                            }
                        }
                } else {
                    Toast.makeText(
                        this@RegistrationActivity,
                        getString(R.string.reg_fail),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    itemsVisibility(true)
                }
            }
    }

    private fun itemsVisibility(boolean: Boolean){
        if (boolean){
            email.visibility = View.VISIBLE
            password.visibility = View.VISIBLE
            register.visibility = View.VISIBLE
            username.visibility = View.VISIBLE
            backBtn.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        } else {
            email.visibility = View.GONE
            password.visibility = View.GONE
            register.visibility = View.GONE
            username.visibility = View.GONE
            backBtn.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }
}