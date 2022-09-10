package com.example.prova3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

var nWins : Int = 0
var userID = user!!.uid

class MainMenu : AppCompatActivity() {

    private lateinit var wins: TextView
    private lateinit var headTitle: TextView
    private lateinit var welcome: TextView
    private lateinit var startGame: Button
    private lateinit var rules: Button
    private lateinit var credits: Button
    private lateinit var exitGame: Button
    private lateinit var settings: ImageButton
    private lateinit var logout: Button
    private lateinit var welcomeString : String
    private lateinit var winsString: String


    private val authStateListener = AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            val intent = Intent(this@MainMenu, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        wins = findViewById(R.id.wins)
        headTitle = findViewById(R.id.headTitle)
        welcome = findViewById(R.id.welcome)
        startGame = findViewById(R.id.startGame)
        rules = findViewById(R.id.rules)
        credits = findViewById(R.id.credits)
        exitGame = findViewById(R.id.exitGame)
        settings = findViewById(R.id.settings)
        logout = findViewById(R.id.logout)
        welcomeString = getString(R.string.welcome)
        winsString = getString(R.string.wins)


        startGame.setOnClickListener{
            startActivity(Intent(this@MainMenu, MainActivity::class.java))}

        logout.setOnClickListener(){
            Firebase.auth.signOut()
            startActivity(Intent(this@MainMenu, LoginActivity::class.java))
        }

        reference.child("Users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(User::class.java)

                user?.let {
                    val email = profile?.email
                    val username = profile?.username
                    nWins = profile?.wins!!
                    welcome.text = welcomeString.plus(" ").plus(username)
                    wins.text = winsString.plus(" ").plus(nWins)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainMenu, "Something went wrong getting data", Toast.LENGTH_LONG).show()
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