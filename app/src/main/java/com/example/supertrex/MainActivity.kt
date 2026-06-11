package com.example.supertrex

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
class MainActivity : ComponentActivity() {

    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.gameView)

        // SCORES
        val txtScore = findViewById<TextView>(R.id.txtScore)
        val txtCoins = findViewById<TextView>(R.id.txtCoins)

        // BUTTONS
        val btnJump = findViewById<ImageButton>(R.id.btnJump)
        val btnSlide = findViewById<ImageButton>(R.id.btnSlide)
        val btnPause = findViewById<ImageButton>(R.id.btnPause)
        val btnRestart = findViewById<ImageButton>(R.id.btnRestart)
        val btnStart = findViewById<ImageButton>(R.id.btnStart)
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)
        val btnCloseAd = findViewById<ImageButton>(R.id.btnCloseAd)

        // IMAGES
        val imgWelcome = findViewById<ImageView>(R.id.imgWelcome)
        val imgAd = findViewById<ImageView>(R.id.imgAd)

        // AD DISPLAY
        gameView.onShowAd = {

            imgAd.visibility = View.VISIBLE

            Glide.with(this)
                .asGif()
                .load(gameView.randomAd)
                .into(imgAd)

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


        // SCORE DISPLAY
        gameView.onScoreChanged = { score ->
            txtScore.text = "Score: $score"
        }

        gameView.onCoinsChanged = { coins ->
            txtCoins.text = "Coins: $coins"
        }

        // GAME OVER DISPLAY
        gameView.onGameOver = {
            btnRestart.visibility = View.VISIBLE
            btnJump.visibility = View.INVISIBLE
            btnSlide.visibility = View.INVISIBLE
        }

        // START BUTTON EVENTS
        btnStart.setOnClickListener {

            gameView.startGame()

            imgWelcome.visibility = View.GONE
            btnStart.visibility = View.GONE
            btnSettings.visibility = View.GONE

            btnJump.visibility = View.VISIBLE
            btnSlide.visibility = View.VISIBLE
            btnPause.visibility = View.VISIBLE
        }

        // JUMP BUTTON EVENTS
        btnJump.setOnClickListener {
            gameView.jump()
        }

        // SLIDE BUTTON EVENTS
        btnSlide.setOnClickListener {
            gameView.slide()
        }

        // PAUSE BUTTON EVENTS
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

        // CLOSE BUTTON EVENTS
        btnCloseAd.setOnClickListener {

            imgAd.visibility = View.GONE
            btnCloseAd.visibility = View.GONE

            gameView.closeAd()
        }

        // RESTART BUTTON EVENTS
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