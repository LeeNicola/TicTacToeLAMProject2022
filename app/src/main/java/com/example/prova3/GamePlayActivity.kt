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
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.system.exitProcess

var playerTurn = true
class GamePlayActivity : AppCompatActivity() {

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
    private var activeUser = 1
    private var userID = Firebase.auth.currentUser!!.uid
    private var reference = Firebase.database.reference
    private val SEMICOLON = " : "
    private val USERS = "Users"
    private val WINS = "wins"
    private val X : String = "X"
    private val O : String = "O"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_play)

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
        backBtn = findViewById(R.id.back)
        turnTV = findViewById(R.id.idTVTurn)
        timerTV = findViewById(R.id.timer)
        player2String = getString(R.string.player2)
        turnString = getString(R.string.turn)
        val loseAudio = MediaPlayer.create(this,R.raw.draw_sound)


        val timer = object: CountDownTimer(60000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timerTV.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                buttonDisable()
                loseAudio.start()
                buildAlert(loseAudio, getString(R.string.time_out))
            }
        }

        timer.start()
        turnTV.text = turnString.plus(SEMICOLON).plus(playerUsername)
        player1TV.text = playerUsername.plus(SEMICOLON).plus(player1count)
        player2TV.text = player2String.plus(SEMICOLON).plus(player2count)

        resetBtn.setOnClickListener{
            reset()
        }

        backBtn.setOnClickListener{
            finish()
        }
    }

    private fun reset() {
        player1.clear()
        player2.clear()
        clickedCells.clear()
        turnTV.text = turnString.plus(SEMICOLON).plus(playerUsername)
        activeUser = 1
        player1TV.text = playerUsername.plus(SEMICOLON).plus(player1count)
        player2TV.text = player2String.plus(SEMICOLON).plus(player2count)
        playerTurn = true
        for(i in 1..9){
            val buttonSelected : Button  = when(i) {
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

    fun buttonClick(view: View) {
        if(playerTurn){
            val but = view as Button
            var cellID = 0
            when (but.id) {
                R.id.idBtnBox1 -> cellID = 1
                R.id.idBtnBox2 -> cellID = 2
                R.id.idBtnBox3 -> cellID = 3
                R.id.idBtnBox4 -> cellID = 4
                R.id.idBtnBox5 -> cellID = 5
                R.id.idBtnBox6 -> cellID = 6
                R.id.idBtnBox7 -> cellID = 7
                R.id.idBtnBox8 -> cellID = 8
                R.id.idBtnBox9 -> cellID = 9
            }
            playerTurn = false
            Handler(Looper.getMainLooper()).postDelayed( { playerTurn = true }, 800)
            playNow(but, cellID)
        }

    }

    private fun playNow(buttonSelected: Button, currCell: Int) {
        val audio = MediaPlayer.create(this,R.raw.click_sound)
        if(activeUser == 1){
            audio.start()
            buttonSelected.text = X
            turnTV.text = turnString.plus(SEMICOLON).plus(player2String)
            buttonSelected.setTextColor(Color.parseColor("#FF000000"))
            player1.add(currCell)
            clickedCells.add(currCell)
            buttonSelected.isEnabled = false
            val checkWinner = checkWinner()
            if(checkWinner == 1){
                Handler(Looper.getMainLooper()).postDelayed( { reset()}, 1000)
            } else if (singleUser){
                Handler(Looper.getMainLooper()).postDelayed( { ia()}, 800)
            } else {
                activeUser = 2
            }
        } else {
            audio.start()
            buttonSelected.text = O
            turnTV.text = turnString.plus(SEMICOLON).plus(playerUsername)
            buttonSelected.setTextColor(Color.parseColor("#FFFFFF"))
            activeUser = 1
            player2.add(currCell)
            clickedCells.add(currCell)
            buttonSelected.isEnabled = false
            val checkWinner = checkWinner()
            if(checkWinner == 1){
                Handler(Looper.getMainLooper()).postDelayed( { reset() }, 800)
            }
        }
    }

    private fun ia() {
        val rnd = (1..9).random()
        if (clickedCells.contains(rnd)){
            ia()
        } else {
            val buttonSelected = when(rnd){
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
            clickedCells.add(rnd)
            val audio = MediaPlayer.create(this,R.raw.click_sound)
            audio.start()
            buttonSelected.text = O
            turnTV.text = turnString.plus(SEMICOLON).plus(playerUsername)
            buttonSelected.setTextColor(Color.parseColor("#FFFFFF"))
            player2.add(rnd)
            buttonSelected.isEnabled = false
            val checkWinner = checkWinner()
            if(checkWinner==1){
                Handler(Looper.getMainLooper()).postDelayed( { reset() }, 800)
            }
        }
    }

    private fun checkWinner(): Int {
        val audio = MediaPlayer.create(this,R.raw.victory_sound)
        val loseAudio = MediaPlayer.create(this,R.raw.draw_sound)
        if(winCondition(player1)){
            Handler(Looper.getMainLooper()).postDelayed({ reset() }, 300)
            player1count+=1
            nWins+=1
            reference.child(USERS).child(userID).child(WINS).setValue(nWins)
            buttonDisable()
            audio.start()
            buildAlert(audio, getString(R.string.player1_win))
            return 1

        } else if(winCondition(player2)){
            player2count+=1
            buttonDisable()
            loseAudio.start()
            buildAlert(loseAudio, getString(R.string.player2_win))
            return 1

        } else if(clickedCells.contains(1) && clickedCells.contains(2) && clickedCells.contains(3) &&
                clickedCells.contains(4) && clickedCells.contains(5) && clickedCells.contains(6) &&
                clickedCells.contains(7) && clickedCells.contains(8) && clickedCells.contains(9)){
            buttonDisable()
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
            reset()
            audio.release()
        }
        build.setNegativeButton(getString(R.string.exit)){ _,_->
            audio.release()
            exitProcess(1)
        }
        Handler(Looper.getMainLooper()).postDelayed( { build.show() }, 700)
    }

    private fun buttonDisable() {
        resetBtn.isEnabled = false
        backBtn.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({ resetBtn.isEnabled = true }, 1000)
        Handler(Looper.getMainLooper()).postDelayed({ backBtn.isEnabled = true }, 1000)
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