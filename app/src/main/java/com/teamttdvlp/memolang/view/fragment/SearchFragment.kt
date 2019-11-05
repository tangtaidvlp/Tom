package com.teamttdvlp.memolang.view.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.room.Room

import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.FragmentSearchBinding
import com.teamttdvlp.memolang.model.sqlite.entity.FlashCardEntity
import com.teamttdvlp.memolang.model.sqlite.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.model.sqlite.entity.UserEntity
import com.teamttdvlp.memolang.view.helper.quickLog
import java.text.SimpleDateFormat

class SearchFragment : Fragment() {
    var TAG : String = "SEARCHFRAGMENT"
    lateinit var lnTextToBeTranslatedPropertiesBackup: ViewBackUpProperties
    lateinit var txtTranslatedTextPropertiesBackup: ViewBackUpProperties

    lateinit var mBinding: FragmentSearchBinding

    var isTxtTranslatedTextPopUp = false
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var data : MemoLangSqliteDataBase = Room.databaseBuilder(requireContext(), MemoLangSqliteDataBase::class.java, "MemoLangSqlite" )
            .allowMainThreadQueries()
            .build()
        var u = UserEntity()
        var card = FlashCardEntity()
        u.id = "23"
        card.createdAt = SimpleDateFormat("dd-MM-yyyy").parse("16-8-2019")
        card.kind = "en-vi"
        card.spelling = "\\computer\\"
        card.toBeTranslatedWord = "Computer"
        card.translatedWord = "Máy Tính"
        card.synonym = "Calculator"
        card.type = "N"
        card.using  = "Computer is the most magic of human ever"
        addControls(container)
        setup()
        addEvents()
        return mBinding.root
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun setup() {
        mBinding.btnClearText.focusable = View.NOT_FOCUSABLE

        txtTranslatedTextPropertiesBackup = ViewBackUpProperties(
            mBinding.txtTranslatedText.scaleX,
            mBinding.txtTranslatedText.scaleY,
            mBinding.txtTranslatedText.x,
            mBinding.txtTranslatedText.y
        )

        mBinding.lnTextToBeTranslated.scaleX = 0.9f
        mBinding.lnTextToBeTranslated.scaleY = 0.9f
        mBinding.supportTxtTranslatedWordAnimation.visibility = View.GONE
        mBinding.txtTranslatedText.visibility = View.GONE
    }

