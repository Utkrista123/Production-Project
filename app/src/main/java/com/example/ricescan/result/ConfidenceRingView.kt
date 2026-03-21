package com.example.ricescan.result

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator

class ConfidenceRingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var progress = 0f
    private var targetProgress = 0f
    private var confidenceText = "0%"

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 18f
        color = Color.parseColor("#1AFFFFFF")
    }

    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 18f
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 56f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#99FFFFFF")
        textAlign = Paint.Align.CENTER
        textSize = 24f
    }

    private val rectF = RectF()

    fun setConfidence(confidence: Float) {
        targetProgress = confidence * 100f
        confidenceText = "${(confidence * 100).toInt()}%"

        // Set color based on confidence level
        val color = when {
            confidence >= 0.75f -> Color.parseColor("#E74C3C") // Red — high confidence disease
            confidence >= 0.5f  -> Color.parseColor("#FFC107") // Yellow — medium
            else                -> Color.parseColor("#2ECC71")  // Green — low / healthy
        }
        ringPaint.color = color

        // Animate the ring filling up
        ValueAnimator.ofFloat(0f, targetProgress).apply {
            duration = 1200
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val radius = (minOf(width, height) / 2f) - 24f

        rectF.set(cx - radius, cy - radius, cx + radius, cy + radius)

        // Background ring
        canvas.drawArc(rectF, 0f, 360f, false, bgPaint)

        // Progress ring
        val sweepAngle = (progress / 100f) * 360f
        canvas.drawArc(rectF, -90f, sweepAngle, false, ringPaint)

        // Confidence percentage text
        canvas.drawText(confidenceText, cx, cy + textPaint.textSize / 3, textPaint)

        // "confidence" sub label
        canvas.drawText("confidence", cx, cy + textPaint.textSize / 3 + 36f, subTextPaint)
    }
}