package com.teamttdvlp.memolang.view.customview

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.view.children
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.helper.goGONE
import com.teamttdvlp.memolang.view.helper.goVISIBLE

class MyCustomDialog (context : Context, attrSet : AttributeSet) : ConstraintLayout(context, attrSet) {

    private val showBackgroundAnmtr : Animator

    private val hideBackgroundAnmtr : Animator

    private val hideAnmtrList = ArrayList<Animator>()

    private val showAnmtrList = ArrayList<Animator>()

    private val view: ConstraintLayout

    private var onHide: (() -> Unit)? = null

    private var onShow: (() -> Unit)? = null

    private var onStartHide: (() -> Unit)? = null

    private var onStartShow: (() -> Unit)? = null

    private var ANIM_DURATION = 100L

    init {
        view = inflate(context, R.layout.my_custom_dialog, this) as ConstraintLayout
        showBackgroundAnmtr = AnimatorInflater.loadAnimator(context, R.animator.appear_100_percents)
        hideBackgroundAnmtr =
            AnimatorInflater.loadAnimator(context, R.animator.disappear_100_percents)
        showBackgroundAnmtr.setTarget(view)
        hideBackgroundAnmtr.setTarget(view)
        visibility = View.GONE

        showBackgroundAnmtr.addListener(
        onStart = {
            onStartShow?.invoke()
            goVISIBLE()
        },
            onEnd = {
                showAnmtrList.forEach {
                    it.start()
                    onShow?.invoke()
                }
            })

        hideBackgroundAnmtr.addListener(

            onStart = {
                onStartHide?.invoke()
            },
            onEnd = {
                goGONE()
                onHide?.invoke()
            })

        view.setOnClickListener {
            dismiss()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        children.forEachIndexed { index, view ->
            val isNotBlackBackground = index != 0
            if (isNotBlackBackground) {

                val hideAnimator =
                    AnimatorInflater.loadAnimator(context, R.animator.disappear_100_percents)
                val showAnimator =
                    AnimatorInflater.loadAnimator(context, R.animator.appear_100_percents)

                hideAnimator.duration = ANIM_DURATION
                showAnimator.duration = ANIM_DURATION

                hideAnimator.setTarget(view)
                hideAnimator.addListener(onEnd = {
                    view.goGONE()
                    if (index == children.count() - 1) {
                        hideBackgroundAnmtr.start()
                    }
                })
                hideAnmtrList.add(hideAnimator)

                showAnimator.setTarget(view)
                showAnimator.addListener (onStart = {
                    view.goVISIBLE()
                })

                showAnmtrList.add(showAnimator)

                view.alpha = 0f
            }
        }
    }

    fun dismiss() {
        hideAnmtrList.forEach {
            it.start()
        }
    }

    fun show() {
        showBackgroundAnmtr.start()
    }

    fun setOnHide(onHide: () -> Unit) {
        this.onHide = onHide
    }

    fun setOnStartHide(onStartHide: () -> Unit) {
        this.onStartHide = onStartHide
    }

    fun setOnStart(onStartShow: () -> Unit) {
        this.onStartShow = onStartShow
    }

    fun setOnShow(onShow: () -> Unit) {
        this.onShow = onShow
    }


    fun getAnimDuration(): Long {
        return ANIM_DURATION
    }

    fun setAnimDuration(duration: Long) {
        this.ANIM_DURATION = duration
    }
}