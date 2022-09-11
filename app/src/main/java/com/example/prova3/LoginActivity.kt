package com.example.prova3

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var register : Button
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var login : Button
    private lateinit var progressBar : ProgressBar
    private lateinit var headTitle : TextView
    private lateinit var exitGame: Button
    private var firebaseAuth = Firebase.auth

    private val authStateListener = AuthStateListener {
        val firebaseUser = Firebase.auth.currentUser
        if (firebaseUser != null) {
            val intent = Intent(this@LoginActivity, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById(R.id.login)
        headTitle = findViewById(R.id.headTitle)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressBar)
        register = findViewById(R.id.register)
        exitGame = findViewById(R.id.exitGame)

        exitGame.setOnClickListener{
            finish()
        }

        register.setOnClickListener {
                registrationPage()
        }

        login.setOnClickListener {
            userLogin()
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }


    private fun userLogin() {
        val emailString = email.text.toString()
        val passwordString = password.text.toString()
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
        firebaseAuth.signInWithEmailAndPassword(emailString, passwordString)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, getString(R.string.success), Toast.LENGTH_SHORT).show()
                    toMenuPage()
                } else {
                    Toast.makeText(this@LoginActivity, getString(R.string.fail_log), Toast.LENGTH_SHORT).show()
                    itemsVisibility(true)
                }
            }
    }

    private fun registrationPage() {
        val toRegistration = Intent(this, RegistrationActivity::class.java)
        startActivity(toRegistration)
        finish()
    }

    private fun toMenuPage(){
        val toMainMenuActivity = Intent(this, MainMenuActivity::class.java)
        startActivity(toMainMenuActivity)
        finish()
    }

    private fun itemsVisibility(boolean: Boolean){
        if (boolean){
            login.visibility = View.VISIBLE
            email.visibility = View.VISIBLE
            password.visibility = View.VISIBLE
            register.visibility = View.VISIBLE
            headTitle.visibility = View.VISIBLE
            exitGame.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        } else {
            login.visibility = View.GONE
            email.visibility = View.GONE
            password.visibility = View.GONE
            register.visibility = View.GONE
            headTitle.visibility = View.GONE
            exitGame.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }
}