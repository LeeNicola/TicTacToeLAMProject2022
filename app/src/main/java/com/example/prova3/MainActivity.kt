package com.example.prova3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

var singleUser = false
class MainActivity : AppCompatActivity() {

    private lateinit var singlePlayerBtn : Button
    private lateinit var multiPlayerBtn : Button
    private lateinit var backBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        singlePlayerBtn = findViewById(R.id.singlePlayerBtn)
        multiPlayerBtn = findViewById(R.id.multiPlayerBtn)
        backBtn = findViewById(R.id.back)

        singlePlayerBtn.setOnClickListener(){
            singleUser = true
            startActivity(Intent(this,GamePlayActivity::class.java))
        }

        multiPlayerBtn.setOnClickListener(){
            singleUser = false
            startActivity(Intent(this,MultiPlayerGameSelectionActivity::class.java))
        }

        backBtn.setOnClickListener(){
            finish()
        }

    }
}