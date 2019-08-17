package com.teamttdvlp.memolang.view.Activity

import android.animation.*
import android.transition.TransitionManager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.viewmodel.add_flashcard.AddFlashCardActivityViewModel
import com.teamttdvlp.memolang.databinding.ActivityAddFlashcardBinding
import com.teamttdvlp.memolang.view.Activity.helper.quickLog
import android.util.DisplayMetrics
import android.view.animation.AccelerateInterpolator
import com.teamttdvlp.memolang.R
import kotlin.math.sqrt


class AddFlashcardActivity : BaseActivity<ActivityAddFlashcardBinding, AddFlashCardActivityViewModel>() {

    private val VIEWGROUP_ANIM_DURATION = 500L
    private val SAVED_FLASHCARD_ANIM_DURATION = 500L

    override fun getLayoutId(): Int {
        return R.layout.activity_add_flashcard
    }

    override fun takeViewModel(): AddFlashCardActivityViewModel {
        return getActivityViewModel()
    }

    override fun initProperties() { dataBinding.apply {


    }}

    override fun addViewControls() { dataBinding.apply {
        btnSave.setOnClickListener {
            val animatorSet = AnimatorSet()

            val vgShrinkX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f)
            val vgShrinkY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f)
            val viewGroupScale = ObjectAnimator.ofPropertyValuesHolder(addFlashcardViewgroup, vgShrinkX, vgShrinkY).apply {
                duration = 400
            }.apply {
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}
                    override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {}
                    override fun onAnimationEnd(p0: Animator?) {}
                    override fun onAnimationCancel(p0: Animator?) {}
                    override fun onAnimationStart(p0: Animator?) {
                    }
                    override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                        imgSavedFlashcard.visibility = GONE
                    }
                })
            }

            val savedCardGrowX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1.2f, 1f)
            val savedCardGrowY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1.2f, 1f)
            val savedCardScale = ObjectAnimator.ofPropertyValuesHolder(imgSavedFlashcard, savedCardGrowX, savedCardGrowY)
                .apply {
                duration = 800
                startDelay = 400
                interpolator = AccelerateDecelerateInterpolator()
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}
                    override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {}
                    override fun onAnimationEnd(p0: Animator?) {}
                    override fun onAnimationCancel(p0: Animator?) {}
                    override fun onAnimationStart(p0: Animator?) {
                    }
                    override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                        imgSavedFlashcard.visibility = VISIBLE
                    }
                })
            }

            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            val savedCardMoveX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, width/1.5f)
            val savedCardFadeOut = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
            val savedCardGoOut = ObjectAnimator.ofPropertyValuesHolder(imgSavedFlashcard, savedCardMoveX, savedCardFadeOut)
                .apply {
                    startDelay = 600
                    duration = 1100
                    interpolator = FastOutSlowInInterpolator()
                    addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(p0: Animator?) {}
                        override fun onAnimationEnd(p0: Animator?) {}
                        override fun onAnimationCancel(p0: Animator?) {}
                        override fun onAnimationStart(p0: Animator?) {
                        }
                        override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                        }
                        override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                            finish()
                        }
                    })
                }
            animatorSet
                .play(viewGroupScale).with(savedCardScale)
                .before(savedCardGoOut)
            animatorSet.start()
        }
    }}

}