    fun addControls(container: ViewGroup?) {
        mBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_search, container, false)
    }

    fun addEvents() {
        mBinding.includeCard.layoutCardFrontSide.visibility = View.VISIBLE
        mBinding.includeCard.layoutCardBackSide.visibility = View.INVISIBLE
        mBinding.lnCard.setOnClickListener {
            mBinding.lnCard.isEnabled = false
            var alreadyVisibility = false
            var valueAnimator: ValueAnimator = ValueAnimator.ofFloat(it.scaleY, it.scaleY * -1f)
            valueAnimator.duration = 200
            valueAnimator.addUpdateListener { animator ->
                var value = animator.animatedValue as Float
                it.scaleY = value
                if (value < 0.15f && value > - 0.15f) {
                    if (!alreadyVisibility) {
                         alreadyVisibility = true
                        quickLog("flip: $value")
                        if (mBinding.includeCard.layoutCardFrontSide.visibility == View.VISIBLE) {
                            mBinding.includeCard.layoutCardFrontSide.visibility = View.INVISIBLE
                            mBinding.includeCard.layoutCardBackSide.visibility = View.VISIBLE
                        } else {
                            mBinding.includeCard.layoutCardFrontSide.visibility = View.VISIBLE
                            mBinding.includeCard.layoutCardBackSide.visibility = View.INVISIBLE
                        }
                    }
                }
            }
            valueAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    mBinding.lnCard.isEnabled = true
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }

            })
            valueAnimator.start()
        }

        mBinding.btnClearText.setOnClickListener {
            mBinding.edtTextToBeTranslated.text.clear()
        }

        mBinding.edtTextToBeTranslated.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onlnTextToBeTranslatedChanged()
            }
        })

        mBinding.edtTextToBeTranslated.setOnFocusChangeListener { view, focus ->
            if (focus) {
                lnTextToBeTranslatedFocusAnimation()
                if (!mBinding.edtTextToBeTranslated.text.isNullOrEmpty()) {
                    txtTranslatedTextPopUpAnim()
                    isTxtTranslatedTextPopUp = true
                }
            } else {
                lnTextToBeTranslatedUnFocusAnimation()
                txtTranslatedTextPopDownAnim(150)
                isTxtTranslatedTextPopUp = false
            }
        }
    }

    fun onlnTextToBeTranslatedChanged() {
        if (!isTxtTranslatedTextPopUp) {
            txtTranslatedTextPopUpAnim()
            isTxtTranslatedTextPopUp = true
        }
        if (mBinding.edtTextToBeTranslated.text.isNullOrEmpty()) {
            txtTranslatedTextPopDownAnim(0)
            isTxtTranslatedTextPopUp = false
        }
    }

    fun scaleAnim(view: View, duration: Long, scaleX: Array<Float>, scaleY: Array<Float>): Animator {
        var xUpHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, scaleX[0], scaleX[1])
        var yUpHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleY[0], scaleY[1])

        var scaleUpAnimation = ValueAnimator.ofPropertyValuesHolder(yUpHolder, xUpHolder)
        scaleUpAnimation.setDuration(duration)
        scaleUpAnimation.addUpdateListener {
            view.scaleX = it.getAnimatedValue(View.SCALE_X.name) as Float
            view.scaleY = it.getAnimatedValue(View.SCALE_Y.name) as Float
        }
        return scaleUpAnimation
    }

    fun txtTranslatedTextPopUpAnim() {
        mBinding.txtTranslatedText.visibility = View.VISIBLE
        mBinding.supportTxtTranslatedWordAnimation.visibility = View.VISIBLE
        mBinding.txtTranslatedText.elevation = 20f
        var animSet = AnimatorSet()
        animSet.play(scaleAnim(mBinding.txtTranslatedText, 300, arrayOf(0f, 1.2f), arrayOf(0f, 1.15f)))
            .before(scaleAnim(mBinding.txtTranslatedText, 170, arrayOf(1.2f, 1f), arrayOf(1.15f, 1f)))
        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {

            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

        })
        animSet.start()
    }

    fun txtTranslatedTextPopDownAnim (delay: Long) {
        var anim = scaleAnim(mBinding.txtTranslatedText, 300, arrayOf(1.2f, 0.1f), arrayOf(1.15f, 0.1f))
        anim.startDelay = delay
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                mBinding.txtTranslatedText.visibility = View.GONE
                mBinding.supportTxtTranslatedWordAnimation.visibility = View.GONE
                mBinding.txtTranslatedText.elevation = 0f
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })
        anim.start()
    }


    @TargetApi(Build.VERSION_CODES.O)
    fun lnTextToBeTranslatedFocusAnimation() {
        if (!::lnTextToBeTranslatedPropertiesBackup.isInitialized) {
            lnTextToBeTranslatedPropertiesBackup = ViewBackUpProperties(
                mBinding.lnTextToBeTranslated.scaleX,
                mBinding.lnTextToBeTranslated.scaleY,
                mBinding.lnTextToBeTranslated.x,
                mBinding.lnTextToBeTranslated.y
            )
        }

        var view = mBinding.lnTextToBeTranslated
        var animSet = AnimatorSet()
        animSet.play(moveYAnim(view, 20f, 200))
            .with(scaleAnim(view, 100, arrayOf(view.scaleX, 1f), arrayOf(view.scaleX, 1f)))
        view.elevation = 15f
        animSet.start()
    }

    fun lnTextToBeTranslatedUnFocusAnimation()  {
        var view = mBinding.lnTextToBeTranslated
        var animSet = AnimatorSet()
        animSet.play(moveYAnim(view, -20f, 200))
            .with(scaleAnim(view, 100, arrayOf(1f, 0.9f), arrayOf(1f, 0.9f)))
        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                // restore view properties, prevent it from wrong position when user run anim mutiple time
                view.x = lnTextToBeTranslatedPropertiesBackup.x
                view.y = lnTextToBeTranslatedPropertiesBackup.y
                view.scaleX = lnTextToBeTranslatedPropertiesBackup.scaleX
                view.scaleY = lnTextToBeTranslatedPropertiesBackup.scaleY
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

        })
        animSet.start()
        view.elevation = 0f
    }

    fun moveYAnim(view: View, offfSet: Float, duration: Long): ValueAnimator {
        var anim = ValueAnimator.ofFloat(0f, offfSet)
        anim.duration = duration
        var currentLocation = view.y
        anim.addUpdateListener {
            view.y = currentLocation + it.animatedValue as Float
        }
        return anim
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {
            }
    }

    class ViewBackUpProperties {
        var scaleX: Float = 1f
        var scaleY: Float = 1f
        var x: Float = 100f
        var y: Float = 0f

        constructor(scaleX: Float, scaleY: Float, x: Float, y: Float) {
            this.scaleX = scaleX
            this.scaleY = scaleY
            this.x = x
            this.y = y
        }
    }
}
