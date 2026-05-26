package com.example.supertrex

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import android.view.View

class MainActivity : ComponentActivity() {

    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.gameView)

        val txtScore = findViewById<TextView>(R.id.txtScore)

        val btnJump = findViewById<Button>(R.id.btnJump)
        val btnDown = findViewById<Button>(R.id.btnSlide)
        val btnPause = findViewById<Button>(R.id.btnPause)
        val btnRestart = findViewById<Button>(R.id.btnRestart)

        gameView.onScoreChanged = { score ->
            txtScore.text = "Score: $score"
        }
        gameView.onGameOver = {
            btnRestart.visibility = View.VISIBLE
            btnJump.visibility = View.INVISIBLE
            btnDown.visibility = View.INVISIBLE
        }


        btnJump.setOnClickListener {
            gameView.jump()
        }

        btnDown.setOnClickListener {
            gameView.duck()
        }

        btnPause.setOnClickListener {
            gameView.pauseGame()
        }

        btnRestart.setOnClickListener {

            gameView.restartGame()

            btnRestart.visibility = View.GONE

            btnJump.visibility = View.VISIBLE
            btnDown.visibility = View.VISIBLE
        }
    }
}