package com.example.supertrex

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.gameView)

        val btnJump = findViewById<Button>(R.id.btnJump)
        val btnDown = findViewById<Button>(R.id.btnSlide)
        val btnPause = findViewById<Button>(R.id.btnPause)

        btnJump.setOnClickListener {
            gameView.jump()
        }

        btnDown.setOnClickListener {
            gameView.duck()
        }

        btnPause.setOnClickListener {
            gameView.pauseGame()
        }
    }
}