package com.teamttdvlp.memolang.view.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.text.InputType.TYPE_NULL
import android.view.View
import android.view.View.SCALE_X
import android.view.View.SCALE_Y
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.*
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
import androidx.core.animation.addListener
import androidx.core.app.ActivityCompat
import androidx.core.view.*
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.data.model.entity.flashcard.SetNameUtils
import com.teamttdvlp.memolang.databinding.ActivityAddFlashcardRetroBinding
import com.teamttdvlp.memolang.view.activity.iview.AddFlashcardView
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCVRecentUsedLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCV_SimpleChooseFlashcardSetAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.MyGestureDetector
import com.teamttdvlp.memolang.view.customview.NormalOutExtraSlowIn
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.AddFlashCardViewModel
import java.io.FileNotFoundException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Named


const val KEYBOARD_DISAPPEAR_INTERVAL = 50L

class RetrofitAddFlashcardActivity : BaseActivity<ActivityAddFlashcardRetroBinding, AddFlashCardViewModel>()
    ,AddFlashcardView {

    companion object {

        private val ADD_REQUEST_FLASHCARD_SET = "add_set_name"

        private val EDIT_REQUEST_FLASHCARD_SET = "edit_set_name"

        private val EDIT_REQUEST_FLASHCARD = "nef"

        fun requestAddLanguage(packageContext: Context, flashcardSet: FlashcardSet) {
            val intent = Intent(packageContext, RetrofitAddFlashcardActivity::class.java)
            intent.putExtra(ADD_REQUEST_FLASHCARD_SET, flashcardSet)
            packageContext.startActivity(intent)
        }

        fun requestEditFlashcard(
            packageContext: Context,
            flashcard: Flashcard,
            flashcardSet: FlashcardSet
        ) {
            val intent = Intent(packageContext, RetrofitAddFlashcardActivity::class.java)
            intent.putExtra(EDIT_REQUEST_FLASHCARD, flashcard)
            intent.putExtra(EDIT_REQUEST_FLASHCARD_SET, flashcardSet)
            packageContext.startActivity(intent)
        }
    }

    // The image will stay on screen for a while before disappearing

    private var isIPAKeyboardVisible = false

    private var isShowingFrontCard = true

    private var hasStartedEdit = false

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

    lateinit var rcvFlashcardSetNameAdapter: RCV_SimpleChooseFlashcardSetAdapter
        @Inject set

    lateinit var viewModelProviderFactory: ViewModelProviderFactory
        @Inject set


    private var requestEditFlashcard: Flashcard? = null

    private var requestEditFlashcardSet: FlashcardSet? = null

    private var requestAdd_FlashcardSet: FlashcardSet? = null

    private var is_InAdjustCardProperty_Mode = false

    override fun getLayoutId(): Int = R.layout.activity_add_flashcard_retro

    override fun takeViewModel(): AddFlashCardViewModel {
        return getActivityViewModel(viewModelProviderFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(resources.getColor(R.color.app_darker_blue))
        viewModel.setUpView(this)
        getRequests()
        if (requestEditFlashcard != null) {
            startEditting()
            showVirtualKeyboard()
            dB.edtText.requestFocus()
        }

        recalculate_ChooseFlashcardPropertyPanel_Dimens()
    }


    private fun recalculateIllustrationPictureDimens(rate: Float) {
        dB.apply {
            imgIllustrationPicture.doOnPreDraw {
                imgIllustrationPicture.layoutParams.width =
                    (imgIllustrationPicture.height * rate).toInt()
                imgIllustrationPicture.requestLayout()
            }
        }

    }

    private fun recalculate_Flashcard_Dimens(heightRate: Float) {
        dB.apply {
            viewgroupFrontFlashcard.doOnPreDraw {
                viewgroupFrontFlashcard.layoutParams.height =
                    (heightRate * (viewgroupFrontFlashcard.width / 2)).toInt()
                log("Height: ${viewgroupFrontFlashcard.height}")
                log("LayoutParams.Height: ${viewgroupFrontFlashcard.layoutParams.height}")
                viewgroupFrontFlashcard.requestLayout()
            }

            viewgroupBackFlashcard.doOnPreDraw {
                viewgroupBackFlashcard.layoutParams.height =
                    (heightRate * (viewgroupFrontFlashcard.width / 2)).toInt()
                viewgroupBackFlashcard.requestLayout()
            }
        }
    }

    private fun recalculate_ChooseFlashcardPropertyPanel_Dimens() {
        dB.apply {
            layoutChooseCardProperty.root.doOnPreDraw {
                var totalMargin: Int = viewgroupFrontFlashcard.marginTop
                log("Got Height: ${viewgroupFrontFlashcard.height}")
                log("Got LayoutParams.Height: ${viewgroupFrontFlashcard.layoutParams.height}")
                totalMargin += (viewgroupFrontFlashcard.height * FLASHCARD_WIDE_HEIGHT_RATE).toInt()

                totalMargin += 25.dp()
                (layoutChooseCardProperty.root.layoutParams as ConstraintLayout.LayoutParams).topMargin =
                    totalMargin
                layoutChooseCardProperty.root.requestLayout()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        viewModel.saveUsingHistory()
    }

    private fun getRequests() {
        requestAdd_FlashcardSet = getAddFlashcardRequest()

        val editInfo = getEditFlashcardRequest()
        if (editInfo != null) {
            requestEditFlashcard = editInfo.first
            requestEditFlashcardSet = editInfo.second
        }
    }

    override fun addViewControls() {
        dB.apply {
            // imgFlashcardRightReceiver Width : 50dp
            // imgFlashcardRightReceiver Height : 50dp
            // Check it in .xml
            imgFlashcardRightReceiver.pivotX = 25.dp().toFloat()
            imgFlashcardRightReceiver.pivotY = 25.dp().toFloat()

            imgFlashcardLeftReceiver.pivotX = 0f
            imgFlashcardLeftReceiver.pivotY = 25.dp().toFloat()

            disableEdtPronunciation()
            setUpEditTextsFeatures()
            setUpChooseLanguageRecyclerViews()

            // Choose Flashcard SET NAME Recycler View
            rcvFlashcardSetName.adapter = rcvFlashcardSetNameAdapter
            viewModel.getAllFlashcardSetWithNoCardList { flashcardSetList ->

                rcvFlashcardSetNameAdapter.setData(flashcardSetList)

                // Set up request
                val thereIs_AddRequest = (getAddFlashcardRequest() != null)
                val thereIs_EditFlashcardRequest = (getEditFlashcardRequest() != null)
                if (thereIs_AddRequest) {
                    showFlashcardSetInfoOnScreen(
                        requestAdd_FlashcardSet!!.name,
                        requestAdd_FlashcardSet!!.frontLanguage,
                        requestAdd_FlashcardSet!!.backLanguage
                    )

                    viewModel.currentFocusFlashcardSet = requestAdd_FlashcardSet!!

                } else if (thereIs_EditFlashcardRequest) {
                    showFlashcardSetInfoOnScreen(
                        requestEditFlashcardSet!!.name,
                        requestEditFlashcardSet!!.frontLanguage,
                        requestEditFlashcardSet!!.backLanguage
                    )

                    showRequestEditFlashcard_Info_OnScreen(requestEditFlashcard!!)
                    viewModel.currentFocusFlashcardSet = requestEditFlashcardSet!!

                } else {
                    val thereIsNoFlashcardSetAvailable = (flashcardSetList.size == 0)
                    if (thereIsNoFlashcardSetAvailable) {
                        var defaultSet = viewModel.getDefaultFlashcardSet()
                        txtSetName.text = defaultSet.name + " (Default)"
                        showFlashcardSetInfoOnScreen(
                            setName = defaultSet.name,
                            frontLang = defaultSet.frontLanguage,
                            backLang = defaultSet.backLanguage
                        )
                        viewModel.currentFocusFlashcardSet = defaultSet
                    } else { // There have been some Flashcard Sets and No AddFlashcardRequest from any specified set
                        val lastUsedFrontLang = viewModel.getCurrentFrontLanguage()
                        val lastUsedBackLang = viewModel.getCurrentBackLanguage()
                        val lastUsedFlashcardSet = viewModel.getLastedUseFlashcardSetName()

                        showFlashcardSetInfoOnScreen(
                            setName = lastUsedFlashcardSet
                            , frontLang = lastUsedFrontLang
                            , backLang = lastUsedBackLang
                        )

                        viewModel.currentFocusFlashcardSet =
                            FlashcardSet(lastUsedFlashcardSet, lastUsedFrontLang, lastUsedBackLang)
                    }
                }

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

    private fun setUpChooseLanguageRecyclerViews() {
        dB.apply {

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

        }
    }

    private fun showRequestEditFlashcard_Info_OnScreen(requestEditFlashcard: Flashcard) {
        dB.apply {
            edtText.setText(requestEditFlashcard.text)
            edtType.setText(requestEditFlashcard.type)
            edtPronunciation.text = requestEditFlashcard.pronunciation

            edtTranslation.setText(requestEditFlashcard.translation)
            edtExample.setText(requestEditFlashcard.example)
            edtExampleTranslation.setText(requestEditFlashcard.meanOfExample)

            if (requestEditFlashcard.illustrationPictureName != null) {
                startWaitingLoadingImageProgress()
                viewModel.loadIllustrationPicture(
                    requestEditFlashcard.illustrationPictureName!!, onGet = { ex, picture ->
                        if (ex != null) {
                            log("Load image error happens")
                            ex.printStackTrace()
                        } else {
                            if (picture == null) {
                                log("No error happens but image is null")
                                throw Exception("????")
                                // TODO ()
                            } else {
                                setPictureToCardOnScreen(picture)
                                endWaitingLoadingImageProgress()
                            }
                        }
                    })
            }
        }
    }

    private fun startWaitingLoadingImageProgress() {
        recalculateIllustrationPictureDimens(1f)
        val infiniteRotate =
            AnimationUtils.loadAnimation(this@RetrofitAddFlashcardActivity, R.anim.rotate_forever)
        infiniteRotate.duration = 300
        dB.imgLoadPictureProgressBar.goVISIBLE()
        dB.imgLoadPictureProgressBar.startAnimation(infiniteRotate)
    }

    private fun endWaitingLoadingImageProgress() {
        dB.imgLoadPictureProgressBar.animation.cancel()
        dB.imgLoadPictureProgressBar.goGONE()
    }

    private fun setUpEditTextsFeatures() {
        dB.apply {
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

    }
    }

    private fun disableEdtPronunciation() {
        dB.edtPronunciation.inputType = TYPE_NULL
    }

    private fun showFlashcardSetInfoOnScreen(
        setName: String,
        frontLang: String,
        backLang: String
    ) {
        dB.apply {
            txtSetName.text = setName
            txtSetLanguages.text = SetNameUtils.getLanguagePairForm(frontLang, backLang)
        }
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

            btnSave.setOnClickListener {
                proceedAddOrSaveFlashcard()
                userEndAdding = true
            }

            btnSaveAndContinue.setOnClickListener {
                proceedAddOrSaveFlashcard()
                userEndAdding = false
            }

            imgSwipeZone.setOnTouchListener(object :
                MyGestureDetector(this@RetrofitAddFlashcardActivity) {

                override fun onSwipeRight() {
                    log("???? R")
                }

                override fun onSwipeLeft() {
                    log("???? L")
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

            imgSetNameSpinner.setOnClickListener {
                txtSetName.performClick()
            }

            txtSetName.setOnClickListener {
                if (rcvFlashcardSetNameAdapter.itemCount > 0) {
                    showSetNameList()
                } else {
                    showButton_CreateNewSetSuggesstion()
                }
                hideScreenFocusToTranslationEditText()
            }

            btnCreateNewSetSuggestion.setOnClickListener {
                hideButton_CreateNewSetSuggesstion()
                btnStartCreateNewSet.performClick()
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
                showFlashcardSetInfoOnScreen(
                    flashcardSet.name,
                    flashcardSet.frontLanguage,
                    flashcardSet.backLanguage
                )
                viewModel.currentFocusFlashcardSet = flashcardSet
                hideSetNameList()
            }

            btnStartCreateNewSet.setOnClickListener {
                edtNewSetName.requestFocus()
                edtNewSetFrontLanguage.setText(viewModel.currentFocusFlashcardSet.frontLanguage)
                edtNewSetBackLanguage.setText(viewModel.currentFocusFlashcardSet.backLanguage)
                if (rcvFlashcardSetName.isVisible) {
                    hideSetNameList()
                }

                if (btnCreateNewSetSuggestion.isVisible) {
                    hideButton_CreateNewSetSuggesstion()
                }

                showCreateSetNamePanel()
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

            btnPanelCreateNewSet.setOnClickListener {
                val setName = edtNewSetName.text.toString()
                val frontLang = edtNewSetFrontLanguage.text.toString()
                val backLang = edtNewSetBackLanguage.text.toString()
                showFlashcardSetInfoOnScreen(setName, frontLang, backLang)
                val newSet = viewModel.createNewFlashcardSetIfValid(setName, frontLang, backLang)
                val setIsValid = (newSet != null)
                if (setIsValid) {
                    viewModel.currentFocusFlashcardSet = newSet!!
                    rcvFlashcardSetNameAdapter.addFlashcardSetAtTop(newSet)
                }
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

            btnChoosePicture.setOnClickListener {
                startWaitingLoadingImageProgress()
                loadImagesFromGallery()
            }

            vwgrpBtnBlueChooseCardProperty.setOnClickListener {
                is_InAdjustCardProperty_Mode = true

                layoutChooseCardProperty.root.goVisibleByFadeInAnim(1f)
                vwgrpBtnBlueChooseCardProperty.goGoneByFadeOutAnim()
                imgFlashcardsBackground.goGoneByFadeOutAnim()
                imgArrowUp.goGoneByFadeOutAnim()
                txtSwipeUp.goGoneByFadeOutAnim()
            }

            btnCardPropertiesOnTop.setOnClickListener {
                vwgrpBtnBlueChooseCardProperty.performClick()
            }

            layoutChooseCardProperty.apply {
                rdgrCardHeight.setOnCheckedChangeListener { view, changedId ->
                    if (changedId == rdbtnHeightNormal.id) {
                        recalculate_Flashcard_Dimens(1f)
                    } else if (changedId == rdbtnHeightWide.id) {
                        recalculate_Flashcard_Dimens(FLASHCARD_WIDE_HEIGHT_RATE)
                    }
                }

                rdgrCardFrontSide.setOnCheckedChangeListener { view, changedId ->
                    if (changedId == rdbtnFrontImageWithText.id) {
                        vwgrpFrontTexts.goVISIBLE()
                        (imgIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).endToEnd =
                            ConstraintSet.GONE
                        recalculateIllustrationPictureDimens(1f)
                        imgIllustrationPicture.requestLayout()
                    } else if (changedId == rdbtnFrontImageWithoutText.id) {
                        vwgrpFrontTexts.goGONE()
                        (imgIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).endToEnd =
                            PARENT_ID
                        (imgIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).width =
                            MATCH_PARENT
                        imgIllustrationPicture.requestLayout()
                    }
                }
            }


        }
    }

    private fun View.goGoneByFadeOutAnim() {
        animate().alpha(0.0f)
            .setDuration(100).setInterpolator(LinearInterpolator())
            .setLiteListener(onEnd = {
                goGONE()
            })

        // Reset
        animate().setLiteListener()
    }


    private fun View.goVisibleByFadeInAnim(targetAlpha: Float) {
        goVISIBLE()
        animate().alpha(targetAlpha)
            .setDuration(100).interpolator = LinearInterpolator()

        // Reset
        animate().setLiteListener()
    }


    private fun showCardTypeList() {
        dB.rcvChooseCardType.showByScaleUpAndFadeIn()
    }

    private fun hideCardTypeList() {
        dB.rcvChooseCardType.hideByScaleDownAndFadeOut()
    }

    private fun proceedAddOrSaveFlashcard() {
        dB.apply {

            if (hasStartedEdit.not()) {
                return
            }

            val text = edtText.text.toString()
            val translation = edtTranslation.text.toString()
            val example = edtExample.text.toString()
            val meanOfExample = edtExampleTranslation.text.toString()
            val type = edtType.text.toString()
            val pronunciation = edtPronunciation.text.toString()
            val frontLanguage = viewModel.currentFocusFlashcardSet.frontLanguage
            val backLanguage = viewModel.currentFocusFlashcardSet.backLanguage
            val setName = viewModel.currentFocusFlashcardSet.name

            val isEditingFlashcard = (requestEditFlashcard != null)
            val id = if (isEditingFlashcard) {
                requestEditFlashcard!!.id
            } else 0

            val newCard = Flashcard(
                id = id, text = text,
                translation = translation,
                example = example,
                meanOfExample = meanOfExample,
                type = type,
                pronunciation = pronunciation,
                frontLanguage = frontLanguage,
                backLanguage = backLanguage,
                setOwner = setName
            )

            val illustrationPicture = if (imgIllustrationPicture.drawable != null) {
                (imgIllustrationPicture.drawable as BitmapDrawable).bitmap
            } else null

            viewModel.proceedAddFlashcard(newCard, illustrationPicture)
            if (type.isNotEmpty()) {
                rcvChooseCardType.addType(type)
            }
    }}

    private fun clearAllCardInfomation () { dB.apply {
        edtText.setText("")
        edtType.setText("")
        edtPronunciation.text = ""
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
        setStatusBarColor(resources.getColor(R.color.app_darker_blue))

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
            .setLiteListener(onEnd = {
                imgTurnOffCreateNewSet.goGONE()
            })

        hideVirtualKeyboard()
    }
    }

    private fun showButton_CreateNewSetSuggesstion() {
        dB.btnCreateNewSetSuggestion.showByScaleUpAndFadeIn()
    }

    private fun hideButton_CreateNewSetSuggesstion() {
        dB.btnCreateNewSetSuggestion.hideByScaleDownAndFadeOut()
    }

    private fun showSetNameList() {
        dB.rcvFlashcardSetName.animate()
            .alpha(1f)
            .scaleX(1f).scaleY(1f)
            .setDuration(150).setInterpolator(NormalOutExtraSlowIn())
            .setLiteListener(onStart = {
                dB.rcvFlashcardSetName.goVISIBLE()
            })
    }

    private fun hideSetNameList() {
        dB.rcvFlashcardSetName.animate()
            .alpha(0f)
            .scaleX(0f).scaleY(0f)
            .setDuration(150).setInterpolator(NormalOutExtraSlowIn())
            .setLiteListener(onEnd = {
                dB.rcvFlashcardSetName.goGONE()
            })
    }

    private fun loadImagesFromGallery() {
        if (ActivityCompat.checkSelfPermission(
                this@RetrofitAddFlashcardActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@RetrofitAddFlashcardActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                100
            )
            return
        }
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val clipData = data!!.clipData
            //clip data will be null if user select one item from gallery
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val imageUri: Uri = clipData.getItemAt(i).uri
                    try {
                        val istr: InputStream? = contentResolver.openInputStream(imageUri)
                        val bitmap = BitmapFactory.decodeStream(istr)
                        setPictureToCardOnScreen(bitmap)
                        endWaitingLoadingImageProgress()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            } else {
                val imageUri: Uri? = data.data
                try {
                    val `is`: InputStream? = contentResolver.openInputStream(imageUri!!)
                    val picture = BitmapFactory.decodeStream(`is`)
                    setPictureToCardOnScreen(picture)
                    endWaitingLoadingImageProgress()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setPictureToCardOnScreen(picture: Bitmap) {
        val rate = picture.width.toFloat() / picture.height.toFloat()
        if (rate < 1) {
            recalculateIllustrationPictureDimens(rate)
        } else {
            recalculateIllustrationPictureDimens(1f)
        }

        dB.imgIllustrationPicture.setImageBitmap(picture)
    }


    private fun resetEditTextTranslationHintToNormal() {
        dB.edtTranslation.hint = "Translation"
        dB.edtTranslation.setHintTextColor(Color.parseColor("#9E9E9E"))
    }

    private fun resetTextEditTextHintToNormal() {
        dB.edtText.hint = "Text"
        dB.edtText.setHintTextColor(Color.parseColor("#9E9E9E"))
    }

    private val SAVE_ANIM_PHASE1_DURATION = 120L

    private val SAVE_ANIM_PHASE2_DURATION = 150L

    private fun playSaveAnimations(delayTime: Long = 0, onEnd: () -> Unit = {}) {
        dB.apply {
            Thread(Runnable {
                Thread.sleep(delayTime)
                runOnUiThread {
                    imgFlashcardRightReceiver.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(SAVE_ANIM_PHASE1_DURATION).interpolator =
                        NormalOutExtraSlowIn()

                    playFlashcardGoTo_Right_ReceiverAnim(onEnd = onEnd)
                }
            }).start()
        }
    }

    private fun playFlashcardGoTo_Left_ReceiverAnim(onEnd: () -> Unit) {
        val viewWillPerformSaveAnim = if (isShowingFrontCard)
            dB.viewgroupFrontFlashcard
        else
            dB.viewgroupBackFlashcard

        viewWillPerformSaveAnim.animate() // Phase 1
            .scaleX(0.1f).scaleY(0.1f)
            .translationX(-viewWillPerformSaveAnim.width * 0.1f)
            .setDuration(SAVE_ANIM_PHASE2_DURATION).setInterpolator(LinearInterpolator())
            .setLiteListener(onEnd = {
                viewWillPerformSaveAnim.animate() // Phase 2
                    .translationX(-viewWillPerformSaveAnim.width / 2f - viewWillPerformSaveAnim.marginStart * 1.2f)
                    .setDuration(200).setInterpolator(LinearInterpolator())
                    .setLiteListener(onEnd = { // Phase 3

                        val bulgeXAnimator = PropertyValuesHolder.ofFloat(SCALE_X, 1f, 1.2f, 1f)
                        val bulgeYAnimator = PropertyValuesHolder.ofFloat(SCALE_Y, 1f, 1.2f, 1f)
                        val bulgeAnimator = ObjectAnimator.ofPropertyValuesHolder(
                            dB.imgFlashcardLeftReceiver,
                            bulgeXAnimator,
                            bulgeYAnimator
                        )
                        bulgeAnimator.duration = 280
                        bulgeAnimator.interpolator = AccelerateDecelerateInterpolator()
                        bulgeAnimator.start()
                        bulgeAnimator.addListener(onEnd = {
                            onEnd.invoke()
                        })
                    })
            })
    }

    private fun playFlashcardGoTo_Right_ReceiverAnim(onEnd: () -> Unit) {
        val viewWillPerformSaveAnim = if (isShowingFrontCard)
            dB.viewgroupFrontFlashcard
        else
            dB.viewgroupBackFlashcard

        viewWillPerformSaveAnim.animate() // Phase 1
            .scaleX(0.1f).scaleY(0.1f)
            .translationX(viewWillPerformSaveAnim.width * 0.1f)
            .setDuration(SAVE_ANIM_PHASE2_DURATION).setInterpolator(LinearInterpolator())
            .setLiteListener(onEnd = {
                viewWillPerformSaveAnim.animate() // Phase 2
                    .translationX(viewWillPerformSaveAnim.width / 2f + viewWillPerformSaveAnim.marginEnd * 1.2f)
                    .setDuration(200).setInterpolator(LinearInterpolator())
                    .setLiteListener(onEnd = { // Phase 3

                        val bulgeXAnimator = PropertyValuesHolder.ofFloat(SCALE_X, 1f, 1.2f, 1f)
                        val bulgeYAnimator = PropertyValuesHolder.ofFloat(SCALE_Y, 1f, 1.2f, 1f)
                        val bulgeAnimator = ObjectAnimator.ofPropertyValuesHolder(
                            dB.imgFlashcardRightReceiver,
                            bulgeXAnimator,
                            bulgeYAnimator
                        )
                        bulgeAnimator.duration = 280
                        bulgeAnimator.interpolator = AccelerateDecelerateInterpolator()
                        bulgeAnimator.start()
                        bulgeAnimator.addListener(onEnd = {
                            onEnd.invoke()
                        })
                    })
            })
    }


    private fun startEditting () { dB.apply {
        vwgrpSwipeFunctions.animate().alpha(1f).setDuration(100)
            .setLiteListener(onStart = {
                vwgrpSwipeFunctions.goVISIBLE()
            })

        imgArrowUp.animate().alpha(0f).setDuration(100)
            .setLiteListener(onEnd = {
                imgArrowUp.goGONE()
            })

        txtSwipeUp.animate().alpha(0f).setDuration(100)
            .setLiteListener(onEnd = {
                txtSwipeUp.goGONE()
            })

        imgFlashcardsBackground.animate().alpha(0f).setDuration(100)
            .setLiteListener(onEnd = {
                imgFlashcardsBackground.goGONE()
            })

        vwgrpBtnBlueChooseCardProperty.goGONE()
        btnCardPropertiesOnTop.goVISIBLE()
        txtCardPropertiesOnTop.goVISIBLE()

        hasStartedEdit = true

    }}

    private fun onUserFlipCard() {
        if (isShowingFrontCard) {
            showBackCard_AndUpdateStatus()
            isShowingFrontCard = false
            dB.edtTranslation.requestFocus()

        } else { // is showing back card
            showFrontCard()
            dB.edtText.requestFocus()
        }
    }


    private fun showFrontCard() {
        dB.apply {
            viewgroupBackFlashcard.startAnimation(turnUpFrontCard_After_TurnDownBackCard)
            isShowingFrontCard = true
        }
    }

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
            } else if (is_InAdjustCardProperty_Mode) {
                is_InAdjustCardProperty_Mode = false

                layoutChooseCardProperty.root.goGoneByFadeOutAnim()

                vwgrpBtnBlueChooseCardProperty.goVisibleByFadeInAnim(1f)
                imgFlashcardsBackground.goVisibleByFadeInAnim(0.35f)
                imgArrowUp.goVisibleByFadeInAnim(1f)
                txtSwipeUp.goVisibleByFadeInAnim(1f)
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

        val flashcardSet = bundle.getSerializable(ADD_REQUEST_FLASHCARD_SET) as FlashcardSet?
        return flashcardSet
    }

    private fun getEditFlashcardRequest(): Pair<Flashcard, FlashcardSet>? {
        val bundle = intent.extras

        if (bundle == null) {
            return null
        }

        val flashcard = bundle.getSerializable(EDIT_REQUEST_FLASHCARD) as Flashcard?
        val flashcardSet = bundle.getSerializable(EDIT_REQUEST_FLASHCARD_SET) as FlashcardSet?

        if ((flashcard == null) || (flashcardSet == null)) {
            return null
        }

        return Pair(flashcard, flashcardSet)

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
                    ipaKeyboard.animate().alpha(1f).duration = 200
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
            showFrontCard()
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
        dB.vwgrpCreateSetError.animate().alpha(1f).duration = 200
    }

    fun hideInvalidFlashcardSetError () {
        dB.vwgrpCreateSetError.animate().alpha(0f).setDuration(200).setLiteListener (onEnd = {
            dB.vwgrpCreateSetError.goGONE()
        })
    }

    override fun onAddFlashcardSuccess() { dB.apply {
        if (userEndAdding) {
            hideVirtualKeyboard()
            playSaveAnimations(delayTime = KEYBOARD_DISAPPEAR_INTERVAL, onEnd = {
                finish()
            })
            currentFocus?.clearFocus()
        } else { // User continues adding another flashcard
            playSaveAnimationButKeepAdding(onEnd = {
                resetFrontCardState()
                resetBackCardState()
                isShowingFrontCard = true

                clearAllCardInfomation()

                hideLeftFlashcardReceiver()
                playResetFlashcardAnim()

                edtText.requestFocus()
            })
        }
    }
    }

    private fun playSaveAnimationButKeepAdding(delayTime: Long = 0, onEnd: () -> Unit = {}) {
        dB.apply {
            Thread(Runnable {
                Thread.sleep(delayTime)
                runOnUiThread {
                    imgFlashcardLeftReceiver.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(SAVE_ANIM_PHASE1_DURATION).interpolator =
                        NormalOutExtraSlowIn()

                    playFlashcardGoTo_Left_ReceiverAnim(onEnd = onEnd)
                }
            }).start()
        }
    }


    private fun hideLeftFlashcardReceiver() {
        dB.imgFlashcardLeftReceiver.animate()
            .scaleX(0f).scaleY(0f)
            .setDuration(200L).interpolator = LinearInterpolator()
    }

    private fun playResetFlashcardAnim() {
        dB.apply {
            viewgroupFrontFlashcard.alpha = 0f
            viewgroupFrontFlashcard.scaleX = 0.6f
            viewgroupFrontFlashcard.scaleY = 0.6f
            viewgroupFrontFlashcard.animate()
                .alpha(1f)
                .scaleX(1f).scaleY(1f)
                .setDuration(200L).interpolator = NormalOutExtraSlowIn()
        }
    }

    private fun resetFrontCardState() {
        dB.apply {
            viewgroupBackFlashcard.translationX = 0f
            viewgroupBackFlashcard.scaleX = 1f
            viewgroupBackFlashcard.scaleY = 1f
            viewgroupFrontFlashcard.goVISIBLE()
        }
    }

    private fun resetBackCardState() {
        dB.apply {
            viewgroupBackFlashcard.translationX = 0f
            viewgroupBackFlashcard.scaleX = 1f
            viewgroupBackFlashcard.scaleY = 1f
            viewgroupBackFlashcard.goINVISIBLE()
        }
    }

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

