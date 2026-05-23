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

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Running character image
    private val girlRun: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.girl_run)

    // Sliding character image
    private val girlSlide: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.girl_slide)

    // Jumping character image
    private val girlJump: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.girl_jump)

    private var isPaused = false
    private var isSliding = false

    // Jump state
    private var isJumping = false
    private var jumpOffset = 0f
    private var jumpVelocity = 0f
    private val gravity = 2f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val groundTop = height - 340f

        // =========================
        // JUMP PHYSICS
        // =========================
        if (!isPaused && isJumping) {
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

        paint.color = Color.rgb(240, 213, 168)
        canvas.drawRect(0f, 140f, width.toFloat(), height.toFloat(), paint)

        // SUN
        paint.color = Color.rgb(255, 212, 75)
        canvas.drawCircle(95f, 90f, 45f, paint)

        // FAR PYRAMID
        paint.color = Color.rgb(205, 175, 125)
        canvas.drawPath(Path().apply {
            moveTo(width * 0.52f, groundTop)
            lineTo(width * 0.68f, groundTop - 210f)
            lineTo(width * 0.85f, groundTop)
            close()
        }, paint)

        // MAIN PYRAMID
        paint.color = Color.rgb(198, 160, 88)
        canvas.drawPath(Path().apply {
            moveTo(width * 0.12f, groundTop)
            lineTo(width * 0.32f, groundTop - 330f)
            lineTo(width * 0.54f, groundTop)
            close()
        }, paint)

        // PYRAMID SHADOW
        paint.color = Color.rgb(150, 120, 72)
        canvas.drawPath(Path().apply {
            moveTo(width * 0.32f, groundTop - 330f)
            lineTo(width * 0.54f, groundTop)
            lineTo(width * 0.34f, groundTop)
            close()
        }, paint)

        // RIGHT PYRAMID
        paint.color = Color.rgb(190, 150, 88)
        canvas.drawPath(Path().apply {
            moveTo(width * 0.75f, groundTop)
            lineTo(width * 0.90f, groundTop - 250f)
            lineTo(width * 1.05f, groundTop)
            close()
        }, paint)

        // SAND DUNES
        paint.color = Color.rgb(218, 196, 125)
        canvas.drawOval(-100f, groundTop - 90f, width * 0.42f, groundTop + 35f, paint)
        canvas.drawOval(width * 0.25f, groundTop - 80f, width * 0.75f, groundTop + 35f, paint)
        canvas.drawOval(width * 0.65f, groundTop - 95f, width * 1.15f, groundTop + 35f, paint)

        // CLOUD
        paint.color = Color.WHITE
        canvas.drawOval(width * 0.52f, 165f, width * 0.60f, 210f, paint)

        // GROUND
        paint.color = Color.rgb(150, 105, 70)
        canvas.drawRect(0f, groundTop, width.toFloat(), height.toFloat(), paint)

        // SAND DETAIL LINES
        paint.color = Color.rgb(120, 80, 55)
        paint.strokeWidth = 3f
        canvas.drawLine(0f, groundTop + 70f, width.toFloat(), groundTop + 70f, paint)
        canvas.drawLine(0f, groundTop + 150f, width.toFloat(), groundTop + 150f, paint)

        // =========================
// PLAYER CHARACTER
// =========================

        if (isSliding) {

            // Slide sprite
            val slideBitmap = Bitmap.createScaledBitmap(
                girlSlide,
                320,
                200,
                false
            )

            canvas.drawBitmap(
                slideBitmap,
                80f,
                groundTop - 200f,
                null
            )

        } else if (isJumping) {

            // Jump sprite
            val jumpBitmap = Bitmap.createScaledBitmap(
                girlJump,
                280,
                340,
                false
            )

            canvas.drawBitmap(
                jumpBitmap,
                80f,
                groundTop - 340f + jumpOffset,
                null
            )

        } else {
            // Run sprite
            val runBitmap = Bitmap.createScaledBitmap(
                girlRun,
                280,
                340,
                false
            )

            canvas.drawBitmap(
                runBitmap,
                80f,
                groundTop - 340f,
                null
            )
        }

        invalidate()
    }

    fun jump() {
        if (!isJumping && !isSliding) {
            isJumping = true
            jumpVelocity = 35f
        }
    }

    fun duck() {
        if (!isJumping) {
            isSliding = true

            postDelayed({
                isSliding = false
            }, 500)
        }
    }

    fun pauseGame() {
        isPaused = !isPaused
    }
}