package com.example.supertrex

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

        val imgWelcome = findViewById<ImageView>(R.id.imgWelcome)
        val btnStart = findViewById<ImageButton>(R.id.btnStart)
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)

        val imgAd = findViewById<ImageView>(R.id.imgAd)
        val btnCloseAd = findViewById<ImageButton>(R.id.btnCloseAd)

        gameView.onShowAd = {

            imgAd.visibility = View.VISIBLE

            imgAd.setImageResource(gameView.randomAd)

            btnCloseAd.visibility = View.GONE

            gameView.onShowCloseButton = {

                runOnUiThread {
                    btnCloseAd.visibility = View.VISIBLE
                }
            }

            gameView.advertisment()
        }

        btnJump.visibility = View.INVISIBLE
        btnSlide.visibility = View.INVISIBLE
        btnPause.visibility = View.INVISIBLE

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


        btnStart.setOnClickListener {

            gameView.startGame()

            imgWelcome.visibility = View.GONE
            btnStart.visibility = View.GONE
            btnSettings.visibility = View.GONE

            btnJump.visibility = View.VISIBLE
            btnSlide.visibility = View.VISIBLE
            btnPause.visibility = View.VISIBLE
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

                btnJump.visibility = View.INVISIBLE
                btnSlide.visibility = View.INVISIBLE

            } else {
                btnPause.setImageResource(R.drawable.pause_button)

                btnJump.visibility = View.VISIBLE
                btnSlide.visibility = View.VISIBLE
            }
        }

        btnCloseAd.setOnClickListener {

            imgAd.visibility = View.GONE
            btnCloseAd.visibility = View.GONE

            gameView.closeAd()
        }

        btnRestart.setOnClickListener {

            gameView.restartGame()
            gameView.closeAd()

            btnCloseAd.visibility = View.GONE
            imgAd.visibility = View.GONE

            btnRestart.visibility = View.GONE

            btnJump.visibility = View.VISIBLE
            btnSlide.visibility = View.VISIBLE
        }
    }
}