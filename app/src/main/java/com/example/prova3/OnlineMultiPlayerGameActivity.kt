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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlin.system.exitProcess

var isMyTurn = isCodeCreator
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
    private lateinit var player1String : String
    private lateinit var player2String : String
    private lateinit var turnString : String
    private var player1count = 0
    private var player2count = 0
    private var player1 = ArrayList<Int>()
    private var player2 = ArrayList<Int>()
    private var clickedCells = ArrayList<Int>()
    private var nPlayers = 1

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
        player1String = getString(R.string.player1)
        player2String = getString(R.string.player2)
        turnString = getString(R.string.turn)

        resetBtn.setOnClickListener {
            reset()
        }

        reference.child("data").child(code).addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.value
                if(isMyTurn){
                    isMyTurn = false
                    moveOnline(data.toString(), isMyTurn)
                } else{
                    isMyTurn = true
                    moveOnline(data.toString(), isMyTurn)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                reset()
                Toast.makeText(this@OnlineMultiPlayerGameActivity,"Game Reset",Toast.LENGTH_SHORT).show()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun reset() {
        player1.clear()
        player2.clear()
        clickedCells.clear()
        nPlayers = 1
        player1TV.text = player1String.plus(" : ").plus(player1count)
        player2TV.text = player2String.plus(" : ").plus(player2count)
        isMyTurn = isCodeCreator
        if(isCodeCreator){
            reference.child("data").child(code).removeValue()
        }
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

    private fun moveOnline(data : String, move : Boolean){
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
            buttonSelected.text = "0"
            turnTV.text = turnString.plus(" : ").plus(player1String)
            buttonSelected.setTextColor(Color.parseColor("#EC0C0C"))
            player2.add(data.toInt())
            clickedCells.add(data.toInt())
            buttonSelected.isEnabled = false
            checkWinner()
        }
    }

    private fun playNow(buttonSelected: Button, currCell: Int) {
        val audio = MediaPlayer.create(this,R.raw.click_sound)
        audio.start()
        buttonSelected.text = "X"
        turnTV.text = turnString.plus(" : ").plus(player2String)
        buttonSelected.setTextColor(Color.parseColor("#EC0C0C"))
        player1.add(currCell)
        clickedCells.add(currCell)
        buttonSelected.isEnabled = false
        checkWinner()

    }

    private fun checkWinner(): Int {
        val audio = MediaPlayer.create(this,R.raw.victory_sound)
        val drawAudio = MediaPlayer.create(this,R.raw.draw_sound)
        if((player1.contains(1) && player1.contains(2) && player1.contains(3)) ||
            (player1.contains(4) && player1.contains(5) && player1.contains(6)) ||
            (player1.contains(7) && player1.contains(8) && player1.contains(9)) ||
            (player1.contains(1) && player1.contains(4) && player1.contains(7)) ||
            (player1.contains(2) && player1.contains(5) && player1.contains(8)) ||
            (player1.contains(3) && player1.contains(6) && player1.contains(9)) ||
            (player1.contains(1) && player1.contains(5) && player1.contains(9)) ||
            (player1.contains(3) && player1.contains(5) && player1.contains(7))){
            player1count+=1
            nWins+=player1count
            audio.start()
            reference.child("Users").child(userID).child("wins").push().setValue(nWins)
            val build = AlertDialog.Builder(this)
            build.setTitle("Game Over")
            build.setMessage("Player 1 Wins \n\n" + "Do you want to play again?")
            build.setPositiveButton("ok"){ _,_->
                reset()
                audio.release()
            }
            build.setNegativeButton("Exit"){ _,_->
                audio.release()
                removeCode()
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
                removeCode()
                exitProcess(1)
            }
            build.show()
            return 1

        } else if(clickedCells.contains(1) && clickedCells.contains(2) && clickedCells.contains(3) &&
            clickedCells.contains(4) && clickedCells.contains(5) && clickedCells.contains(6) &&
            clickedCells.contains(7) && clickedCells.contains(8) && clickedCells.contains(9)){
            drawAudio.start()
            val build = AlertDialog.Builder(this)
            build.setTitle("Game Over")
            build.setMessage("Game Draw \n\n" + "Do you want to play again?")
            build.setPositiveButton("ok"){ _,_->
                reset()
                drawAudio.release()
            }
            build.setNegativeButton("Exit"){ _,_->
                drawAudio.release()
                removeCode()
                exitProcess(1)
            }
            Handler(Looper.getMainLooper()).postDelayed(Runnable { build.show() }, 2000)
            return 1
        }
        return 0

    }

    private fun removeCode(){
        if (isCodeCreator){
            reference.child("codes").child(keyValue).removeValue()
        }
    }

    private fun updateDatabase(cellID : Int){
        reference.child("data").child(code).push().setValue(cellID)
    }

    override fun onBackPressed() {
        removeCode()
        if (isCodeCreator){
            reference.child("data").child(code).removeValue()
        }
        exitProcess(0)
    }

    fun buttonClick(view: View) {
        if(isMyTurn){
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
            Handler(Looper.getMainLooper()).postDelayed(Runnable { playerTurn = true }, 800)
            playNow(but, cellOnline)
            updateDatabase(cellOnline)
        }
    }
}