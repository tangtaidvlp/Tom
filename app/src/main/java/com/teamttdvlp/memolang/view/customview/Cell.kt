package com.teamttdvlp.memolang.view.customview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewPropertyAnimator
import androidx.annotation.Dimension.SP
import androidx.appcompat.widget.AppCompatTextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.helper.dp

class Cell  : AppCompatTextView {

    val ANIM_DURATION = 150L

    companion object {
        val DIRECTION_UP = -1

        val DIRECTION_DOWN = -2

        val INPUT_CELL = -3

        val OUTPUT_CELL = -4
    }

    private var onRestore : ((delayTime : Long) -> Unit)? = null

    constructor(context: Context, cellType : Int) : super(context) {
            background = context.getDrawable(R.drawable.background_app_round_1dp_app_puzzle_yellow)
            setTextColor(Color.BLACK)
    }

    constructor(context: Context, attrSet: AttributeSet?) : super(context, attrSet)

    fun setOnRestore (onRestore : (delayTime : Long) -> Unit) {
        this.onRestore = onRestore
    }

    fun performOnRestore (delayTime: Long) {
        onRestore?.invoke(delayTime)
    }

    fun performDestroyAnimate(appearDirection: Int): ViewPropertyAnimator {
        val destinationY : Float
        if (appearDirection == DIRECTION_UP) {
            destinationY = layoutParams.height * -1f
        } else if (appearDirection == DIRECTION_DOWN) {
            destinationY = layoutParams.height * 1f
        } else throw Exception("DIRECTION NOT FOUND $appearDirection")

        return animate().translationY(destinationY).alpha(0f)
            .setDuration(ANIM_DURATION).setInterpolator(
                FastOutSlowInInterpolator()
            )
    }

    fun performCreateAnimate(appearDirection : Int): ViewPropertyAnimator {
        translationY =  if (appearDirection == DIRECTION_UP) {
            layoutParams.height * 1f
        } else if (appearDirection == DIRECTION_DOWN) {
            layoutParams.height * -1f
        } else throw Exception("DIRECTION NOT FOUND $appearDirection")
        alpha = 0f
        return animate().translationY(0f).alpha(1f)
            .setDuration(ANIM_DURATION).setInterpolator(
                FastOutSlowInInterpolator()
            )

    }

    init {
        elevation = 1.dp().toFloat()
        setTextSize(SP, 17f)
        gravity = Gravity.CENTER
    }

}