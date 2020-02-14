package com.teamttdvlp.memolang.view.customview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewPropertyAnimator
import android.widget.TextView
import androidx.annotation.Dimension.SP
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.helper.UnitConverter.DpToPixel

class CharacterCell  : TextView {

    val ANIM_DURATION = 150L

    companion object {
        val DIRECTION_UP = -1

        val DIRECTION_DOWN = -2
    }

    private var onRestore : ((delayTime : Long) -> Unit)? = null

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrSet : AttributeSet?) : super(context, attrSet) {

    }

    fun setOnRestore (onRestore : (delayTime : Long) -> Unit) {
        this.onRestore = onRestore
    }

    fun performOnRestore (delayTime: Long) {
        onRestore?.invoke(delayTime)
    }

    fun performDestroyAnimate(appearDirection: Int): ViewPropertyAnimator {
        val destinationY = if (appearDirection == DIRECTION_UP) {
            layoutParams.height * -1f
        } else if (appearDirection == DIRECTION_DOWN) {
            layoutParams.height * 1f
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
        background = context.getDrawable(R.drawable.round_3dp_light_red_background)
        elevation = DpToPixel(5).toFloat()
        setTextColor(Color.WHITE)
        setTextSize(SP, 20f)
        gravity = Gravity.CENTER
    }

}