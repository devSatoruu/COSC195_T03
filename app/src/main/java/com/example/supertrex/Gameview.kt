package com.example.supertrex

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.*

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // PAINT - used for drawing (not heavily used yet)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // GAME OVER IMAGE - shown when player loses
    private val gameOverBitmap = BitmapFactory.decodeResource(resources, R.drawable.game_over)

    // PAUSE IMAGE - shown when game is paused
    private val pausedBitmap = BitmapFactory.decodeResource(resources, R.drawable.paused)

    // BACKGROUND IMAGE - scrolling desert
    private val desertBg = BitmapFactory.decodeResource(resources, R.drawable.desert_bg)

    // PLAYER SPRITES - run, jump, slide states
    private val girlRun = BitmapFactory.decodeResource(resources, R.drawable.girl_run)
    private val girlSlide = BitmapFactory.decodeResource(resources, R.drawable.girl_slide)
    private val girlJump = BitmapFactory.decodeResource(resources, R.drawable.girl_jump)

    // OBSTACLES - cactus and bomb
    private val cactus = BitmapFactory.decodeResource(resources, R.drawable.cactus)
    private val bomb = BitmapFactory.decodeResource(resources, R.drawable.bomb)

    // COIN IMAGE - collectible item
    private val coin = BitmapFactory.decodeResource(resources, R.drawable.coin)

    // ADS LIST - random ad selection
    private val adsList = listOf(
        R.drawable.ad1,
        R.drawable.ad2,
        R.drawable.ad3,
        R.drawable.ad4
    )

    var randomAd = adsList.random()
        private set

    private var enableAds = false

    // GAME STATES - controls gameplay flow
    private var isPaused = false
    private var isSliding = false
    private var isGameOver = false
    private var isGameStarted = false

    // CALLBACKS - communicate with MainActivity
    var onGameOver: (() -> Unit)? = null
    var onShowAd: (() -> Unit)? = null
    var onShowCloseButton: (() -> Unit)? = null

    // JUMP SYSTEM - physics variables
    private var isJumping = false
    private var jumpOffset = 0f
    private var jumpVelocity = 0f
    private val gravity = 2f

    // SCORE SYSTEM - time based scoring
    private var score = 0
    private var lastScoreTime = System.currentTimeMillis()
    var onScoreChanged: ((Int) -> Unit)? = null

    var scoreMultiplier = 1

    // COIN SYSTEM - tracking coins
    private var coinX = 1800f
    private var coinY = 0f
    private var coins = 0
    var onCoinsChanged: ((Int) -> Unit)? = null

    // GAME SPEED - increases difficulty over time
    private var gameSpeed = 1f

    // OBSTACLE SYSTEM - movement and type
    private var obstacleX = 1400f
    private var obstacleType = 0

    // BACKGROUND OFFSET - scrolling effect
    private var bgOffset = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val groundTop = height - 220f

        // MOVE BACKGROUND - scrolling loop
        if (!isPaused && !isGameOver) {
            bgOffset -= 4f * gameSpeed
            if (bgOffset <= -width) bgOffset = 0f
        }

        val bg = Bitmap.createScaledBitmap(desertBg, width, height, false)
        canvas.drawBitmap(bg, bgOffset, 0f, null)
        canvas.drawBitmap(bg, bgOffset + width, 0f, null)

        // START SCREEN - dark overlay before game starts
        if (!isGameStarted) {
            canvas.drawARGB(150, 0, 0, 0)
            invalidate()
            return
        }

        // INITIAL COIN POSITION
        if (coinY == 0f) coinY = groundTop - 220f

        // SCORE SYSTEM - increases every second
        if (!isPaused && !isGameOver) {
            val now = System.currentTimeMillis()

            if (now - lastScoreTime >= 1000) {
                score++
                lastScoreTime = now
                onScoreChanged?.invoke(score)

                if (score % 10 == 0) gameSpeed += 0.2f
            }
        }

        // JUMP PHYSICS - gravity system
        if (isJumping && !isPaused && !isGameOver) {
            jumpOffset -= jumpVelocity
            jumpVelocity -= gravity

            if (jumpOffset >= 0f) {
                jumpOffset = 0f
                jumpVelocity = 0f
                isJumping = false
            }
        }

        // OBSTACLE MOVEMENT
        obstacleX -= 12f * gameSpeed

        // OBSTACLE RESET
        if (obstacleX < -150f) {
            obstacleX = width + 300f
            obstacleType = (0..1).random()
        }

        // DRAW OBSTACLES
        if (obstacleType == 0) {
            val bmp = Bitmap.createScaledBitmap(cactus, 190, 200, false)
            canvas.drawBitmap(bmp, obstacleX, groundTop - 190f, null)
        } else {
            val bmp = Bitmap.createScaledBitmap(bomb, 190, 130, false)
            canvas.drawBitmap(bmp, obstacleX, groundTop - 240f, null)
        }

        // PLAYER HITBOX - collision area
        val pLeft = 135f
        val pRight = 210f

        val pTop: Float
        val pBottom: Float

        if (isSliding) {
            pTop = groundTop - 85f
            pBottom = groundTop - 10f
        } else if (isJumping) {
            pTop = groundTop - 210f + jumpOffset
            pBottom = groundTop - 20f + jumpOffset
        } else {
            pTop = groundTop - 190f
            pBottom = groundTop - 20f
        }

        // OBSTACLE HITBOX
        val oLeft = obstacleX + 20f
        val oRight = if (obstacleType == 0) obstacleX + 85f else obstacleX + 70f
        val oTop = if (obstacleType == 0) groundTop - 190f else groundTop - 245f
        val oBottom = if (obstacleType == 0) groundTop else groundTop - 175f

        // COLLISION CHECK
        val hit =
            pRight > oLeft &&
                    pLeft < oRight &&
                    pBottom > oTop &&
                    pTop < oBottom

        if (hit && !isGameOver) {
            isGameOver = true
            enableAds = true
            randomAd = adsList.random()

            onShowAd?.invoke()
            onGameOver?.invoke()
        }

        // COIN MOVEMENT
        coinX -= 12f * gameSpeed

        // COIN RESET
        if (coinX < -100f) {
            coinX = width + 800f
            coinY = if ((0..1).random() == 0)
                groundTop - 220f
            else
                groundTop - 320f
        }

        // DRAW COIN
        val coinBmp = Bitmap.createScaledBitmap(coin, 130, 130, false)
        canvas.drawBitmap(coinBmp, coinX, coinY, null)

        // COIN COLLECTION
        val collected =
            pRight > coinX &&
                    pLeft < coinX + 130f &&
                    pBottom > coinY &&
                    pTop < coinY + 130f

        if (collected && !isGameOver) {
            coins++
            score += 10

            onCoinsChanged?.invoke(coins)
            onScoreChanged?.invoke(score)

            coinX = width + 800f
        }

        // DRAW PLAYER
        val x = 60f

        if (isSliding) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(girlSlide, 380, 240, false), x, groundTop - 240f + 30f, null)
        } else if (isJumping) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(girlJump, 320, 380, false), x, groundTop - 380f + 70f + jumpOffset, null)
        } else {
            canvas.drawBitmap(Bitmap.createScaledBitmap(girlRun, 290, 340, false), x, groundTop - 340f + 55f, null)
        }

        // GAME OVER SCREEN
        if (isGameOver) {
            val bmp = Bitmap.createScaledBitmap(gameOverBitmap, 700, 350, false)
            canvas.drawBitmap(bmp, width / 2f - 350f, height / 2f - 250f, null)
        }

        // PAUSE SCREEN
        if (isPaused && !isGameOver) {
            canvas.drawARGB(150, 0, 0, 0)
            val bmp = Bitmap.createScaledBitmap(pausedBitmap, 700, 350, false)
            canvas.drawBitmap(bmp, width / 2f - 350f, height / 2f - 250f, null)
        }

        invalidate()
    }

    // JUMP ACTION
    fun jump() {
        if (!isJumping && !isSliding && !isGameOver) {
            isJumping = true
            jumpVelocity = 35f
        }
    }

    // SLIDE ACTION
    fun slide() {
        if (!isJumping && !isGameOver) {
            isSliding = true
            postDelayed({ isSliding = false }, 1000)
        }
    }

    // PAUSE GAME
    fun pauseGame() {
        if (!isGameOver) isPaused = !isPaused
    }

    fun isPausedNow() = isPaused

    // START GAME
    fun startGame() {
        isGameStarted = true
        lastScoreTime = System.currentTimeMillis()
    }

    // RESTART GAME
    fun restartGame() {
        isGameOver = false
        score = 0
        coins = 0
        gameSpeed = 1f
        obstacleX = width + 300f
        coinX = width + 800f

        isJumping = false
        isSliding = false
        jumpOffset = 0f
        jumpVelocity = 0f

        enableAds = false
    }

    // CLOSE AD
    fun closeAd() {
        enableAds = false
    }

    // ADS LOGIC
    fun advertisment() {
        if (!enableAds) return

        val delayTime = when (randomAd) {
            R.drawable.ad1 -> 75000L
            R.drawable.ad2 -> 12000L
            else -> 16000L
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(delayTime)
            onShowCloseButton?.invoke()
        }
    }
}