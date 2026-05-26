package com.example.supertrex

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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
        val groundTop = height - 340f

        // =========================
        // MOVE BACKGROUND
        // =========================
        if (!isPaused && !isGameOver) {
            bgOffset -= 2f * gameSpeed

            if (bgOffset <= -width.toFloat()) {
                bgOffset = 0f
            }
        }

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
        // SKY BACKGROUND
        // =========================
        canvas.drawColor(Color.rgb(232, 190, 130))

        // Desert haze
        paint.color = Color.rgb(240, 213, 168)
        canvas.drawRect(0f, 140f, width.toFloat(), height.toFloat(), paint)

        // =========================
        // SUN
        // =========================
        paint.color = Color.rgb(255, 212, 75)
        canvas.drawCircle(95f, 90f, 45f, paint)

        // =========================
        // FAR PYRAMIDS
        // =========================
        paint.color = Color.rgb(205, 175, 125)

        canvas.drawPath(Path().apply {
            moveTo(bgOffset + width * 0.52f, groundTop)
            lineTo(bgOffset + width * 0.68f, groundTop - 210f)
            lineTo(bgOffset + width * 0.85f, groundTop)
            close()
        }, paint)

        canvas.drawPath(Path().apply {
            moveTo(bgOffset + width + width * 0.52f, groundTop)
            lineTo(bgOffset + width + width * 0.68f, groundTop - 210f)
            lineTo(bgOffset + width + width * 0.85f, groundTop)
            close()
        }, paint)

        // =========================
        // MAIN PYRAMIDS
        // =========================
        paint.color = Color.rgb(198, 160, 88)

        canvas.drawPath(Path().apply {
            moveTo(bgOffset + width * 0.12f, groundTop)
            lineTo(bgOffset + width * 0.32f, groundTop - 330f)
            lineTo(bgOffset + width * 0.54f, groundTop)
            close()
        }, paint)

        canvas.drawPath(Path().apply {
            moveTo(bgOffset + width + width * 0.12f, groundTop)
            lineTo(bgOffset + width + width * 0.32f, groundTop - 330f)
            lineTo(bgOffset + width + width * 0.54f, groundTop)
            close()
        }, paint)

        // =========================
        // PYRAMID SHADOWS
        // =========================
        paint.color = Color.rgb(150, 120, 72)

        canvas.drawPath(Path().apply {
            moveTo(bgOffset + width * 0.32f, groundTop - 330f)
            lineTo(bgOffset + width * 0.54f, groundTop)
            lineTo(bgOffset + width * 0.34f, groundTop)
            close()
        }, paint)

        canvas.drawPath(Path().apply {
            moveTo(bgOffset + width + width * 0.32f, groundTop - 330f)
            lineTo(bgOffset + width + width * 0.54f, groundTop)
            lineTo(bgOffset + width + width * 0.34f, groundTop)
            close()
        }, paint)

        // =========================
        // MOVING SAND DUNES
        // =========================
        paint.color = Color.rgb(224, 198, 128)

        // Back dunes only
        canvas.drawOval(
            bgOffset - 200f,
            groundTop - 40f,
            bgOffset + width * 0.30f,
            groundTop + 35f,
            paint
        )

        canvas.drawOval(
            bgOffset + width * 0.45f,
            groundTop - 50f,
            bgOffset + width * 0.85f,
            groundTop + 35f,
            paint
        )

        canvas.drawOval(
            bgOffset + width - 200f,
            groundTop - 40f,
            bgOffset + width + width * 0.30f,
            groundTop + 35f,
            paint
        )

        canvas.drawOval(
            bgOffset + width + width * 0.45f,
            groundTop - 50f,
            bgOffset + width + width * 0.85f,
            groundTop + 35f,
            paint
        )

        // =========================
        // CLOUD
        // =========================
        paint.color = Color.WHITE
        canvas.drawOval(width * 0.52f, 165f, width * 0.60f, 210f, paint)

        // =========================
        // GROUND
        // =========================
        paint.color = Color.rgb(210, 180, 110)
        canvas.drawRect(0f, groundTop, width.toFloat(), height.toFloat(), paint)

        // =========================
        // GROUND DETAIL LINES
        // =========================
        paint.color = Color.rgb(180, 150, 95)
        paint.strokeWidth = 3f

        canvas.drawLine(
            bgOffset,
            groundTop + 70f,
            bgOffset + width,
            groundTop + 70f,
            paint
        )

        canvas.drawLine(
            bgOffset + width,
            groundTop + 70f,
            bgOffset + width * 2,
            groundTop + 70f,
            paint
        )

        canvas.drawLine(
            bgOffset,
            groundTop + 150f,
            bgOffset + width,
            groundTop + 150f,
            paint
        )

        canvas.drawLine(
            bgOffset + width,
            groundTop + 150f,
            bgOffset + width * 2,
            groundTop + 150f,
            paint
        )

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
            val cactusBitmap = Bitmap.createScaledBitmap(cactus, 190, 200, false)
            canvas.drawBitmap(cactusBitmap, obstacleX, groundTop - 190f, null)
        } else {
            val bombBitmap = Bitmap.createScaledBitmap(bomb, 190, 130, false)
            canvas.drawBitmap(bombBitmap, obstacleX, groundTop - 240f, null)
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
            coinY = if ((0..1).random() == 0) groundTop - 220f else groundTop - 320f
        }

        // =========================
        // DRAW COIN
        // =========================
        val coinBitmap = Bitmap.createScaledBitmap(coin, 130, 130, false)
        canvas.drawBitmap(coinBitmap, coinX, coinY, null)

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
            coins++
            score += 10

            onCoinsChanged?.invoke(coins)
            onScoreChanged?.invoke(score)

            coinX = width + 800f
        }

        // =========================
        // DRAW PLAYER
        // =========================
        val playerX = 80f

        if (isSliding) {
            val slideBitmap = Bitmap.createScaledBitmap(girlSlide, 320, 200, false)
            canvas.drawBitmap(slideBitmap, playerX, groundTop - 200f + 20f, null)
        } else if (isJumping) {
            val jumpBitmap = Bitmap.createScaledBitmap(girlJump, 230, 280, false)
            canvas.drawBitmap(jumpBitmap, playerX, groundTop - 280f + 45f + jumpOffset, null)
        } else {
            val runBitmap = Bitmap.createScaledBitmap(girlRun, 230, 280, false)
            canvas.drawBitmap(runBitmap, playerX, groundTop - 280f + 45f, null)
        }

        // =========================
        // GAME OVER TEXT
        // =========================
        if (isGameOver) {
            paint.color = Color.RED
            paint.textSize = 90f
            paint.strokeWidth = 4f

            canvas.drawText("GAME OVER", width / 2f - 260f, height / 2f, paint)
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
    fun duck() {
        if (!isJumping && !isGameOver) {
            isSliding = true

            postDelayed({
                isSliding = false
            }, 500)
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