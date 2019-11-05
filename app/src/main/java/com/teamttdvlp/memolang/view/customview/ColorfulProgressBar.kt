package com.teamttdvlp.memolang.view.customview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.Gravity.CENTER
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.core.animation.addListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.helper.quickLog
import kotlin.math.*

private const val UNVALUED = -1


class ColorfulProgressBar : LinearLayout {

    private var radius : Int = UNVALUED

    private var progressBar : ColorfulImage = ColorfulImage(context)

    constructor(context: Context?, attrs : AttributeSet) : super(context!!, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorfulProgressBar, 0, 0)
        radius = typedArray.getDimension(R.styleable.ColorfulProgressBar_radius, UNVALUED.toFloat()).toInt()
        addView(progressBar, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }

    constructor(context: Context?, attrs : AttributeSet, deff : Int) : super(context!!, attrs, deff) {

    }

    constructor(context: Context?, attrs:AttributeSet, deff: Int, deffS : Int) : super (context!!, attrs, deff, deffS) {

    }

    init {
        orientation = VERTICAL
        gravity = CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = min(widthMeasureSpec, heightMeasureSpec)
        if ((radius == UNVALUED) or (radius > width)) radius = width
        progressBar.actionRadius = radius
        super.onMeasure(width, width)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

    }

    fun transToActionMode () {
        progressBar.transToActionMode()
    }

    fun transToEndMode (onEnd : () -> Unit) {
        progressBar.transToEndMode(onEnd)
    }

    fun transToStaticMode() {
        progressBar.transToStaticMode()
    }

    fun getEndAnim () : Animator {
        return progressBar.endAnimator
    }
}

class ColorfulImage : View {

    var actionRadius = 0

    private val sin60 = sin(PI * 60 / 180).toFloat()

    private val cos60 = cos(PI * 60 / 180).toFloat()

    private val sin30 = sin(PI * 30 / 180).toFloat()

