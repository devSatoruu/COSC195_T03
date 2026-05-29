package com.example.supertrex

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import android.view.View
import android.widget.ImageButton

class MainActivity : ComponentActivity() {

    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.gameView)

        val txtScore = findViewById<TextView>(R.id.txtScore)
        val txtCoins = findViewById<TextView>(R.id.txtCoins)

        val btnJump = findViewById<ImageButton>(R.id.btnJump)
        val btnSlide = findViewById<ImageButton>(R.id.btnSlide)
        val btnPause = findViewById<ImageButton>(R.id.btnPause)
        val btnRestart = findViewById<ImageButton>(R.id.btnRestart)

        gameView.onScoreChanged = { score ->
            txtScore.text = "Score: $score"
        }

        gameView.onCoinsChanged = { coins ->
            txtCoins.text = "Coins: $coins"
        }

        gameView.onGameOver = {
            btnRestart.visibility = View.VISIBLE
            btnJump.visibility = View.INVISIBLE
            btnSlide.visibility = View.INVISIBLE
        }


        btnJump.setOnClickListener {
            gameView.jump()
        }

        btnSlide.setOnClickListener {
            gameView.slide()
        }

        btnPause.setOnClickListener {
            gameView.pauseGame()

            if (gameView.isPausedNow()) {
                btnPause.setImageResource(R.drawable.play_button)
            } else {
                btnPause.setImageResource(R.drawable.pause_button)
            }
        }

        btnRestart.setOnClickListener {

            gameView.restartGame()

            btnRestart.visibility = View.GONE

            btnJump.visibility = View.VISIBLE
            btnSlide.visibility = View.VISIBLE
        }
    }
}