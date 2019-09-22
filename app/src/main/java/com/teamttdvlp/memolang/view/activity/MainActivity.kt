package com.teamttdvlp.memolang.view.activity

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.activity.helper.addAnimationLister
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fadeIn = ObjectAnimator.ofFloat(txt_success, View.ALPHA, 0f, 1f).apply {
            duration = 250
            interpolator = LinearInterpolator()
        }

        val moveRightAndFadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.move_right_and_fade_out).apply {
            duration = 200
            interpolator = FastOutLinearInInterpolator()
            fillAfter = true
            addAnimationLister(onEnd = {
                fadeIn.start()
            })
        }

        val showAnimator = ObjectAnimator.ofFloat(success, View.ALPHA, 0f, 1f).apply {
            duration = 500
            interpolator = LinearInterpolator()
        }

        textView.setOnClickListener {
            progress_bar.transToActionMode()
        }

        progress_bar.setOnClickListener {
            progress_bar.transToEndMode {
                showAnimator.start()
                textView.startAnimation(moveRightAndFadeOutAnim)
                txt_success.alpha = 0f
            }
        }
    }
}