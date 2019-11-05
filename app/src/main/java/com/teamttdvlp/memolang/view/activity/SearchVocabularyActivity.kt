package com.teamttdvlp.memolang.view.activity

import android.animation.*
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivitySearchVocabularyBinding
import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.model.model.Language
import com.teamttdvlp.memolang.model.sqlite.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.model.sqlite.repository.FlashcardRepository
import com.teamttdvlp.memolang.model.sqlite.repository.UserSearchHistoryRepository
import com.teamttdvlp.memolang.view.adapter.ChooseLanguageRCVAdapter
import com.teamttdvlp.memolang.view.adapter.RecentSearchFlashcardRCVAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
import com.teamttdvlp.memolang.viewmodel.search_vocabulary.SearchVocabularyActivityViewModel
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class SearchVocabularyActivity : BaseActivity<ActivitySearchVocabularyBinding, SearchVocabularyActivityViewModel>() {

    lateinit var moveRightAndFadeOutAnim : Animation

    lateinit var moveLeftAndFadeOutAnim : Animation

    var startSearchingAnimatorSet : AnimatorSet = AnimatorSet()

    var endSearchingAnimatorSet : AnimatorSet = AnimatorSet()

    var startSearchingWithTranslationAnimatorSet : AnimatorSet = AnimatorSet()

    var endSearchingWithTranslationAnimatorSet : AnimatorSet = AnimatorSet()

    private val animatorSetChooseLangAppear = AnimatorSet()

    private val animatorSetChooseLangDisappear = AnimatorSet()

    private var selectedLanguageSessionTextView : TextView? = null

    lateinit var btnAddAppearAnimator : Animator

    lateinit var btnAddDisappearAnimator : Animator

    lateinit var rcvRecentSearchFlashcardAdapter: RecentSearchFlashcardRCVAdapter
    @Inject set

    lateinit var rcvChooseLanguageAdapter: ChooseLanguageRCVAdapter
    @Inject set

    lateinit var linearLayoutManager: LinearLayoutManager
    @Inject set

    lateinit var linearLayoutManager2: LinearLayoutManager
    @Inject set

    lateinit var databaseManager: MemoLangSqliteDataBase
    @Inject set

    lateinit var onlineFlashCardMainActivity: OnlineFlashcardDBManager
    @Inject set

    lateinit var flashcardRepository : FlashcardRepository
    @Inject set

    lateinit var searchHistoryRepository: UserSearchHistoryRepository
    @Inject set

    private var isInSearchingMode = false

    private var isInChooseLanguageMode= false

    private var addButtonDoesAppear = false

    override fun initProperties() { dataBinding.apply {

    }}

    override fun getLayoutId(): Int  = R.layout.activity_search_vocabulary

    override fun takeViewModel(): SearchVocabularyActivityViewModel = getActivityViewModel() {
        SearchVocabularyActivityViewModel(databaseManager, onlineFlashCardMainActivity, flashcardRepository, searchHistoryRepository)
    }

    override fun addViewControls() { dataBinding.apply {
        rcvRecentSearchFlashcards.adapter = rcvRecentSearchFlashcardAdapter
        rcvRecentSearchFlashcards.layoutManager = linearLayoutManager

        rcvChooseSourceLanguage.adapter = rcvChooseLanguageAdapter
        rcvChooseSourceLanguage.layoutManager = linearLayoutManager2
    }}

    override fun addViewEvents() { dataBinding.apply {
        edtText.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                if  (txtTranslation.text.toString() == "") {
                    startSearchingAnimatorSet.start()
                } else {
                    startSearchingWithTranslationAnimatorSet.start()
                }
            }
        }

        edtText.addTextChangeListener (onTextChanged = { text, _, _, _ ->
            if (text.toString() != "") {
                val sourceLang = Language.getLanguageSign(txtSourceLang.text.toString())
                val targetLang = Language.getLanguageSign(txtTargetLang.text.toString())
                val needTranslatingText = edtText.text.toString()
                viewModel.search(sourceLang!!, targetLang!!, needTranslatingText, onSuccess = {
                    if (!addButtonDoesAppear) {
                        btnAddAppearAnimator.start()
                    }
                }, onFailed = {errorText ->
                    quickToast("Some error happened. $errorText")
                })
            } else {
                btnAddDisappearAnimator.start()
            }
            txtTranslation.text = text
        })

        viewgroupTxtSourceLang.setOnClickListener {
            selectedLanguageSessionTextView = txtSourceLang
            animatorSetChooseLangAppear.start()
        }

        viewgroupTxtTargetLang.setOnClickListener {
            selectedLanguageSessionTextView = txtTargetLang
            animatorSetChooseLangAppear.start()
        }

        imgBlackBackgroundChooseLanguage.setOnClickListener {
            animatorSetChooseLangDisappear.start()
        }

        rcvChooseLanguageAdapter.setOnItemClickListener {
            selectedLanguageSessionTextView?.text = it
            animatorSetChooseLangDisappear.start()
        }

        btnAdd.setOnClickListener {
            fun addFlashcardToOnlineDB (flashcardSetId : String, newFlashcard: Flashcard) {
                viewModel.addFlashcardToOnlineDB(flashcardSetId, newFlashcard) { isSuccess, ex ->
                    if (isSuccess) {
                        quickLog("Add Success To Online DB")
                    } else {
                        quickLog("Add Failed To Online DB")
                    }
                }
            }
            fun addFlashcardToHistory (cardId : Int, searchedDate : Date) {
                viewModel.addSearchedCardToHistory(cardId, searchedDate)
            }

            val newFlashcard = Flashcard(0, edtText.text.toString(), txtTranslation.text.toString(), "${txtSourceLang.text}-${txtTargetLang.text}")
            viewModel.addFlashcardToOfflineDB(newFlashcard) { isSuccess, insertedCardId, ex ->
                if (isSuccess) {
                    quickLog("Add Success To Offline DB")
                    // If add to offline database successfully, we could get its unique id and pass to Online Database
                    // and Search History
                    newFlashcard.id = insertedCardId.toInt()
                    addFlashcardToOnlineDB(newFlashcard.type, newFlashcard)
                    addFlashcardToHistory(newFlashcard.id, newFlashcard.createdAt)
                } else {
                    quickLog("Add Failed To Offline DB")
                }
            }
            if (isInSearchingMode) startEndSearchingAnimations()
            rcvRecentSearchFlashcardAdapter.addFlashcardAtTheFirstPosition(newFlashcard)
            btnAddDisappearAnimator.start()
        }

        btnSwap.setOnClickListener {
            viewgroupTxtSourceLang.startAnimation(moveRightAndFadeOutAnim)
            viewgroupTxtTargetLang.startAnimation(moveLeftAndFadeOutAnim)
        }
    }}


    //================== LIFE CYCLE OVERRIDE METHOD ===============

    override fun onStart() {
        super.onStart()
        viewModel.getAllSearchHistoryInfo {
            val setOfId = ArrayList<Int>()
            for (holder in it) {
                setOfId.add(holder.cardId)
            }
            for (holder in setOfId) {
                quickLog("Times: " + holder)
            }
            viewModel.getAllFlashcardById(setOfId) {
                for (i in it) {
                    quickLog("Card ID: " + i.id)
                }
                rcvRecentSearchFlashcardAdapter.setData(it)
            }
        }
    }

    override fun onBackPressed() {
        if (isInSearchingMode) startEndSearchingAnimations()
        else if (isInChooseLanguageMode) animatorSetChooseLangDisappear.start()
        else super.onBackPressed()
    }

    //================== SERVICE METHODS =======================

    fun startEndSearchingAnimations () { dataBinding.apply {
        if (txtTranslation.text.toString() == "") {
            endSearchingAnimatorSet.start()
        } else {
            endSearchingWithTranslationAnimatorSet.start()
        }
        hideVirtualKeyboard()
        currentFocus?.clearFocus()
    }}

    private fun swapLanguage () { dataBinding.apply {
        var middleManSwapLanguage = txtSourceLang.text
        txtSourceLang.text = txtTargetLang.text
        txtTargetLang.text = middleManSwapLanguage
    }}

    //================== INIT ANIMATIONS ========================



    @Inject
    fun initStartSearchingAnimations (
        @Named("EditTextTextScaleSmallerAnimator") edtTextTextScaleSmaller_Animator : ValueAnimator,
        @Named("ViewGroupLanguageOptionHideAnimator") vgLangOption_HideAnimator : ValueAnimator,
        @Named("AppearAnimator") txtTranslation_Appear_Animator  : Animator,
        @Named("ScaleBiggerAnimator") txtTranslation_ScaleBigger_Animator : ValueAnimator,
        @Named("EditTextTextScaleSmallerAnimator") txtTranslation_ScaleSmaller_Animator : ValueAnimator) { dataBinding.apply {

        vgLangOption_HideAnimator.apply {
            addUpdateListener {
                val newLayoutParams = viewgroupLanguageOption.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                viewgroupLanguageOption.layoutParams = newLayoutParams
            }
            setTarget(viewgroupLanguageOption)
        }

        txtTranslation_Appear_Animator.setTarget(txtTranslation)

        txtTranslation_ScaleBigger_Animator.apply {
            addUpdateListener {
                val newLayoutParams = txtTranslation.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                txtTranslation.layoutParams = newLayoutParams
            }
            addListener (onStart = {
                txtTranslation.appear()
            })
            setTarget(txtTranslation)
        }

        txtTranslation_ScaleSmaller_Animator.apply {
            addUpdateListener {
                val newLayoutParams = txtTranslation.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                txtTranslation.layoutParams = newLayoutParams
            }

            addListener (onEnd = {
                txtTranslation.gravity = Gravity.CENTER_VERTICAL
                txtTranslation.setPadding(edtText.paddingLeft, 0, 0, 0)
                txtTranslation.requestLayout()
            })

            setTarget(txtTranslation)
        }

        edtTextTextScaleSmaller_Animator.apply {
            addUpdateListener {
                val newLayoutParams = edtText.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                edtText.layoutParams = newLayoutParams
            }
            addListener (onEnd = {
                edtText.gravity = Gravity.CENTER_VERTICAL
                edtText.setPadding(edtText.paddingLeft, 0, 0, 0)
                edtText.requestLayout()
            })
            setTarget(edtText)
        }

        val txtTranslation_AppearAnimatorSet = AnimatorSet().apply {
            play(txtTranslation_Appear_Animator).with(txtTranslation_ScaleBigger_Animator)
        }
        startSearchingAnimatorSet
            .play(vgLangOption_HideAnimator)
            .with(txtTranslation_AppearAnimatorSet)
            .with(edtTextTextScaleSmaller_Animator)
        startSearchingAnimatorSet.addListener (onStart = {
            rcvRecentSearchFlashcards.smoothScrollToPosition(0)
        }, onEnd = {
            isInSearchingMode = true
        })

        startSearchingWithTranslationAnimatorSet
            .play(vgLangOption_HideAnimator)
            .with(edtTextTextScaleSmaller_Animator)
            .with(txtTranslation_ScaleSmaller_Animator)
        startSearchingWithTranslationAnimatorSet.addListener (onStart = {
            rcvRecentSearchFlashcards.smoothScrollToPosition(0)
        }, onEnd = {
            isInSearchingMode = true
        })

    }}

    @Inject
    fun initEndSearchingAnimations (
        @Named("EditTextTextScaleBiggerAnimator") edtTextTextScaleBigger_Animator : ValueAnimator,
        @Named("ViewGroupLanguageOptionShowAnimator") vgLangOption_ShowAnimator : ValueAnimator,
        @Named("DisappearAnimator") txtTranslation_Disappear_Animator : Animator,
        @Named("ScaleSmallerAnimator") txtTranslation_ScaleSmaller_Animator : ValueAnimator,
        @Named("EditTextTextScaleBiggerAnimator") txtTranslation_ScaleBigger_Animator: ValueAnimator) { dataBinding.apply {
        vgLangOption_ShowAnimator.apply {
            addUpdateListener {
                val newLayoutParams = viewgroupLanguageOption.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                viewgroupLanguageOption.layoutParams = newLayoutParams
            }
        }

        txtTranslation_Disappear_Animator.setTarget(txtTranslation)

        txtTranslation_ScaleSmaller_Animator.apply {
            addUpdateListener {
                val newLayoutParams = txtTranslation.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                txtTranslation.layoutParams = newLayoutParams
            }
            addListener (onEnd = {
                txtTranslation.disappear()
            })
            setTarget(txtTranslation)
        }

        txtTranslation_ScaleBigger_Animator.apply {
            addUpdateListener {
                val newLayoutParams = txtTranslation.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                txtTranslation.layoutParams = newLayoutParams
            }

            addListener (onStart = {
                txtTranslation.gravity = Gravity.TOP xor Gravity.LEFT
                txtTranslation.setPadding(txtTranslation.paddingLeft, txtTranslation.paddingLeft * 16 / 20, 0, 0)
                txtTranslation.requestLayout()
            })

            setTarget(txtTranslation)
        }

        edtTextTextScaleBigger_Animator.apply {
            addUpdateListener {
                val newLayoutParams = edtText.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                edtText.layoutParams = newLayoutParams
            }
            addListener (onStart = {
                edtText.gravity = Gravity.TOP xor Gravity.LEFT
                edtText.setPadding(edtText.paddingLeft, edtText.paddingLeft * 16 / 20, 0, 0)
                edtText.requestLayout()
                quickLog("Done")
            })
            setTarget(edtText)
        }

        val txtTranslation_DisappearAnimatorSet = AnimatorSet().apply {
            play(txtTranslation_Disappear_Animator).with(txtTranslation_ScaleSmaller_Animator)
        }

        endSearchingAnimatorSet
            .play(vgLangOption_ShowAnimator)
            .with(txtTranslation_DisappearAnimatorSet)
            .with(edtTextTextScaleBigger_Animator)
        endSearchingAnimatorSet.addListener (onStart = {
            rcvRecentSearchFlashcards.smoothScrollToPosition(0)
        }, onEnd = {
            isInSearchingMode = false
        })
        endSearchingWithTranslationAnimatorSet
            .play(vgLangOption_ShowAnimator)
            .with(edtTextTextScaleBigger_Animator)
            .with(txtTranslation_ScaleBigger_Animator)
        endSearchingWithTranslationAnimatorSet.addListener (onStart = {
            rcvRecentSearchFlashcards.smoothScrollToPosition(0)
        }, onEnd = {
            isInSearchingMode = false
        })
    }}

    @Inject
    fun initAddButtonAnimations (
        @Named("FromNothingToNormalSizeAnimator") appearAnimator : Animator,
        @Named("FromNormalSizeToNothingAnimator") disappearAnimator : Animator) {

        btnAddAppearAnimator = appearAnimator.apply {
            setTarget(dataBinding.btnAdd)
            addListener (onStart = {
                dataBinding.btnAdd.appear()
            }, onEnd = {
                addButtonDoesAppear = true
            })
        }

        btnAddDisappearAnimator = disappearAnimator.apply {
            setTarget(dataBinding.btnAdd)
            addListener (onEnd = {
                dataBinding.btnAdd.disappear()
                addButtonDoesAppear = false
            })
        }
    }


    @Inject
    fun initSwapAnimations (
        @Named("MoveRightAndFadeOut") moveRightAndFadeOutAnimation : Animation,
        @Named("MoveLeftAndFadeOut") moveLeftAndFadeOutAnimation : Animation) { dataBinding.apply {
        moveRightAndFadeOutAnim = moveRightAndFadeOutAnimation.apply {
            addAnimationLister (onStart = {
            }, onEnd = {
                swapLanguage()
            })
        }
        moveLeftAndFadeOutAnim = moveLeftAndFadeOutAnimation

    }}

    @Inject
    fun initChooseLanguageAnimation (
        @Named("FromNormalSizeToNothing_") rcvChooseLanguageDisappear : Animator,
        @Named("Disappear50percents_") blackBackgroundDisappear : Animator,
        @Named("FromNothingToNormalSize_") rcvChooseLanguageAppear : Animator,
        @Named("Appear50percents_") blackBackgroundAppear : Animator) { dataBinding.apply {

        rcvChooseLanguageDisappear.setTarget(rcvChooseSourceLanguage)
        blackBackgroundDisappear.setTarget(imgBlackBackgroundChooseLanguage)
        animatorSetChooseLangDisappear.play(rcvChooseLanguageDisappear).before(blackBackgroundDisappear)
        animatorSetChooseLangDisappear.addListener (onEnd = {
            groupChooseLanguage.visibility = View.GONE
            isInChooseLanguageMode = false
        })

        rcvChooseLanguageAppear.setTarget(rcvChooseSourceLanguage)
        blackBackgroundAppear.setTarget(imgBlackBackgroundChooseLanguage)
        animatorSetChooseLangAppear.play(blackBackgroundAppear).before(rcvChooseLanguageAppear)
        animatorSetChooseLangAppear.addListener(onStart = {groupChooseLanguage.appear()},
            onEnd = {
                isInChooseLanguageMode = true
            })
    }}
}
