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

class LoginActivity : AppCompatActivity() {

    private var register: Button? = null
    private var email: EditText? = null
    private var password:EditText? = null
    private var signIn: Button? = null
    private var  progressBar: ProgressBar? = null

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        signIn = findViewById<View>(R.id.login) as Button

        email = findViewById<View>(R.id.email) as EditText
        password = findViewById<View>(R.id.password) as EditText
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar


        //progressBar = (progressBar) findViewById(R.id.progressBar);
        register = findViewById<View>(R.id.register) as Button
        register?.setOnClickListener(View.OnClickListener { view ->
            when (view.id) {
                R.id.register -> registrationPage()
                R.id.login -> userLogin()
            }
        })

    }

    private fun userLogin() {
        val emailString = email!!.text.toString().trim { it <= ' ' }
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
        mAuth!!.createUserWithEmailAndPassword(emailString, passwordString)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this@LoginActivity, MainMenu::class.java))
                } else {
                    Toast.makeText(this@LoginActivity, "Failed to login", Toast.LENGTH_SHORT).show()
                    progressBar!!.visibility = View.GONE
                }
            }
    }

    fun registrationPage() {
        val toRegistration = Intent(this, RegistrationActivity::class.java)
        startActivity(toRegistration)
    }
}