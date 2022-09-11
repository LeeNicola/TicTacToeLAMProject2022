package com.example.prova3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

var isCodeMaker = true
var code = "null"
var codeFound = false
var keyValue :String = "null"
var isCodeInvalid = false
var nPlayers = 1

class OnlineCodeGeneratorActivity : AppCompatActivity() {
    private lateinit var headTV : TextView
    private lateinit var codeEdt : EditText
    private lateinit var createCodeBtn : Button
    private lateinit var joinCodeBtn : Button
    private lateinit var loadingPB : ProgressBar
    private lateinit var backBtn : Button
    private var reference = Firebase.database.reference
    private val CODES = "codes"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_code_generator)
        headTV = findViewById(R.id.idTVHead)
        codeEdt = findViewById(R.id.idEdtCode)
        createCodeBtn = findViewById(R.id.idBtnCreate)
        joinCodeBtn = findViewById(R.id.idBtnJoin)
        loadingPB = findViewById(R.id.idPBLoading)
        backBtn = findViewById(R.id.back)

        backBtn.setOnClickListener(){
            finish()
        }

        createCodeBtn.setOnClickListener {
            code = "null"
            codeFound = false
            keyValue = "null"
            code = codeEdt.text.toString()
            itemsVisibility(false)
            if (code != "null" && code != ""){
                isCodeMaker = true
                reference.child("codes").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val check = isValueAvailable(snapshot, code)
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (check){
                                itemsVisibility(true)
                            } else {
                                reference.child(CODES).push().setValue(code)
                                isValueAvailable(snapshot, code)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    accepted()
                                    Toast.makeText(this@OnlineCodeGeneratorActivity, getString(R.string.dontGoBack),Toast.LENGTH_SHORT).show()
                                },300)
                            }
                        },2000)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        throw error.toException()
                    }
                })
            } else {
                itemsVisibility(true)
                Toast.makeText(this,getString(R.string.enter_valid_code),Toast.LENGTH_SHORT).show()
            }
        }

        joinCodeBtn.setOnClickListener{
            code = "null"
            codeFound = false
            keyValue = "null"
            code = codeEdt.text.toString()
            if (code != "null" && code != ""){
                itemsVisibility(false)
                isCodeMaker = false
                reference.child(CODES).addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val data : Boolean = isValueAvailable(snapshot, code)
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (data){
                                codeFound = true
                                nPlayers = 2
                                accepted()
                                itemsVisibility(true)
                            } else{
                                itemsVisibility(true)
                                isCodeInvalid = true
                                Toast.makeText(this@OnlineCodeGeneratorActivity, getString(R.string.invalid_code),Toast.LENGTH_SHORT).show()
                            }
                        },200)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@OnlineCodeGeneratorActivity, getString(R.string.dbError),Toast.LENGTH_SHORT).show()
                    }
                })

            } else {
                Toast.makeText(this,getString(R.string.enter_valid_code),Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun accepted(){
        startActivity(Intent(this,OnlineMultiPlayerGameActivity::class.java))
        itemsVisibility(true)
    }

    fun isValueAvailable(snapshot: DataSnapshot, code : String): Boolean{
        val data = snapshot.children
        data.forEach{
            val value = it.value.toString()
            if(value==code){
                keyValue = it.key.toString()
                return true
            }
        }
        return false
    }

    private fun itemsVisibility(boolean: Boolean){
        if (boolean){
            createCodeBtn.visibility = View.VISIBLE
            joinCodeBtn.visibility = View.VISIBLE
            codeEdt.visibility = View.VISIBLE
            headTV.visibility = View.VISIBLE
            loadingPB.visibility = View.GONE
        } else {
            createCodeBtn.visibility = View.GONE
            joinCodeBtn.visibility = View.GONE
            headTV.visibility = View.GONE
            codeEdt.visibility = View.GONE
            loadingPB.visibility = View.VISIBLE
        }
    }
}