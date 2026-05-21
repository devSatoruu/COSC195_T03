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

    // Paint object used for drawing shapes/colors
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // =========================
    // PLAYER PNG SPRITES
    // =========================

    // Running character image
    private val girlRun: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.girl_run)

    // Sliding character image
    private val girlSlide: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.girl_slide)

    // =========================
    // GAME STATES
    // =========================

    // Pause state
    private var isPaused = false

    // Sliding state
    private var isSliding = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Ground position
        val groundTop = height - 340f

        // =========================
        // SKY BACKGROUND
        // =========================

        // Main sky color
        canvas.drawColor(Color.rgb(232, 190, 130))

        // Desert haze overlay
        paint.color = Color.rgb(240, 213, 168)

        canvas.drawRect(
            0f,
            140f,
            width.toFloat(),
            height.toFloat(),
            paint
        )

        // =========================
        // SUN
        // =========================

        paint.color = Color.rgb(255, 212, 75)

        canvas.drawCircle(
            95f,
            90f,
            45f,
            paint
        )

        // =========================
        // FAR PYRAMID
        // =========================

        paint.color = Color.rgb(205, 175, 125)

        canvas.drawPath(Path().apply {
            moveTo(width * 0.52f, groundTop)
            lineTo(width * 0.68f, groundTop - 210f)
            lineTo(width * 0.85f, groundTop)
            close()
        }, paint)

        // =========================
        // MAIN PYRAMID
        // =========================

        paint.color = Color.rgb(198, 160, 88)

        canvas.drawPath(Path().apply {
            moveTo(width * 0.12f, groundTop)
            lineTo(width * 0.32f, groundTop - 330f)
            lineTo(width * 0.54f, groundTop)
            close()
        }, paint)

        // =========================
        // PYRAMID SHADOW
        // =========================

        paint.color = Color.rgb(150, 120, 72)

        canvas.drawPath(Path().apply {
            moveTo(width * 0.32f, groundTop - 330f)
            lineTo(width * 0.54f, groundTop)
            lineTo(width * 0.34f, groundTop)
            close()
        }, paint)

        // =========================
        // RIGHT PYRAMID
        // =========================

        paint.color = Color.rgb(190, 150, 88)

        canvas.drawPath(Path().apply {
            moveTo(width * 0.75f, groundTop)
            lineTo(width * 0.90f, groundTop - 250f)
            lineTo(width * 1.05f, groundTop)
            close()
        }, paint)

        // =========================
        // SAND DUNES
        // =========================

        paint.color = Color.rgb(218, 196, 125)

        canvas.drawOval(
            -100f,
            groundTop - 90f,
            width * 0.42f,
            groundTop + 35f,
            paint
        )

        canvas.drawOval(
            width * 0.25f,
            groundTop - 80f,
            width * 0.75f,
            groundTop + 35f,
            paint
        )

        canvas.drawOval(
            width * 0.65f,
            groundTop - 95f,
            width * 1.15f,
            groundTop + 35f,
            paint
        )

        // =========================
        // CLOUD
        // =========================

        paint.color = Color.WHITE

        canvas.drawOval(
            width * 0.52f,
            165f,
            width * 0.60f,
            210f,
            paint
        )

        // =========================
        // GROUND
        // =========================

        paint.color = Color.rgb(150, 105, 70)

        canvas.drawRect(
            0f,
            groundTop,
            width.toFloat(),
            height.toFloat(),
            paint
        )

        // =========================
        // SAND DETAIL LINES
        // =========================

        paint.color = Color.rgb(120, 80, 55)

        paint.strokeWidth = 3f

        canvas.drawLine(
            0f,
            groundTop + 70f,
            width.toFloat(),
            groundTop + 70f,
            paint
        )

        canvas.drawLine(
            0f,
            groundTop + 150f,
            width.toFloat(),
            groundTop + 150f,
            paint
        )

        // =========================
        // PLAYER CHARACTER
        // =========================

        if (isSliding) {

            // Draw sliding sprite
            val slideBitmap = Bitmap.createScaledBitmap(
                girlSlide,
                220,
                140,
                false
            )

            canvas.drawBitmap(
                slideBitmap,
                120f,
                groundTop - 140f,
                null
            )

        } else {

            // Draw running sprite
            val runBitmap = Bitmap.createScaledBitmap(
                girlRun,
                180,
                220,
                false
            )

            canvas.drawBitmap(
                runBitmap,
                120f,
                groundTop - 220f,
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
        // jump code later
    }

    // =========================
    // SLIDE FUNCTION
    // =========================

    fun duck() {

        // Turn on slide
        isSliding = true

        // Turn off slide after 0.5 seconds
        postDelayed({
            isSliding = false
        }, 500)
    }

    // =========================
    // PAUSE FUNCTION
    // =========================

    fun pauseGame() {
        isPaused = !isPaused
    }
}