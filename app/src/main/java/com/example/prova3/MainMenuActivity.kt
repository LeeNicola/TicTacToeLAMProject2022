package com.example.prova3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

var nWins : Int = 0
var playerUsername : String? = null
var singleUser = false

class MainMenuActivity : AppCompatActivity() {

    private lateinit var wins: TextView
    private lateinit var headTitle: TextView
    private lateinit var emailText: TextView
    private lateinit var welcome: TextView
    private lateinit var singlePlayerBtn : Button
    private lateinit var rules: Button
    private lateinit var exitGame: Button
    private lateinit var logout: Button
    private lateinit var onlineBtn : Button
    private lateinit var offlineBtn : Button
    private lateinit var welcomeString : String
    private lateinit var winsString: String
    private lateinit var emailString: String
    private var user = Firebase.auth.currentUser
    private var reference = Firebase.database.reference
    private var userID = user!!.uid
    private var firebaseAuth = Firebase.auth
    private val USERS = "Users"
    private val SEMICOLON = " : "
    private val SPACE = " "

    private val authStateListener = AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            val intent = Intent(this@MainMenuActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        wins = findViewById(R.id.wins)
        headTitle = findViewById(R.id.headTitle)
        welcome = findViewById(R.id.welcome)
        singlePlayerBtn = findViewById(R.id.singlePlayerBtn)
        emailText = findViewById(R.id.emailText)
        rules = findViewById(R.id.rules)
        exitGame = findViewById(R.id.exitGame)
        logout = findViewById(R.id.logout)
        onlineBtn = findViewById(R.id.idBtnOnline)
        offlineBtn = findViewById(R.id.idBtnOffline)
        welcomeString = getString(R.string.welcome)
        winsString = getString(R.string.wins)
        emailString = getString(R.string.email)

        onlineBtn.setOnClickListener(){
            startActivity(Intent(this, OnlineCodeActivity::class.java))
        }

        offlineBtn.setOnClickListener(){
            singleUser = false
            startActivity(Intent(this, OfflineGameActivity::class.java))
        }

        exitGame.setOnClickListener{
            finish()
        }

        singlePlayerBtn.setOnClickListener{
            singleUser = true
            startActivity(Intent(this@MainMenuActivity, OfflineGameActivity::class.java))
        }

        logout.setOnClickListener{
            Firebase.auth.signOut()
            startActivity(Intent(this@MainMenuActivity, LoginActivity::class.java))
        }

        rules.setOnClickListener{
            startActivity(Intent(this@MainMenuActivity, RulesActivity::class.java))
        }


        reference.child(USERS).child(userID).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(User::class.java)

                user?.let {
                    val email = profile!!.email
                    val username = profile.username
                    playerUsername = username
                    nWins = profile.wins
                    welcome.text = welcomeString.plus(SPACE).plus(username)
                    wins.text = winsString.plus(SPACE).plus(nWins)
                    emailText.text = emailString.plus(SEMICOLON).plus(email)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainMenuActivity, getString(R.string.error_get_data), Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}