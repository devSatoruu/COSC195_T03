package com.example.supertrex

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI ELEMENTS
        gameView = findViewById(R.id.gameView)
        val scoreTxt = findViewById<TextView>(R.id.txtScore)
        val coinTxt = findViewById<TextView>(R.id.txtCoins)

        val jump = findViewById<ImageButton>(R.id.btnJump)
        val slide = findViewById<ImageButton>(R.id.btnSlide)
        val pause = findViewById<ImageButton>(R.id.btnPause)
        val restart = findViewById<ImageButton>(R.id.btnRestart)

        val start = findViewById<ImageButton>(R.id.btnStart)
        val adImg = findViewById<ImageView>(R.id.imgAd)
        val closeAd = findViewById<ImageButton>(R.id.btnCloseAd)

        // SCORE UPDATE
        gameView.onScoreChanged = { score ->
            scoreTxt.text = "Score: $score"
        }

        // COIN UPDATE
        gameView.onCoinsChanged = { coins ->
            coinTxt.text = "Coins: $coins"
        }

        // GAME OVER
        gameView.onGameOver = {
            restart.visibility = View.VISIBLE
        }

        // SHOW AD
        gameView.onShowAd = {
            adImg.visibility = View.VISIBLE
            adImg.setImageResource(gameView.randomAd)

            gameView.onShowCloseButton = {
                runOnUiThread {
                    closeAd.visibility = View.VISIBLE
                }
            }

            gameView.advertisment()
        }

        // START GAME
        start.setOnClickListener {
            gameView.startGame()
            start.visibility = View.GONE
        }

        // CONTROLS
        jump.setOnClickListener { gameView.jump() }
        slide.setOnClickListener { gameView.slide() }
        pause.setOnClickListener { gameView.pauseGame() }

        // CLOSE AD
        closeAd.setOnClickListener {
            adImg.visibility = View.GONE
            closeAd.visibility = View.GONE
            gameView.closeAd()
        }

        // RESTART GAME
        restart.setOnClickListener {
            gameView.restartGame()
            restart.visibility = View.GONE
        }
    }
}