package com.example.prova3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainMenu : AppCompatActivity() {

    lateinit var wins: TextView
    lateinit var headTitle: TextView
    lateinit var welcome: TextView
    lateinit var startGame: Button
    lateinit var rules: Button
    lateinit var credits: Button
    lateinit var exitGame: Button
    lateinit var settings: ImageButton
    lateinit var logout: Button

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


        startGame.setOnClickListener{
            startActivity(Intent(this@MainMenu, MainActivity::class.java))}
    }
}