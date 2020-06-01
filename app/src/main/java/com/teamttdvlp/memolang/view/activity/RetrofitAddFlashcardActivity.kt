package com.teamttdvlp.memolang.view.activity
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.InputType.TYPE_NULL
import android.view.Gravity
import android.view.View
import android.view.animation.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.viewmodel.AddFlashCardViewModel
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.databinding.ActivityAddFlashcardRetroBinding
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.view.activity.iview.AddFlashcardView
import com.teamttdvlp.memolang.view.adapter.*
import com.teamttdvlp.memolang.view.customview.MyGestureDetector
import com.teamttdvlp.memolang.view.customview.NormalOutExtraSlowIn
import java.lang.Math.pow
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.sqrt


class RetrofitAddFlashcardActivity : BaseActivity<ActivityAddFlashcardRetroBinding, AddFlashCardViewModel>()
    ,AddFlashcardView {

    companion object {

        private val SET_NAME = "set_name"

        private val FRONT_LANGUAGE = "front_lang"

        private val BACK_LANGUAGE = "back_lang"

        val ADD_FLASHCARD_RESULT_CODE = 2118

        fun requestAddLanguage(packageContext: Context, flashcardSet: FlashcardSet) {
            val intent = Intent(packageContext, RetrofitAddFlashcardActivity::class.java)
            intent.putExtra(SET_NAME, flashcardSet)
            packageContext.startActivity(intent)
        }
    }

    // The image will stay on screen for a while before disappearing
    private val TIME_THAT_SAVED_FLASHCARD_IMAGE_STAY_ON_SCREEN = 400L

    private var currentFocusedLanguageTextView: EditText? = null

    private var isInChooseLanguageMode = false

    private var isIPAKeyboardVisible = false

    private var isShowingFrontCard = true

    private var hasStartedEdit = false

    private var savedCardWidth = 0

    private var savedCardHeight = 0

    private var thereIsAnyCardInbox : Boolean = true

    private var userEndAdding = false

    private lateinit var turnUpFrontCard_After_TurnDownBackCard : Animation

    private lateinit var turnUpBackCard_After_TurnDownFrontCard : Animation


    lateinit var rcvFrontLangChooseLanguageAdapter: RCVChooseLanguageAdapter
    @Inject set

    lateinit var rcvFrontLangRecentChosenLanguageAdapter: RCVRecentUsedLanguageAdapter
    @Inject set

    lateinit var rcvBackLangChooseLanguageAdapter: RCVChooseLanguageAdapter
        @Inject set

    lateinit var rcvBackLangRecentChosenLanguageAdapter: RCVRecentUsedLanguageAdapter
        @Inject set


    lateinit var rcvFlashcardSetNameAdapter: RCV_Generic_SimpleListAdapter<FlashcardSet>
        @Inject set


    lateinit var viewModelProviderFactory: ViewModelProviderFactory
        @Inject set


    override fun getLayoutId(): Int = R.layout.activity_add_flashcard_retro

    override fun takeViewModel(): AddFlashCardViewModel {
        return getActivityViewModel(viewModelProviderFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(resources.getColor(R.color.app_blue))
        viewModel.setUpView(this)
        dB.vwModel = viewModel
        recalculateFlashcardSetBox()
    }

    private fun recalculateFlashcardSetBox () { dB.apply {
        viewgroupFrontFlashcard.doOnPreDraw {
            with (it) {
                val ratio : Double = (height.toDouble() / width.toDouble())
                val smallCardW = width - 30.dp()
                val smallCardH  = (smallCardW * ratio).toInt()

                savedCardWidth = smallCardW
                savedCardHeight = smallCardH

                val boxH = smallCardH + 30.dp()

                vwgrpCardInBox.layoutParams.height = smallCardH
                vwgrpCardInBox.layoutParams.width = smallCardW

                vwgrpBoxBottom.layoutParams.height = boxH
                vwgrpBoxBottom.layoutParams.width = width

                vwgrpBoxCoverLayer.layoutParams.height = boxH
                vwgrpBoxCoverLayer.layoutParams.width = width

                vwgrpBoxBottom.requestLayout()
                vwgrpBoxCoverLayer.requestLayout()
                vwgrpCardInBox.requestLayout()
            }
        }
    }}

    override fun onPause() {
        super.onPause()
        viewModel.saveUsingHistory()
    }

    override fun addViewControls() {
        dB.apply {
            showDefault_Info_OnScreen()
            disableEdtPronunciation()
            setUpEditTextsFeatures()
            // Choose Flashcard SET NAME Recycler View
            rcvFlashcardSetName.adapter = rcvFlashcardSetNameAdapter
            viewModel.getAllFlashcardSetWithNoCardList { flashcardSetList ->
                rcvFlashcardSetNameAdapter.setData(flashcardSetList)
            }

            // Choose LANGUAGE RecyclerView
            rcvFrontChooseLanguage.adapter = rcvFrontLangChooseLanguageAdapter
            rcvBackChooseLanguage.adapter = rcvBackLangChooseLanguageAdapter

            // Recent CHOSEN LANGUAGE RecyclerView
            rcvFrontRecentChosenLanguage.adapter = rcvFrontLangRecentChosenLanguageAdapter
            rcvBackRecentChosenLanguage.adapter = rcvBackLangRecentChosenLanguageAdapter

            viewModel.getUsedLanguageList { recentUsedLanguageList ->
                rcvFrontLangRecentChosenLanguageAdapter.setData(recentUsedLanguageList)
                rcvBackLangRecentChosenLanguageAdapter.setData(recentUsedLanguageList)
            }

            // Choose TYPE RecyclerView
            rcvChooseCardType.addTypes(viewModel.getUserOwnCardTypes())

            ipaKeyboard.setFocusedText(dB.edtPronunciation)

            rcvFlashcardSetName.pivotX = 100f
            rcvFlashcardSetName.pivotY = 0f

            vwgrpFrontLangChooseLanguage.pivotX = 100f
            vwgrpFrontLangChooseLanguage.pivotY = 0f

            vwgrpBackLangChooseLanguage.pivotX = 100f
            vwgrpBackLangChooseLanguage.pivotY = 0f
        }
    }


    private fun setUpEditTextsFeatures() { dB.apply {
        edtText.imeOptions = EditorInfo.IME_ACTION_NEXT
        edtText.setRawInputType(InputType.TYPE_CLASS_TEXT)

        edtType.imeOptions = EditorInfo.IME_ACTION_NEXT
        edtType.setRawInputType(InputType.TYPE_CLASS_TEXT)

        edtTranslation.imeOptions = EditorInfo.IME_ACTION_NEXT
        edtTranslation.setRawInputType(InputType.TYPE_CLASS_TEXT)

        edtExample.imeOptions = EditorInfo.IME_ACTION_NEXT
        edtExample.setRawInputType(InputType.TYPE_CLASS_TEXT)

        edtExampleTranslation.imeOptions = EditorInfo.IME_ACTION_DONE
        edtExampleTranslation.setRawInputType(InputType.TYPE_CLASS_TEXT)

    }}

    private fun disableEdtPronunciation() {
        dB.edtPronunciation.inputType = TYPE_NULL
    }

    private fun showDefault_Info_OnScreen() {
        val flashcardSet = getAddFlashcardRequest()
        if (flashcardSet == null) {
            setUp_CurrentChosenOptions_OnScreen()
        } else {
            viewModel.showToUI(flashcardSet.name, flashcardSet.frontLanguage, flashcardSet.backLanguage)
        }
    }

    private fun setUp_CurrentChosenOptions_OnScreen() {
        val currentBackLang = viewModel.getCurrentBackLanguage()
        val currentFrontLang = viewModel.getCurrentFrontLanguage()
        val currentUseSetName = viewModel.getLastedUseFlashcardSetName()
        viewModel.showToUI(currentUseSetName, currentFrontLang, currentBackLang)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun addViewEvents() {
        dB.apply {

            edtText.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus && hasStartedEdit.not()) {
                    showVirtualKeyboard()
                    edtText.requestFocus()
                    startEditting()
                }
            }

            edtText.addTextChangeListener { text, i, i2, i3 ->
                if (text.isNotEmpty()) {
                    resetTextEditTextHintToNormal()
                }
            }

            edtType.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus && hasStartedEdit.not()) {
                    showVirtualKeyboard()
                    edtType.requestFocus()
                    startEditting()
                }
            }

            edtTranslation.addTextChangeListener { text, i1, i2, i3 ->
                if (text.isNotEmpty()) {
                    resetEditTextTranslationHintToNormal()
                }
            }

            edtPronunciation.setOnClickListener {
                if (hasStartedEdit.not()) {
                    startEditting()
                }
                showIPAKeyboard()
                hideScreenFocusToTranslationEditText()
            }

            ipaKeyboard.setOnDeviceVirtualKeyboardShow {
                hideIPAKeyboard()
            }

            ipaKeyboard.setOnBtnDoneClickListener {
                showBackCard_AndUpdateStatus()
                isShowingFrontCard = false
                edtTranslation.requestFocus()
                showVirtualKeyboard()
            }

            edtType.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    showIPAKeyboard()
                    hideScreenFocusToTranslationEditText()
                    edtTranslation.requestFocus()
                }
                return@setOnEditorActionListener true
            }

            imgSwipeZone.setOnTouchListener(object : MyGestureDetector(this@RetrofitAddFlashcardActivity) {

                override fun onSwipeRight() {
                    proceedAddFlashcard()
                    userEndAdding = true
                }

                override fun onSwipeLeft() {
                    proceedAddFlashcard()
                    userEndAdding = false
                }

                override fun onSwipeUp() {
                    if (hasStartedEdit.not()) {
                        showVirtualKeyboard()
                        edtText.requestFocus()
                        startEditting()
                    } else {
                        showVirtualKeyboard()
                        if (isShowingFrontCard and edtText.text.isEmpty()) {
                            edtText.requestFocus()
                            return
                        }

                        if (isShowingFrontCard.not() and edtTranslation.text.isEmpty()) {
                            edtTranslation.requestFocus()
                            return
                        }

                        onUserFlipCard()
                    }
                }

                override fun onSwipeDown() {
                    if (hasStartedEdit) {
                        onUserFlipCard()
                        showVirtualKeyboard()
                    }
                }
            })

            txtSetName.setOnClickListener {
                showSetNameList()
                hideScreenFocusToTranslationEditText()
            }

            rcvFrontLangChooseLanguageAdapter.setOnItemClickListener { language ->
                onChooseFrontLanguage(language)
            }

            rcvFrontLangRecentChosenLanguageAdapter.setOnItemClickListener { language ->
                onChooseFrontLanguage(language)
            }

            rcvBackLangChooseLanguageAdapter.setOnItemClickListener { language ->
                onChooseBackLanguage(language)
            }

            rcvBackLangRecentChosenLanguageAdapter.setOnItemClickListener { language ->
                onChooseBackLanguage(language)
            }

            edtType.addTextChangeListener (onTextChanged = { text, start, beforeTextLength, count ->
                if (text.length == beforeTextLength) {
                    hideCardTypeList()
                    return@addTextChangeListener
                }

                val result = rcvChooseCardType.filtTypeAndUpdateView(text)
                if ((result.size > 0)) {
                    if (rcvChooseCardType.isVisible().not())
                        showCardTypeList()
                } else { // List is empty
                    hideCardTypeList()
                }
            })

            rcvChooseCardType.setOnItemClickListener { type ->
                edtType.setText(type)
                edtType.setSelection(type.length)
                hideCardTypeList()
            }

            rcvFlashcardSetNameAdapter.setOnItemClickListener { flashcardSet ->
                txtSetName.text = flashcardSet.name
                txtFrontLang.text = flashcardSet.frontLanguage
                txtBackLang.text = flashcardSet.backLanguage
                hideSetNameList()
            }

            btnStartCreateNewSet.setOnClickListener {
                edtNewSetName.requestFocus()
                edtNewSetFrontLanguage.setText(txtFrontLang.text)
                edtNewSetBackLanguage.setText(txtBackLang.text)
                showCreateSetNamePanel()
            }

            imgSetNameSpinner.setOnClickListener {
                showSetNameList()
            }

            imgNewSetFrontLangSpinner.setOnClickListener {
                showChooseFrontLanguageLists()
                hideChooseBackLanguageList()
                hideVirtualKeyboard()
            }

            imgNewSetBackLangSpinner.setOnClickListener {
                showChooseBackLanguageLists()
                hideChooseFrontLanguageList()
                hideVirtualKeyboard()
            }

            btnInPanelCreateNewSet.setOnClickListener {
                val setName = edtNewSetName.text.toString()
                val frontLang = edtNewSetFrontLanguage.text.toString()
                val backLang = edtNewSetBackLanguage.text.toString()
                txtSetName.text = setName
                txtFrontLang.text =frontLang
                txtBackLang.text = backLang
                viewModel.createNewFlashcardSetIfValid(setName, frontLang, backLang)
            }

            imgTurnOffCreateNewSet.setOnClickListener {
                hideCreateNewFlashcardSetPanel()
            }

            btnInErrorPanelGotIt.setOnClickListener {
                hideInvalidFlashcardSetError()
                txtSetName.text = ""
                txtSetName.requestFocus()
                showVirtualKeyboard()
            }
        }
    }

    private fun showCardTypeList () {
        dB.rcvChooseCardType.showByScaleUpAndFadeIn()
    }

    private fun hideCardTypeList () {
        dB.rcvChooseCardType.hideByScaleDownAndFadeOut()
    }

    private fun proceedAddFlashcard () { dB.apply {

        if (hasStartedEdit.not()) {
            return
        }

        quickLog("On Swipe Right")
        hideIPAKeyboard()
        val text = edtText.text.toString()
        val translation = edtTranslation.text.toString()
        val example = edtExample.text.toString()
        val meanOfExample = edtExampleTranslation.text.toString()
        val type = edtType.text.toString()
        val pronunciation = edtPronunciation.text.toString()
        val frontLanguage = txtFrontLang.text.toString()
        val backLanguage = txtBackLang.text.toString()
        val setName = txtSetName.text.toString()

        val newCard = Flashcard(id = 0, text = text,
            translation = translation,
            example = example,
            meanOfExample = meanOfExample,
            type = type,
            pronunciation = pronunciation,
            frontLanguage = frontLanguage,
            backLanguage = backLanguage,
            setOwner = setName)

        viewModel.proceedAddFlashcard(newCard)
        if (type.isNotEmpty()) {
            rcvChooseCardType.addType(type)
        }
    }}

    private fun playResetCardAnims(startDelay : Long = 0) { dB.apply {
        // Base on Pytagorean Theorem c = sqrt ( a^2 + b^2 )
        val circleTargetRadius = sqrt(pow(viewgroupFrontFlashcard.height.toDouble(), 2.0) + pow(viewgroupFrontFlashcard.width.toDouble(), 2.0))
        val scaleRatio = circleTargetRadius.toFloat()  / imgResetFlashcardCircle.height.toFloat()

        // Phase 1: Scale Up and fade in
        imgResetFlashcardCircle.animate()
            .alpha(1f)
            .scaleX(scaleRatio).scaleY(scaleRatio)
            .setDuration(200).setInterpolator(NormalOutExtraSlowIn()).setStartDelay(startDelay)
            .setLiteListener (onStart = {
                imgResetFlashcardCircle.isClickable = true
            }, onEnd = {

                // Phase 2 : Fade out
                clearAllCardInfomation()
                edtText.requestFocus()

                imgResetFlashcardCircle.animate().setStartDelay(0).setDuration(150).alpha(0f).setLiteListener (onEnd = {
                    imgResetFlashcardCircle.scaleX = 1f / scaleRatio
                    imgResetFlashcardCircle.scaleY = 1f / scaleRatio
                    imgResetFlashcardCircle.isClickable = false
                })
            })
    }}

    private fun clearAllCardInfomation () { dB.apply {
        edtText.setText("")
        edtType.setText("")
        edtPronunciation.setText("")
        edtTranslation.setText("")
        edtExample.setText("")
        edtExampleTranslation.setText("")
    }}



    private fun onChooseFrontLanguage (language : String) { dB.apply {
        edtNewSetFrontLanguage.setText(language)
        hideChooseFrontLanguageList()
    }}

    private fun showChooseFrontLanguageLists () { dB.apply {
        vwgrpFrontLangChooseLanguage.showByScaleUpAndFadeIn()
    }}

    private fun hideChooseFrontLanguageList () { dB.apply {
        vwgrpFrontLangChooseLanguage.hideByScaleDownAndFadeOut()
    }}


    private fun onChooseBackLanguage (language : String) { dB.apply {
        edtNewSetBackLanguage.setText(language)
        hideChooseBackLanguageList()
    }}

    private fun showChooseBackLanguageLists () { dB.apply {
        vwgrpBackLangChooseLanguage.showByScaleUpAndFadeIn()
    }}

    private fun hideChooseBackLanguageList () { dB.apply {
        vwgrpBackLangChooseLanguage.hideByScaleDownAndFadeOut()
    }}


    private fun View.hideByScaleDownAndFadeOut () {
        this.animate().alpha(0f)
            .scaleX(0f).scaleY(0f)
            .setDuration(150).setInterpolator(NormalOutExtraSlowIn())
            .setLiteListener (onStart = {
                // DO NOTHING (This prevent view from running the onStart() set by View.showByScaleUpAndFadeIn()
            }, onEnd = {
                this.goGONE()
            })
    }


    private fun View.showByScaleUpAndFadeIn () {
        this.animate().alpha(1f)
            .scaleX(1f).scaleY(1f)
            .setDuration(150).setInterpolator(NormalOutExtraSlowIn())
            .setLiteListener (onStart = {
                this.goVISIBLE()
            }, onEnd = {
                // DO NOTHING (This prevent view from running the onEnd() set by View.hideByScaleDownAndFadeOut()
            })
    }


    private fun showCreateSetNamePanel () { dB.apply {
        // Blue with 50% Black color covers
        setStatusBarColor(resources.getColor(R.color.dark_covered_blue))

        vwgrpCreateNewSet.showByScaleUpAndFadeIn()
        imgTurnOffCreateNewSet.goVISIBLE()
        imgTurnOffCreateNewSet.animate().alpha(0.5f).setDuration(100).setLiteListener(onEnd = {
            showVirtualKeyboard()
        })
    }}


    override fun hideCreateNewFlashcardSetPanel() { dB.apply {
        setStatusBarColor(resources.getColor(R.color.app_blue))

        vwgrpCreateNewSet.hideByScaleDownAndFadeOut()
        imgTurnOffCreateNewSet.animate().alpha(0f).setDuration(100)
            .setLiteListener (onEnd = {
            imgTurnOffCreateNewSet.goGONE()
        })

        hideVirtualKeyboard()
    }}

    private fun showSetNameList () {
        dB.rcvFlashcardSetName.animate()
                                                .alpha(1f)
                                                .scaleX(1f).scaleY(1f)
                                                .setDuration(150).setInterpolator(NormalOutExtraSlowIn())
                                                .setLiteListener (onStart = {
                                                    dB.rcvFlashcardSetName.goVISIBLE()
                                                })
    }

    private fun hideSetNameList () {
        dB.rcvFlashcardSetName.animate()
                                                .alpha(0f)
                                                .scaleX(0f).scaleY(0f)
                                                .setDuration(150).setInterpolator(NormalOutExtraSlowIn())
                                                .setLiteListener (onEnd = {
                                                    dB.rcvFlashcardSetName.goGONE()
                                                })
    }

    private fun resetEditTextTranslationHintToNormal() {
        dB.edtTranslation.hint = "Translation"
        dB.edtTranslation.setHintTextColor(Color.parseColor("#9E9E9E"))
    }

    private fun resetTextEditTextHintToNormal() {
        dB.edtText.hint = "Text"
        dB.edtText.setHintTextColor(Color.parseColor("#9E9E9E"))
    }

    private fun playSaveAnimations(delayTime : Long = 0, onEnd : () -> Unit = {}) { dB.apply {
        Thread(Runnable {
            Thread.sleep(delayTime)
            runOnUiThread {
                // PHASE 1
                val boxAppear = doActionOnBox {
                    if ((it == vwgrpCardInBox) && (thereIsAnyCardInbox.not())) {
                        return@doActionOnBox
                    }

                    it.alpha = 0f
                    it.goVISIBLE()
                    it.animate()
                        .alpha(1f)
                        .setDuration(200).setInterpolator(LinearInterpolator())
                }

                val boxMoveUp = doActionOnBox {
                    if ((it == vwgrpCardInBox) && (thereIsAnyCardInbox.not())) {
                        return@doActionOnBox
                    }

                    it.translationY = 90f
                    it.animate()
                        .translationY(0f)
                        .setDuration(200).setInterpolator(LinearInterpolator())
                }

                val swipeFunctionsDisappear = vwgrpSwipeFunctions.animate()
                    .alpha(0f)
                    .setDuration(200).setInterpolator(LinearInterpolator())
                    .setListener(null) // Reset this listener because it has been registered before in
                // startEditting() function, if the code is updated. Please becareful of this kind of listener, it is affected on all animate() functions called thorough project
                // which can run something that is unexpected


                if (edtType.text.isEmpty()) {
                    edtType.animate().alpha(0f).setDuration(200)
                    
                    val edtTextScaleOut = ValueAnimator.ofInt(edtType.height, 0).apply {
                        setTarget(edtType)
                        duration = 200
                        addUpdateListener {
                            edtType.layoutParams.height = it.animatedValue as Int
                            edtType.requestLayout()
                        }
                        start()
                    }
                }

                if (edtPronunciation.text.isEmpty()) {
                    edtPronunciation.animate().alpha(0f).setDuration(200)
                    val edtPronunciationScaleOut = ValueAnimator.ofInt(edtPronunciation.height, 0).apply {
                        setTarget(edtPronunciation)
                        duration = 200
                        addUpdateListener {
                            edtPronunciation.layoutParams.height = it.animatedValue as Int
                            edtPronunciation.requestLayout()
                        }
                        start()
                    }
                }

                edtText.gravity = Gravity.CENTER_HORIZONTAL

                val theSmallRatio = savedCardWidth.toFloat() / viewgroupFrontFlashcard.width.toFloat()
                val theCardShrink = viewgroupFrontFlashcard.animate()
                    .scaleX(theSmallRatio).scaleY(theSmallRatio)
                    .setDuration(200).setInterpolator(LinearInterpolator())
                    .setLiteListener (onEnd = {
                        // PHASE 2
                        val theLidGoDown = vwgrpBoxCoverLayer.animate().translationY((vwgrpBoxCoverLayer.height + 30.dp()).toFloat())
                            .setDuration(400).setInterpolator(FastOutSlowInInterpolator())
                            .setLiteListener (onEnd = {

                                // PHASE 3
                                val distanceY = viewgroupFrontFlashcard.height + 20.dp() + (viewgroupFrontFlashcard.height - savedCardHeight) / 2
                                val theCardGoDown = viewgroupFrontFlashcard.animate().translationY(distanceY.toFloat())
                                    .setDuration(350).setInterpolator(FastOutSlowInInterpolator())
                                    .setLiteListener (onEnd = {

                                        // PHASE 4 (ENDING)
                                        val theLidGoUp = vwgrpBoxCoverLayer.animate().translationY(0f)
                                            .setDuration(300).setInterpolator(FastOutSlowInInterpolator()).setLiteListener {
                                                onEnd.invoke()
                                            }

                                        val lowerCardElevation = ValueAnimator.ofFloat(viewgroupFrontFlashcard.elevation, vwgrpCardInBox.elevation + vwgrpCardInBox.translationZ + 1.dp())
                                        lowerCardElevation.duration = 100
                                        lowerCardElevation.addUpdateListener { it ->
                                            viewgroupFrontFlashcard.elevation = it.animatedValue as Float
                                        }
                                        lowerCardElevation.setTarget(viewgroupFrontFlashcard)
                                        lowerCardElevation.start()
                                    })
                            })
                    })
            }
        }).start()
    }}

    private fun doActionOnBox (action : (View) -> Unit) { dB.apply {
          action.invoke(vwgrpBoxBottom)
          action.invoke(vwgrpCardInBox)
          action.invoke(vwgrpBoxCoverLayer)
    }}

    private fun startEditting () { dB.apply {
        vwgrpSwipeFunctions.animate().alpha(0f).setDuration(100)
            .setLiteListener (onEnd = {
                txtFlipCardText.goVISIBLE()
                txtStartCreatingText.goGONE()
                vwgrpSwipeRightToSave.goVISIBLE()
                vwgrpSwipeFunctions.animate().alpha(1f).setDuration(100)
            })

        hasStartedEdit = true
    }}

    private fun onUserFlipCard() {
        if (isShowingFrontCard) {
            showBackCard_AndUpdateStatus()
            isShowingFrontCard = false
            dB.edtTranslation.requestFocus()

        } else { // is showing back card
            showFrontCard_AndUpdateStatus()
            dB.edtText.requestFocus()
        }
    }


    private fun showFrontCard_AndUpdateStatus () { dB.apply {
        viewgroupBackFlashcard.startAnimation( turnUpFrontCard_After_TurnDownBackCard )
        isShowingFrontCard = true
    }}

    private fun showBackCard_AndUpdateStatus () { dB.apply {
        viewgroupFrontFlashcard.startAnimation( turnUpBackCard_After_TurnDownFrontCard )
        isShowingFrontCard = false
    }}

    override fun overrideEnterAnim() {
        overridePendingTransition(R.anim.appear, R.anim.nothing)
    }

    override fun overrideExitAnim() {
        overridePendingTransition(R.anim.nothing, R.anim.disappear)
    }

    override fun onBackPressed() {
        dB.apply {
            if (isIPAKeyboardVisible) {
                hideIPAKeyboard()
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun getAddFlashcardRequest(): FlashcardSet? {
        val bundle = intent.extras

        if (bundle == null) {
            return null
        }

        val flashcardSet = bundle.getSerializable(SET_NAME) as FlashcardSet
        return flashcardSet
    }


    // VIEW IMPLEMENTED METHODS

    private fun hideIPAKeyboard() {
        dB.apply {
            isIPAKeyboardVisible = false
            ipaKeyboard.goINVISIBLE()
            ipaKeyboard.alpha = 0f
            ipaKeyboard.db.keyboardParent.isClickable = false
        }
    }

    private fun showIPAKeyboard() {
        dB.apply {
            Thread ( Runnable {
                Thread.sleep(200)
                runOnUiThread {
                    ipaKeyboard.goVISIBLE()
                    ipaKeyboard.animate().alpha(1f).setDuration(200)
                    ipaKeyboard.db.keyboardParent.isClickable = true
                    isIPAKeyboardVisible = true
                }
            }).start()

            hideVirtualKeyboard()
        }
    }

    private fun hideScreenFocusToTranslationEditText() {
        dB.edtTranslation.requestFocus()
    }


    override fun showTextInputError() {
        if (isShowingFrontCard.not()) {
            showFrontCard_AndUpdateStatus()
        }
        dB.edtText.requestFocus()
        dB.edtText.hint = "Text can not be empty"
        dB.edtText.setHintTextColor(Color.parseColor("#EC4444"))
    }

    override fun showTranslationInputError() {
        if (isShowingFrontCard) {
            showBackCard_AndUpdateStatus()
        }
        dB.edtTranslation.requestFocus()
        dB.edtTranslation.hint = "Translation can not be empty"
        dB.edtTranslation.setHintTextColor(Color.parseColor("#EC4444"))
    }

    override fun showInvalidFlashcardSetError(errorMessage: String) {
        dB.txtCreateSetError.text = errorMessage
        dB.vwgrpCreateSetError.goVISIBLE()
        dB.vwgrpCreateSetError.animate().alpha(1f).setDuration(200)
    }

    fun hideInvalidFlashcardSetError () {
        dB.vwgrpCreateSetError.animate().alpha(0f).setDuration(200).setLiteListener (onEnd = {
            dB.vwgrpCreateSetError.goGONE()
        })
    }

    private val KEYBOARD_DISAPPEAR_INTERVAL = 50L

    override fun onAddFlashcardSuccess() { dB.apply {
        if (userEndAdding) {
            hideVirtualKeyboard()
            dB.txtBoxSetName.text = dB.txtSetName.text.toString()
            currentFocus?.clearFocus()

            val currentSetName = dB.txtSetName.text.toString()
            viewModel.getFlashcardSetByName(currentSetName) { full_InfoFlashcardSet, err ->
                if (err == null) {
                    if ((full_InfoFlashcardSet != null) && (full_InfoFlashcardSet.flashcards.size > 1)) {
                        val secondLastCard = full_InfoFlashcardSet.flashcards.get(full_InfoFlashcardSet.flashcards.size - 2)
                        setUpCardInbox(secondLastCard)
                    } else { // This is empty set, so in virtual box there's no card
                        vwgrpCardInBox.goGONE()
                        thereIsAnyCardInbox = false
                    }
                } else { // Load flashcard set has error
                    quickLog("Error happened")
                    err.printStackTrace()
                }


                val onEndSaveAnimation = {
                    Thread(Runnable {
                        Thread.sleep(75)
                        runOnUiThread {
                            finish()
                        }
                    }).start()
                }

                if (isShowingFrontCard.not()) {
                    showFrontCard_AndUpdateStatus()
                    playSaveAnimations(delayTime = turnUpFrontCard_After_TurnDownBackCard.duration * 2 + KEYBOARD_DISAPPEAR_INTERVAL, onEnd = onEndSaveAnimation)
                } else {
                    playSaveAnimations(delayTime = KEYBOARD_DISAPPEAR_INTERVAL, onEnd = onEndSaveAnimation)
                }
            }
        }
        else { // User continues adding another flashcard
            if (isShowingFrontCard .not()) {
                showFrontCard_AndUpdateStatus()
                playResetCardAnims(startDelay = turnUpFrontCard_After_TurnDownBackCard.duration + 50)
            } else
            playResetCardAnims()
        }
    }}

    private fun setUpCardInbox (flashcard : Flashcard) { dB.apply {
        txtCardInBoxText.text = flashcard.text

        if (flashcard.type.isNotEmpty()) {
            txtCardInBoxType.text = flashcard.type
        } else {
            txtCardInBoxType.goGONE()
        }

        if (flashcard.pronunciation.isNotEmpty()) {
            txtCardInBoxPronunciation.text = flashcard.pronunciation
        } else {
            txtCardInBoxPronunciation.goGONE()
        }

        thereIsAnyCardInbox = true
    }}

    private fun addToUsedLanguageList(language: String) {
        rcvFrontLangRecentChosenLanguageAdapter.addLanguage(language)
        viewModel.addToUsedLanguageList(language)
    }

    private val COMMON_FLIP_CARD_DURATION = 250L
    private val COMMON_FLIP_CARD_INTERPOLATOR = DecelerateInterpolator()

    @Inject
    fun initShow_FRONT_CardAnimators (
                                             @Named("FlipOpen") turnUpFront : Animation,
                                             @Named("FlipClose") turnDownBack : Animation) { dB.apply {

        turnUpFront.apply {
            duration = COMMON_FLIP_CARD_DURATION
            interpolator = COMMON_FLIP_CARD_INTERPOLATOR
            setAnimationListener(onStart = {
                viewgroupFrontFlashcard.goVISIBLE()
            })
        }

        turnUpFrontCard_After_TurnDownBackCard = turnDownBack.apply {
            duration = COMMON_FLIP_CARD_DURATION
            interpolator = COMMON_FLIP_CARD_INTERPOLATOR
            setAnimationListener(onEnd = {
                viewgroupBackFlashcard.goINVISIBLE()
                viewgroupFrontFlashcard.startAnimation(turnUpFront)
            })
        }

    }}

    @Inject
    fun initShow_BACK_CardAnimators (
                                    @Named("FlipOpen") turnUpBack : Animation,
                                    @Named("FlipClose") turnDownFront : Animation) { dB.apply {

        turnUpBackCard_After_TurnDownFrontCard = turnDownFront.apply {
            duration = COMMON_FLIP_CARD_DURATION
            interpolator = COMMON_FLIP_CARD_INTERPOLATOR
            setAnimationListener(onEnd = {
                viewgroupFrontFlashcard.goINVISIBLE()
                viewgroupBackFlashcard.startAnimation(turnUpBack)
            })
        }

        turnUpBack.apply {
            duration = COMMON_FLIP_CARD_DURATION
            interpolator = COMMON_FLIP_CARD_INTERPOLATOR
            setAnimationListener(onStart = {
                viewgroupBackFlashcard.goVISIBLE()
            })
        }

    }}
}

