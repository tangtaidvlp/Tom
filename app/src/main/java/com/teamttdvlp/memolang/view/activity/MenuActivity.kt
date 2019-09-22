package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.net.ConnectivityManager
import android.view.animation.Animation
import androidx.core.animation.addListener
import androidx.core.net.ConnectivityManagerCompat
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityMenuBinding
import com.teamttdvlp.memolang.view.activity.base.BaseActivity
import com.teamttdvlp.memolang.view.activity.helper.*
import com.teamttdvlp.memolang.viewmodel.menu.MenuActivityViewModel
import kotlinx.android.synthetic.main.activity_menu.*
import javax.inject.Inject
import javax.inject.Named

class MenuActivity : BaseActivity<ActivityMenuBinding, MenuActivityViewModel>() {

    @field: Named("AppearThenDisappearAnimation")
    @Inject
    lateinit var showAnimation : Animation

    private var vgLogOutAppearAnimatorSet : AnimatorSet = AnimatorSet()

    private var vgLogOutDisappearAnimatorSet : AnimatorSet = AnimatorSet()


    override fun getLayoutId(): Int {
        return R.layout.activity_menu
    }

    override fun takeViewModel(): MenuActivityViewModel {
        return getActivityViewModel()
    }

    override fun addViewEvents() { dataBinding.apply {
        btn_add_flashcard.setOnClickListener {
            quickStartActivity(AddFlashcardActivity::class.java)
        }

        btn_learn_flashcard.setOnClickListener {
            quickStartActivity(LanguageActivity::class.java)
        }

        btn_search.setOnClickListener {
            quickStartActivity(SearchVocabularyActivity::class.java)
        }

        btnLogOut.setOnClickListener {
            vgLogOutAppearAnimatorSet.start()
            quickLog("ADIDAS")
        }

        btnLogOutInLogOutGroup.setOnClickListener {
            viewModel.clearUserInfomation()
        }

        imgBlackBackgroundSignOut.setOnClickListener {
            vgLogOutDisappearAnimatorSet.start()
        }
    }}

    override fun overrideEnterAnim() {
        overridePendingTransition(R.anim.from_right_to_centre, R.anim.from_centre_to_left)
    }

    override fun onStart() { dataBinding.apply {
        super.onStart()
        if (ConnectivityManagerCompat.isActiveNetworkMetered((getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager))) {
            txtOfflineModeNotification.startAnimation(showAnimation)
        }
    }}

    // ====================== INIT ANIMATIONS ===============

    @Inject
    fun initViewGroupLogOut_APPEAR_Animation (
        @Named("Menu_Appear50Percents") appear50PercentsAnimation : Animator,
        @Named("Menu_FromNothingToNormalSize") fromNothingToNormalSizeAnimation : Animator) { dataBinding.apply {

        appear50PercentsAnimation.setTarget(imgBlackBackgroundSignOut)
        fromNothingToNormalSizeAnimation.setTarget(viewgroupSignOut)
        vgLogOutAppearAnimatorSet.play(appear50PercentsAnimation).before(fromNothingToNormalSizeAnimation)
        vgLogOutAppearAnimatorSet.duration = 300
        vgLogOutAppearAnimatorSet.interpolator = FastOutLinearInInterpolator()
        vgLogOutAppearAnimatorSet.addListener (onStart = {
            groupSignOut.appear()
        })
    }}

    @Inject
    fun initViewGroupLogOut_DISAPPEAR_Animation (
        @Named("Menu_Disappear50Percents") disappear50PercentsAnimation : Animator,
        @Named("Menu_FromNormalSizeToNothing") fromNormalSizeToNothingAnimation : Animator) { dataBinding.apply {

        disappear50PercentsAnimation.setTarget(imgBlackBackgroundSignOut)
        fromNormalSizeToNothingAnimation.setTarget(viewgroupSignOut)
        vgLogOutDisappearAnimatorSet.play(fromNormalSizeToNothingAnimation).before(disappear50PercentsAnimation)
        vgLogOutDisappearAnimatorSet.duration = 300
        vgLogOutDisappearAnimatorSet.interpolator = FastOutLinearInInterpolator()
        vgLogOutDisappearAnimatorSet.addListener (onEnd = {
            groupSignOut.disappear()
        })
    }}
}
