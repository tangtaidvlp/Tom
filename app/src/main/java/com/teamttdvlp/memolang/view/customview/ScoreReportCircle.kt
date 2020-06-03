package com.teamttdvlp.memolang.view.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.helper.dp
import kotlin.math.min

class ScoreReportCircle(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val greenPaint: Paint = Paint()

    private val redPaint: Paint = Paint()

    private val rect = RectF()

    private val commonStrokeWidth = 12.dp().toFloat()

    init {
        greenPaint.color = context.resources.getColor(R.color.use_flashcard_done_green)
        greenPaint.strokeCap = Paint.Cap.SQUARE
        greenPaint.strokeWidth = commonStrokeWidth
        greenPaint.style = Paint.Style.STROKE
        greenPaint.isAntiAlias = true

        redPaint.color = context.resources.getColor(R.color.use_flashcard_done_red)
        redPaint.strokeCap = Paint.Cap.SQUARE
        redPaint.strokeWidth = commonStrokeWidth
        redPaint.style = Paint.Style.STROKE
        redPaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val squareDimension = min(width, height)
        rect.left = commonStrokeWidth / 2
        rect.top = commonStrokeWidth / 2
        rect.right = width.toFloat() - commonStrokeWidth / 2
        rect.bottom = height.toFloat() - commonStrokeWidth / 2
        setMeasuredDimension(squareDimension, squareDimension)
    }

    private var positiveRatio = 0f
    private var negativeRatio = 0f

    private var positiveProgress = 0f
    private var negativeProgress = 0f

    private var totalPositiveAngle = 0f
    private var totalNegativeAngle = 0f

    private val ANIMATION_DURATION: Long = 600

    fun setProgressesValue(positiveRatio: Float, negativeRatio: Float = 1f - positiveRatio) {
        if ((positiveRatio + negativeRatio) != 1f) {
            throw Exception("Sum of Green progress and Red progress must be 100%")
        }
        this.positiveRatio = positiveRatio
        this.negativeRatio = negativeRatio

        totalPositiveAngle = this.positiveRatio * 360f
        totalNegativeAngle = 360f - totalPositiveAngle
    }

    private var onPositiveProgressChange: ((Float) -> Unit)? = null
    private var onNegativeProgressChange: ((Float) -> Unit)? = null
    private var onDrawAlmostEnd: (() -> Unit)? = null

    fun addOnPositiveProgressChangeListener(onChange: (Float) -> Unit) {
        this.onPositiveProgressChange = onChange
    }

    fun addOnNegativeProgressChangeListener(onChange: (Float) -> Unit) {
        this.onNegativeProgressChange = onChange
    }

    fun addOnDrawAlmostEndListener(onAlmostEnd: () -> Unit) {
        onDrawAlmostEnd = onAlmostEnd
    }

    fun startDrawing(startDelay: Long = 0) {
        val drawGreenArcAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                if ((it.animatedValue as Float) <= positiveRatio) {
                    positiveProgress = (it.animatedValue as Float) / positiveRatio
                    onPositiveProgressChange?.invoke(positiveProgress)
                } else {
                    negativeProgress = ((it.animatedValue as Float) - positiveRatio) / negativeRatio
                    onNegativeProgressChange?.invoke(positiveProgress)
                }
                if (it.animatedFraction > 0.92) {
                    onDrawAlmostEnd?.invoke()
                }
                invalidate()
            }
            this.startDelay = startDelay
            duration = ANIMATION_DURATION
            interpolator = FastOutSlowInInterpolator()
            start()
        }

    }

    override fun onDraw(canvas: Canvas?) {
        if (negativeProgress > 0f) {
            canvas!!.drawArc(rect, 90f, totalPositiveAngle, false, greenPaint)
            canvas.drawArc(
                rect,
                totalPositiveAngle - 270f,
                negativeProgress * totalNegativeAngle,
                false,
                redPaint
            )
        } else {
            canvas!!.drawArc(rect, 90f, positiveProgress * totalPositiveAngle, false, greenPaint)
        }
    }
}