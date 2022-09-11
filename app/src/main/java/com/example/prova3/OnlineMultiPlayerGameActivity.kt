package com.example.prova3

import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.system.exitProcess

var isMyMove = isCodeMaker
class OnlineMultiPlayerGameActivity : AppCompatActivity() {

    private lateinit var player1TV : TextView
    private lateinit var player2TV : TextView
    private lateinit var box1Btn : Button
    private lateinit var box2Btn : Button
    private lateinit var box3Btn : Button
    private lateinit var box4Btn : Button
    private lateinit var box5Btn : Button
    private lateinit var box6Btn : Button
    private lateinit var box7Btn : Button
    private lateinit var box8Btn : Button
    private lateinit var box9Btn : Button
    private lateinit var resetBtn : Button
    private lateinit var turnTV : TextView
    private lateinit var player2String : String
    private lateinit var turnString : String
    private lateinit var backBtn : Button
    private lateinit var timerTV : TextView
    private var player1count = 0
    private var player2count = 0
    private var player1 = ArrayList<Int>()
    private var player2 = ArrayList<Int>()
    private var clickedCells = ArrayList<Int>()
    private var userID = Firebase.auth.currentUser!!.uid
    private var reference = Firebase.database.reference
    private val CODES = "codes"
    private val DATA = "data"
    private val SEMICOLON = " : "
    private val USERS = "Users"
    private val WINS = "wins"
    private val X : String = "X"
    private val O : String = "O"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_multi_player_game)
        player1TV = findViewById(R.id.idTVPlayer1)
        player2TV = findViewById(R.id.idTVPlayer2)
        box1Btn = findViewById(R.id.idBtnBox1)
        box2Btn = findViewById(R.id.idBtnBox2)
        box3Btn = findViewById(R.id.idBtnBox3)
        box4Btn = findViewById(R.id.idBtnBox4)
        box5Btn = findViewById(R.id.idBtnBox5)
        box6Btn = findViewById(R.id.idBtnBox6)
        box7Btn = findViewById(R.id.idBtnBox7)
        box8Btn = findViewById(R.id.idBtnBox8)
        box9Btn = findViewById(R.id.idBtnBox9)
        resetBtn = findViewById(R.id.idBtnReset)
        turnTV = findViewById(R.id.idTVTurn)
        backBtn = findViewById(R.id.back)
        timerTV = findViewById(R.id.timer)
        player2String = getString(R.string.player2)
        turnString = getString(R.string.turn)

        val loseAudio = MediaPlayer.create(this,R.raw.draw_sound)


        val timer = object: CountDownTimer(60000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timerTV.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                buttonDisable(300)
                loseAudio.start()
                buildAlert(loseAudio, getString(R.string.time_out))
            }
        }



        player1TV.text = playerUsername.plus(SEMICOLON).plus(player1count)
        player2TV.text = player2String.plus(SEMICOLON).plus(player2count)

        backBtn.setOnClickListener(){
            finish()
        }

        resetBtn.setOnClickListener {
            reset()
        }

        if (nPlayers == 1) {
            Toast.makeText(this@OnlineMultiPlayerGameActivity, getString(R.string.sec_timer), Toast.LENGTH_SHORT).show()
            buttonDisable(10000)
            Handler(Looper.getMainLooper()).postDelayed({ onBackPressed() }, 10000)
            Handler(Looper.getMainLooper()).postDelayed({ Toast.makeText(this@OnlineMultiPlayerGameActivity, getString(
                            R.string.scared), Toast.LENGTH_SHORT).show() }, 9999)
        }

        timer.start()

        reference.child(DATA).child(code).addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.value
                if(isMyMove){
                    isMyMove = false
                    moveOnline(data.toString(), isMyMove)
                } else{
                    isMyMove = true
                    moveOnline(data.toString(), isMyMove)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //do nothing
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                reset()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //do nothing
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OnlineMultiPlayerGameActivity, getString(R.string.dbError), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun reset() {
        player1.clear()
        player2.clear()
        clickedCells.clear()
        player1TV.text = playerUsername.plus(SEMICOLON).plus(player1count)
        player2TV.text = player2String.plus(SEMICOLON).plus(player2count)
        turnTV.text = turnString.plus(SEMICOLON).plus(playerUsername)
        isMyMove = isCodeMaker
        reference.child(DATA).child(code).removeValue()
        for(i in 1..9){
            val buttonSelected : Button = when(i) {
                1->box1Btn
                2->box2Btn
                3->box3Btn
                4->box4Btn
                5->box5Btn
                6->box6Btn
                7->box7Btn
                8->box8Btn
                9->box9Btn
                else->{
                    box1Btn
                }
            }
            buttonSelected.isEnabled = true
            buttonSelected.text = ""
        }
    }

    fun moveOnline(data : String, move : Boolean){
        val audio = MediaPlayer.create(this, R.raw.click_sound)
        if(move){
            val buttonSelected : Button = when(data.toInt()){
                1->box1Btn
                2->box2Btn
                3->box3Btn
                4->box4Btn
                5->box5Btn
                6->box6Btn
                7->box7Btn
                8->box8Btn
                9->box9Btn
                else->{
                    box1Btn
                }
            }
            audio.start()
            buttonSelected.text = O
            turnTV.text = turnString.plus(SEMICOLON).plus(playerUsername)
            buttonSelected.setTextColor(Color.parseColor("#FFFFFF"))
            player2.add(data.toInt())
            clickedCells.add(data.toInt())
            buttonSelected.isEnabled = false
            checkWinner()
        }
    }

    private fun playNow(buttonSelected: Button, currCell: Int) {
        val audio = MediaPlayer.create(this,R.raw.click_sound)
        audio.start()
        buttonSelected.text = X
        turnTV.text = turnString.plus(SEMICOLON).plus(player2String)
        buttonSelected.setTextColor(Color.parseColor("#FF000000"))
        player1.add(currCell)
        clickedCells.add(currCell)
        buttonSelected.isEnabled = false
        checkWinner()

    }

    private fun checkWinner(): Int {
        val audio = MediaPlayer.create(this,R.raw.victory_sound)
        val loseAudio = MediaPlayer.create(this,R.raw.draw_sound)
        if(winCondition(player1)){
            Handler(Looper.getMainLooper()).postDelayed({ reset() }, 300)
            player1count+=1
            nWins+=1
            reference.child(USERS).child(userID).child(WINS).setValue(nWins)
            buttonDisable(300)
            audio.start()
            buildAlert(audio, getString(R.string.player1_win))
            return 1

        } else if(winCondition(player2)){
            Handler(Looper.getMainLooper()).postDelayed( { reset() }, 300)
            player2count+=1
            buttonDisable(300)
            audio.start()
            buildAlert(loseAudio, getString(R.string.player2_win))

            return 1

        } else if(clickedCells.contains(1) && clickedCells.contains(2) && clickedCells.contains(3) &&
            clickedCells.contains(4) && clickedCells.contains(5) && clickedCells.contains(6) &&
            clickedCells.contains(7) && clickedCells.contains(8) && clickedCells.contains(9)){
            Handler(Looper.getMainLooper()).postDelayed( { reset() }, 300)
            buttonDisable(300)
            loseAudio.start()
            buildAlert(loseAudio, getString(R.string.draw))
            return 1
        }
        return 0

    }

    private fun winCondition(player : ArrayList<Int>): Boolean {
        if((player.contains(1) && player.contains(2) && player.contains(3)) ||
                (player.contains(4) && player.contains(5) && player.contains(6)) ||
                (player.contains(7) && player.contains(8) && player.contains(9)) ||
                (player.contains(1) && player.contains(4) && player.contains(7)) ||
                (player.contains(2) && player.contains(5) && player.contains(8)) ||
                (player.contains(3) && player.contains(6) && player.contains(9)) ||
                (player.contains(1) && player.contains(5) && player.contains(9)) ||
                (player.contains(3) && player.contains(5) && player.contains(7))) {
            return true
        }
        return false
    }

    private fun buildAlert(audio : MediaPlayer, alertMessage : String){
        val build = AlertDialog.Builder(this)
        build.setTitle(getString(R.string.game_over))
        build.setMessage(alertMessage +" \n\n" + getString(R.string.play_again))
        build.setPositiveButton(getString(R.string.ok)){ _,_->
            isCodeInvalid()
            reset()
            audio.release()
        }
        build.setNegativeButton(getString(R.string.exit)){ _,_->
            audio.release()
            removeCode()
            exitProcess(1)
        }
        Handler(Looper.getMainLooper()).postDelayed( { build.show() }, 2000)
    }

    private fun isCodeInvalid(){
        if (isCodeInvalid) {
            finish()
            Toast.makeText(this@OnlineMultiPlayerGameActivity, getString(R.string.opponent_fled), Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeCode(){
        reference.child(CODES).child(keyValue).removeValue()
        reference.child(DATA).child(code).removeValue()
    }

    private fun updateDatabase(cellID : Int){
        reference.child(DATA).child(code).push().setValue(cellID)
    }


    override fun onBackPressed() {
        removeCode()
        exitProcess(0)
    }

    fun buttonClick(view: View) {
        if(isMyMove){
            val but = view as Button
            val cellOnline: Int  = when (but.id) {
                R.id.idBtnBox1 -> 1
                R.id.idBtnBox2 -> 2
                R.id.idBtnBox3 -> 3
                R.id.idBtnBox4 -> 4
                R.id.idBtnBox5 -> 5
                R.id.idBtnBox6 -> 6
                R.id.idBtnBox7 -> 7
                R.id.idBtnBox8 -> 8
                R.id.idBtnBox9 -> 9
                else -> {
                    0
                }
            }
            playerTurn = false
            Handler(Looper.getMainLooper()).postDelayed({ playerTurn = true }, 800)
            playNow(but, cellOnline)
            updateDatabase(cellOnline)
        }
    }

    private fun buttonDisable(time : Long) {
        resetBtn.isEnabled = false
        backBtn.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({ resetBtn.isEnabled = true }, time)
        Handler(Looper.getMainLooper()).postDelayed({ backBtn.isEnabled = true }, time)
        for (i in 1..9){
            val buttonSelected : Button = when(i){
                1->box1Btn
                2->box2Btn
                3->box3Btn
                4->box4Btn
                5->box5Btn
                6->box6Btn
                7->box7Btn
                8->box8Btn
                9->box9Btn
                else->{
                    box1Btn
                }
            }
            if (buttonSelected.isEnabled){
                buttonSelected.isEnabled = false
            }
        }
    }
}