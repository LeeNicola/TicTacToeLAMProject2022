package com.example.prova3

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private var register: Button? = null
    private var email: EditText? = null
    private var password:EditText? = null
    private var login: Button? = null
    private var progressBar: ProgressBar? = null
    private var firebaseAuth = Firebase.auth

    val authStateListener = AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            val intent = Intent(this@LoginActivity, MainMenu::class.java)
            startActivity(intent)
            finish()
        }
    }

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        login = findViewById(R.id.login)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressBar)

        register = findViewById(R.id.register)
        register?.setOnClickListener {
                registrationPage()
        }

        login?.setOnClickListener {
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
        val emailString = email!!.text.toString()
        val passwordString = password!!.text.toString()
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
        mAuth.signInWithEmailAndPassword(emailString, passwordString)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Success", Toast.LENGTH_SHORT).show()
                    mainMenuPage()
                } else {
                    Toast.makeText(this@LoginActivity, "Failed to login", Toast.LENGTH_SHORT).show()
                    progressBar!!.visibility = View.GONE
                }
            }
    }

    private fun registrationPage() {
        val toRegistration = Intent(this, RegistrationActivity::class.java)
        startActivity(toRegistration)
    }

    private fun mainMenuPage(){
        val toMainMenu = Intent(this, MainMenu::class.java)
        startActivity(toMainMenu)
    }
}