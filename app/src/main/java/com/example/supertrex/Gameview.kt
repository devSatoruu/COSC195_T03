package com.example.supertrex

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // =========================
    // PAINT
    // =========================
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // =========================
    // GAME OVER PNG
    // =========================
    private val gameOverBitmap: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.game_over)

    // =========================
    // BACKGROUND PNG
    // =========================
    private val desertBg: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.desert_bg)

    // =========================
    // PLAYER PNG SPRITES
    // =========================
    private val girlRun: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.girl_run)

    private val girlSlide: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.girl_slide)

    private val girlJump: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.girl_jump)

    // =========================
    // OBSTACLE PNG SPRITES
    // =========================
    private val cactus: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.cactus)

    private val bomb: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.bomb)

    // =========================
    // COIN PNG
    // =========================
    private val coin: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.coin)

    // =========================
    // advertisement PNG
    // =========================
    //private val ads = listOf(
        //R.drawable.ad1
    //)

    // =========================
    // GAME STATES
    // =========================
    private var isPaused = false
    private var isSliding = false
    private var isGameOver = false

    var onGameOver: (() -> Unit)? = null

    // =========================
    // JUMP SYSTEM
    // =========================
    private var isJumping = false
    private var jumpOffset = 0f
    private var jumpVelocity = 0f
    private val gravity = 2f

    // =========================
    // SCORE SYSTEM
    // =========================
    private var score = 0
    private var lastScoreTime = System.currentTimeMillis()
    var onScoreChanged: ((Int) -> Unit)? = null

    // =========================
    // COIN SYSTEM
    // =========================
    private var coinX = 1800f
    private var coinY = 0f
    private var coins = 0
    var onCoinsChanged: ((Int) -> Unit)? = null

    // =========================
    // GAME SPEED
    // =========================
    private var gameSpeed = 1f

    // =========================
    // OBSTACLE SYSTEM
    // =========================
    private var obstacleX = 1400f
    private var obstacleType = 0 // 0 = cactus, 1 = bomb

    // =========================
    // MOVING BACKGROUND
    // =========================
    private var bgOffset = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // =========================
        // GROUND POSITION
        // =========================
        val groundTop = height - 220f

        // =========================
        // MOVE BACKGROUND
        // =========================
        if (!isPaused && !isGameOver) {
            bgOffset -= 4f * gameSpeed

            if (bgOffset <= -width.toFloat()) {
                bgOffset = 0f
            }
        }

        // =========================
        // DRAW LOOPING BACKGROUND PNG
        // =========================
        val bgBitmap = Bitmap.createScaledBitmap(
            desertBg,
            width,
            height,
            false
        )

        canvas.drawBitmap(
            bgBitmap,
            bgOffset,
            0f,
            null
        )

        canvas.drawBitmap(
            bgBitmap,
            bgOffset + width,
            0f,
            null
        )

        // =========================
        // INITIAL COIN HEIGHT
        // =========================
        if (coinY == 0f) {
            coinY = groundTop - 220f
        }

        // =========================
        // SCORE + SPEED SYSTEM
        // =========================
        if (!isPaused && !isGameOver) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastScoreTime >= 1000) {
                score++
                lastScoreTime = currentTime
                onScoreChanged?.invoke(score)

                if (score % 10 == 0) {
                    gameSpeed += 0.2f
                }
            }
        }

        // =========================
        // JUMP PHYSICS
        // =========================
        if (!isPaused && !isGameOver && isJumping) {
            jumpOffset -= jumpVelocity
            jumpVelocity -= gravity

            if (jumpOffset >= 0f) {
                jumpOffset = 0f
                jumpVelocity = 0f
                isJumping = false
            }
        }

        // =========================
        // MOVE OBSTACLE
        // =========================
        if (!isPaused && !isGameOver) {
            obstacleX -= 12f * gameSpeed
        }

        // =========================
        // RESET OBSTACLE
        // =========================
        if (obstacleX < -150f) {
            obstacleX = width + 300f
            obstacleType = (0..1).random()
        }

        // =========================
        // DRAW CACTUS OR BOMB
        // =========================
        if (obstacleType == 0) {
            val cactusBitmap = Bitmap.createScaledBitmap(
                cactus,
                190,
                200,
                false
            )

            canvas.drawBitmap(
                cactusBitmap,
                obstacleX,
                groundTop - 190f,
                null
            )

        } else {
            val bombBitmap = Bitmap.createScaledBitmap(
                bomb,
                190,
                130,
                false
            )

            canvas.drawBitmap(
                bombBitmap,
                obstacleX,
                groundTop - 240f,
                null
            )
        }

        // =========================
        // PLAYER HITBOX
        // =========================
        val playerLeft = 135f
        val playerRight = 210f

        val playerTop: Float
        val playerBottom: Float

        if (isSliding) {
            playerTop = groundTop - 85f
            playerBottom = groundTop - 10f
        } else if (isJumping) {
            playerTop = groundTop - 210f + jumpOffset
            playerBottom = groundTop - 20f + jumpOffset
        } else {
            playerTop = groundTop - 190f
            playerBottom = groundTop - 20f
        }

        // =========================
        // OBSTACLE HITBOX
        // =========================
        val obstacleLeft = obstacleX + 20f
        val obstacleRight =
            if (obstacleType == 0) obstacleX + 85f
            else obstacleX + 70f

        val obstacleTop =
            if (obstacleType == 0) groundTop - 190f
            else groundTop - 245f

        val obstacleBottom =
            if (obstacleType == 0) groundTop
            else groundTop - 175f

        // =========================
        // COLLISION DETECTION
        // =========================
        val isColliding =
            playerRight > obstacleLeft &&
                    playerLeft < obstacleRight &&
                    playerBottom > obstacleTop &&
                    playerTop < obstacleBottom

        if (isColliding && !isGameOver) {
            isGameOver = true
            onGameOver?.invoke()
        }

        // =========================
        // MOVE COIN
        // =========================
        if (!isPaused && !isGameOver) {
            coinX -= 12f * gameSpeed
        }

        if (coinX < -100f) {
            coinX = width + 800f
            coinY =
                if ((0..1).random() == 0)
                    groundTop - 220f
                else
                    groundTop - 320f
        }

        // =========================
        // DRAW COIN
        // =========================
        val coinBitmap = Bitmap.createScaledBitmap(
            coin,
            130,
            130,
            false
        )

        canvas.drawBitmap(
            coinBitmap,
            coinX,
            coinY,
            null
        )

        // =========================
        // COIN HITBOX
        // =========================
        val coinLeft = coinX
        val coinRight = coinX + 130f
        val coinTop = coinY
        val coinBottom = coinY + 130f

        // =========================
        // COIN COLLECTION
        // =========================
        val collectedCoin =
            playerRight > coinLeft &&
                    playerLeft < coinRight &&
                    playerBottom > coinTop &&
                    playerTop < coinBottom

        if (collectedCoin && !isGameOver) {
            coins += 1
            score += 10

            onCoinsChanged?.invoke(coins)
            onScoreChanged?.invoke(score)

            coinX = width + 800f
        }

        // =========================
        // DRAW PLAYER
        // =========================
        val playerX = 60f

        if (isSliding) {
            val slideBitmap = Bitmap.createScaledBitmap(
                girlSlide,
                380,
                240,
                false
            )

            canvas.drawBitmap(
                slideBitmap,
                playerX,
                groundTop - 240f + 30f,
                null
            )

        } else if (isJumping) {
            val jumpBitmap = Bitmap.createScaledBitmap(
                girlJump,
                320,
                380,
                false
            )

            canvas.drawBitmap(
                jumpBitmap,
                playerX,
                groundTop - 380f + 70f + jumpOffset,
                null
            )

        } else {
            val runBitmap = Bitmap.createScaledBitmap(
                girlRun,
                290,
                340,
                false
            )

            canvas.drawBitmap(
                runBitmap,
                playerX,
                groundTop - 340f + 55f,
                null
            )
        }

        // =========================
        // DRAW GAME OVER PNG
        // =========================
        if (isGameOver) {

            val scaledGameOver = Bitmap.createScaledBitmap(
                gameOverBitmap,
                700,
                350,
                false
            )

            canvas.drawBitmap(
                scaledGameOver,
                width / 2f - 350f,
                height / 2f - 250f,
                null
            )
        }

        // =========================
        // GAME LOOP
        // =========================
        invalidate()
    }

    // =========================
    // JUMP FUNCTION
    // =========================
    fun jump() {
        if (!isJumping && !isSliding && !isGameOver) {
            isJumping = true
            jumpVelocity = 35f
        }
    }

    // =========================
    // SLIDE FUNCTION
    // =========================
    fun slide() {
        if (!isJumping && !isGameOver) {
            isSliding = true

            postDelayed({
                isSliding = false
            }, 1000)
        }
    }

    // =========================
    // PAUSE FUNCTION
    // =========================
    fun pauseGame() {
        if (!isGameOver) {
            isPaused = !isPaused
        }
    }

    fun isPausedNow(): Boolean {
        return isPaused
    }

    // =========================
    // RESTART GAME
    // =========================
    fun restartGame() {
        isGameOver = false

        score = 0
        onScoreChanged?.invoke(score)

        coins = 0
        onCoinsChanged?.invoke(coins)

        gameSpeed = 1f

        obstacleX = width + 300f
        coinX = width + 800f

        isJumping = false
        isSliding = false
        jumpOffset = 0f
        jumpVelocity = 0f

        lastScoreTime = System.currentTimeMillis()
        bgOffset = 0f
    }
}