    private val cos30 = cos(PI * 30 / 180).toFloat()

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
    }

    private var dot1 : Bitmap
    private var dot2 : Bitmap
    private var dot3 : Bitmap

    private var dot_width = 0f

    private var haftWidth = 0f

    private var end = false

    private var rotateAnim : Animation = AnimationUtils.loadAnimation(context, R.anim.rotate_forever).apply {
        duration = 650
        interpolator = FastOutSlowInInterpolator()
    }

    var actionProgress = 0f

    var endProgress = 0f

    val action_duration = 250L

    var actionToEndDistance = 0

    val actionAnimator = ValueAnimator.ofFloat(0f, 1f)

    val endAnimator = AnimatorSet()

    private val fadeOutAnimator = ObjectAnimator.ofFloat(this, ALPHA, 1f, 0f)

    var totalDotWidth = 0f

    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, attrs : AttributeSet) : super(context!!, attrs)

    constructor(context: Context?, attrs : AttributeSet, deff : Int) : super(context!!, attrs, deff)

    init {
        dot1 = BitmapFactory.decodeResource(context.resources, R.drawable.image_yellow_dot)
        dot2 = BitmapFactory.decodeResource(context.resources, R.drawable.image_red_dot)
        dot3 = BitmapFactory.decodeResource(context.resources, R.drawable.image_blue_dot)

        dot_width = dot1.width.toFloat()

        totalDotWidth = dot_width * 3

        actionAnimator.apply {
            duration = action_duration
            startDelay = 200L
            var hasRotated = false
            addUpdateListener {
                actionProgress = it.animatedValue as Float
                invalidate()
                if ((actionProgress >= 0.6) and !hasRotated) {
                    startAnimation(rotateAnim)
                    hasRotated = true
                }
            }
            setTarget(this)
        }


        val fadeOutAnimator = ObjectAnimator.ofFloat(this, ALPHA, 1f, 0f)

        val endAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 350L
            startDelay = 100L
            interpolator = AccelerateInterpolator()
            addUpdateListener {
                endProgress = it.animatedValue as Float
                invalidate()
            }
            addListener (onStart = {
                end = true
            })
            setTarget(this)
        }

        this.endAnimator.play(endAnimator).with(fadeOutAnimator)

        fadeOutAnimator.apply {
            duration = 350L
            startDelay = 100L
            interpolator = LinearInterpolator()
        }
    }

    fun transToStaticMode () {
        end = false
        actionProgress = 0f
        endProgress = 0f
        alpha = 1f
        invalidate()
    }

    fun transToActionMode () {
        end = false
        actionAnimator.start()
    }

    fun transToEndMode (onAlmostEnd: () -> Unit) {
        end = true
        endAnimator.start()
        fadeOutAnimator.addListener (onEnd = {
            onAlmostEnd()
        })
        fadeOutAnimator.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(width, width)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        haftWidth = (width/2).toFloat()

        x1 = (width - totalDotWidth) / 2
        y1 = (height - dot_width) / 2
        x2 = x1 + dot_width
        y2 = y1
        x3 = x2 + dot_width
        y3 = y1

        val specialX = haftWidth + haftWidth * sin60
        val specialY = haftWidth + haftWidth * cos60

        val offsetX = (dot_width/2) * cos30
        val offsetY = (dot_width/2) * sin30

        actionToEndDistance = abs(width - actionRadius)

        quickLog("Radius: $width")
        quickLog("Action Radius: $actionRadius")
        quickLog("Distance: $actionToEndDistance")

        offsetActionToEnd_x1 = 0f
        offsetActionToEnd_y1 = actionToEndDistance.toFloat()
        offsetActionToEnd_x2 = - actionToEndDistance * sin60
        offsetActionToEnd_y2 = - actionToEndDistance * cos60
        offsetActionToEnd_x3 = actionToEndDistance * sin60
        offsetActionToEnd_y3 = - actionToEndDistance * cos60

        action_x1 = (width - dot_width)/2 + offsetActionToEnd_x1
        action_y1 = - dot_width/2 + dot_width/2 + offsetActionToEnd_y1
        action_x2 = specialX - dot_width/2 - offsetX + offsetActionToEnd_x2
        action_y2 = specialY - dot_width/2 - offsetY + offsetActionToEnd_y2
        action_x3 = (width - specialX) - dot_width/2 + offsetX + offsetActionToEnd_x3
        action_y3 = specialY - dot_width/2 - offsetY + offsetActionToEnd_y3

        action_offset_x1 =  action_x1 - x1
        action_offset_y1 =  action_y1 - y1
        action_offset_x2 =  action_x2 - x2
        action_offset_y2 =  action_y2 - y2
        action_offset_x3 =  action_x3 - x3
        action_offset_y3 =  action_y3 - y3
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (!end) {
            canvas!!.drawBitmap(dot1, x1 + action_offset_x1 * actionProgress, y1 + action_offset_y1 * actionProgress, paint)
            canvas.drawBitmap(dot2, x2 + action_offset_x2 * actionProgress, y2 + action_offset_y2 * actionProgress, paint)
            canvas.drawBitmap(dot3, x3 + action_offset_x3 * actionProgress, y3 + action_offset_y3 * actionProgress, paint)
        } else {
            canvas!!.drawBitmap(dot1,action_x1 - offsetActionToEnd_x1 * endProgress, action_y1 - offsetActionToEnd_y1 * endProgress, paint)
            canvas.drawBitmap(dot2, action_x2 - offsetActionToEnd_x2 * endProgress, action_y2 - offsetActionToEnd_y2 * endProgress, paint)
            canvas.drawBitmap(dot3, action_x3 - offsetActionToEnd_x3 * endProgress, action_y3 - offsetActionToEnd_y3 * endProgress, paint)
        }
    }

    var x1 = 0f
    var y1 = 0f
    var x2 = 0f
    var y2 = 0f
    var x3 = 0f
    var y3 = 0f

    var action_offset_x1 = 0f
    var action_offset_y1 = 0f
    var action_offset_x2 = 0f
    var action_offset_y2 = 0f
    var action_offset_x3 = 0f
    var action_offset_y3 = 0f

    var action_x1 = 0f
    var action_y1 = 0f
    var action_x2 = 0f
    var action_y2 = 0f
    var action_x3 = 0f
    var action_y3 = 0f

    var offsetActionToEnd_x1 = 0f
    var offsetActionToEnd_y1 = 0f
    var offsetActionToEnd_x2 = 0f
    var offsetActionToEnd_y2 = 0f
    var offsetActionToEnd_x3 = 0f
    var offsetActionToEnd_y3 = 0f

}


