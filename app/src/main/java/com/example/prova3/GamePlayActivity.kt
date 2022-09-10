package com.example.prova3

import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlin.system.exitProcess

var playerTurn = true
class GamePlayActivity : AppCompatActivity() {

    lateinit private var player1TV : TextView
    lateinit private var player2TV : TextView
    lateinit private var box1Btn : Button
    lateinit private var box2Btn : Button
    lateinit private var box3Btn : Button
    lateinit private var box4Btn : Button
    lateinit private var box5Btn : Button
    lateinit private var box6Btn : Button
    lateinit private var box7Btn : Button
    lateinit private var box8Btn : Button
    lateinit private var box9Btn : Button
    lateinit private var resetBtn : Button
    lateinit private var player1String : String
    lateinit private var player2String : String
    private var player1count = 0
    private var player2count = 0
    private var player1 = ArrayList<Int>()
    private var player2 = ArrayList<Int>()
    private var clickedCells = ArrayList<Int>()
    private var activeUser = 1


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
        player1String = getString(R.string.player1)
        player2String = getString(R.string.player2)

        resetBtn.setOnClickListener{
            reset()
        }
    }

    private fun reset() {
        player1.clear()
        player2.clear()
        clickedCells.clear()
        activeUser = 1
        player1TV.text = player1String.plus(" : ").plus(player1count)
        player2TV.text = player2String.plus(" : ").plus(player2count)
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
            Handler(Looper.getMainLooper()).postDelayed(Runnable { playerTurn = true }, 800)
            playNow(but, cellID)
        }

    }

    private fun playNow(buttonSelected: Button, currCell: Int) {
        val audio = MediaPlayer.create(this,R.raw.click_sound)
        if(activeUser == 1){
            audio.start()
            buttonSelected.text = "X"
            buttonSelected.setTextColor(Color.parseColor("#EC0C0C"))
            player1.add(currCell)
            clickedCells.add(currCell)
            buttonSelected.isEnabled = false
            val checkWinner = checkWinner()
            if(checkWinner == 1){
                Handler(Looper.getMainLooper()).postDelayed(Runnable { reset()}, 800)
            } else if (singleUser){
                Handler(Looper.getMainLooper()).postDelayed(Runnable { robot()}, 800)
            } else {
                activeUser = 2
            }
        } else {
            audio.start()
            buttonSelected.text = "O"
            buttonSelected.setTextColor(Color.parseColor("#EC0C0C"))
            activeUser = 1
            player2.add(currCell)
            clickedCells.add(currCell)
            buttonSelected.isEnabled = false
            val checkWinner = checkWinner()
            if(checkWinner == 1){
                Handler(Looper.getMainLooper()).postDelayed(Runnable { reset() }, 800)
            }
        }
    }

    private fun robot() {
        val rnd = (1..9).random()
        if (clickedCells.contains(rnd)){
            robot()
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
            buttonSelected.text = "O"
            buttonSelected.setTextColor(Color.parseColor("#EC0C0C"))
            player2.add(rnd)
            buttonSelected.isEnabled = false
            val checkWinner = checkWinner()
            if(checkWinner==1){
                Handler(Looper.getMainLooper()).postDelayed(Runnable { reset() }, 800)
            }
        }
    }

    private fun checkWinner(): Int {
        val audio = MediaPlayer.create(this,R.raw.victory_sound)
        val draw_audio = MediaPlayer.create(this,R.raw.draw_sound)
        if((player1.contains(1) && player1.contains(2) && player1.contains(3)) ||
            (player1.contains(4) && player1.contains(5) && player1.contains(6)) ||
            (player1.contains(7) && player1.contains(8) && player1.contains(9)) ||
            (player1.contains(1) && player1.contains(4) && player1.contains(7)) ||
            (player1.contains(2) && player1.contains(5) && player1.contains(8)) ||
            (player1.contains(3) && player1.contains(6) && player1.contains(9)) ||
            (player1.contains(1) && player1.contains(5) && player1.contains(9)) ||
            (player1.contains(3) && player1.contains(5) && player1.contains(7))){
            player1count+=1
            audio.start()
            val build = AlertDialog.Builder(this)
            build.setTitle("Game Over")
            build.setMessage("Player 1 Wins \n\n" + "Do you want to play again?")
            build.setPositiveButton("ok"){ _,_->
                reset()
                audio.release()
            }
            build.setNegativeButton("Exit"){ _,_->
                audio.release()
                exitProcess(1)
            }
            build.show()
            return 1

        } else if((player2.contains(1) && player2.contains(2) && player2.contains(3)) ||
            (player2.contains(4) && player2.contains(5) && player2.contains(6)) ||
            (player2.contains(7) && player2.contains(8) && player2.contains(9)) ||
            (player2.contains(1) && player2.contains(4) && player2.contains(7)) ||
            (player2.contains(2) && player2.contains(5) && player2.contains(8)) ||
            (player2.contains(3) && player2.contains(6) && player2.contains(9)) ||
            (player2.contains(1) && player2.contains(5) && player2.contains(9)) ||
            (player2.contains(3) && player2.contains(5) && player2.contains(7))){
            player2count+=1
            audio.start()
            val build = AlertDialog.Builder(this)
            build.setTitle("Game Over")
            build.setMessage("Player 2 Wins \n\n" + "Do you want to play again?")
            build.setPositiveButton("ok"){ _,_->
                reset()
                audio.release()
            }
            build.setNegativeButton("Exit"){ _,_->
                audio.release()
                exitProcess(1)
            }
            build.show()
            return 1

        } else if(clickedCells.contains(1) && clickedCells.contains(2) && clickedCells.contains(3) &&
                clickedCells.contains(4) && clickedCells.contains(5) && clickedCells.contains(6) &&
                clickedCells.contains(7) && clickedCells.contains(8) && clickedCells.contains(9)){
            draw_audio.start()
            val build = AlertDialog.Builder(this)
            build.setTitle("Game Over")
            build.setMessage("Game Draw \n\n" + "Do you want to play again?")
            build.setPositiveButton("ok"){ _,_->
                reset()
                draw_audio.release()
            }
            build.setNegativeButton("Exit"){ _,_->
                draw_audio.release()
                exitProcess(1)
            }
            build.show()
            return 1
        }
        return 0
    }
}