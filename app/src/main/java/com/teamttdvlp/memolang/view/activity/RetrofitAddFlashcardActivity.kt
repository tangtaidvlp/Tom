package com.teamttdvlp.memolang.view.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
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
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.InputType.TYPE_NULL
import android.view.View
import android.view.View.SCALE_X
import android.view.View.SCALE_Y
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.*
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
import androidx.core.animation.addListener
import androidx.core.app.ActivityCompat
import androidx.core.view.*
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.*
import com.teamttdvlp.memolang.databinding.ActivityAddFlashcardRetroBinding
import com.teamttdvlp.memolang.view.activity.iview.AddFlashcardView
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCVRecentUsedLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCV_SimpleChooseFlashcardSetAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.NormalOutExtraSlowIn
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.AddFlashCardViewModel
import java.io.FileNotFoundException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Named


const val KEYBOARD_DISAPPEAR_INTERVAL = 50L

const val KEYBOARD_APPEAR_INTERVAL = 150L

class RetrofitAddFlashcardActivity :
    BaseActivity<ActivityAddFlashcardRetroBinding, AddFlashCardViewModel>()
    , AddFlashcardView {

    companion object {

        private val ADD_REQUEST_FLASHCARD_SET = "add_set_name"

        private val EDIT_REQUEST_FLASHCARD_SET = "edit_set_name"

        private val EDIT_REQUEST_FLASHCARD = "nef"

        enum class FlipDirection {
            FLIP_UP, FLIP_DOWN
        }

        fun requestAddLanguage(packageContext: Context, deck: Deck) {
            val intent = Intent(packageContext, RetrofitAddFlashcardActivity::class.java)
            intent.putExtra(ADD_REQUEST_FLASHCARD_SET, deck)
            packageContext.startActivity(intent)
        }

        fun requestEditFlashcard(
            packageContext: Context,
            flashcard: Flashcard,
            deck: Deck
        ) {
            val intent = Intent(packageContext, RetrofitAddFlashcardActivity::class.java)
            intent.putExtra(EDIT_REQUEST_FLASHCARD, flashcard)
            intent.putExtra(EDIT_REQUEST_FLASHCARD_SET, deck)
            packageContext.startActivity(intent)
        }
    }

    private val LOAD_PICTURE_REQUEST_CODE = 1

    private var isIPAKeyboardVisible = false

    private var isShowingFrontCard = true

    private var userEndAdding = false

    private var setEdtTypeCausedByEdit = false

    private var currentCardProperty: CardProperty = CardProperty()

    private lateinit var turnUpFrontCard_After_TurnDownBackCard: Animation

    private lateinit var turnUpBackCard_After_TurnDownFrontCard: Animation

    private var front_Up_To_Back_EffectAnmtrSet: AnimatorSet = AnimatorSet()

    private var front_Down_To_Back_EffectAnmtrSet: AnimatorSet = AnimatorSet()

    private var back_Up_To_Front_EffectAnmtrSet: AnimatorSet = AnimatorSet()

    private var back_Down_To_Front_EffectAnmtrSet: AnimatorSet = AnimatorSet()

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

    private lateinit var front_illustrationDefaultDrawable: Drawable

    private lateinit var back_illustrationDefaultDrawable: Drawable

    private var requestEditFlashcard: Flashcard? = null

    private var requestEditDeck: Deck? = null

    private var requestAdd_Deck: Deck? = null

    private var is_InAdjustCardProperty_Mode = false

    override fun getLayoutId(): Int = R.layout.activity_add_flashcard_retro

    override fun takeViewModel(): AddFlashCardViewModel {
        return getActivityViewModel(viewModelProviderFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(resources.getColor(R.color.extreme_dark_blue))
        viewModel.setUpView(this)
        getRequests()
        if (requestEditFlashcard != null) {
            showVirtualKeyboard()
            dB.edtText.requestFocus()
        }

        recalculate_Flashcard_Dimens(1f)
        recalculate_ChooseFlashcardPropertyPanel_Dimens()
        recalculate_FunctionsPanel_Dimens()
        performStartUpAnimation()
    }

    private val COMMON_START_UP_ANIM_DURATION = 300L
    private val COMMON_START_UP_ANIM_DELAY = 300L

    private fun performStartUpAnimation() {
        dB.layoutFunctionsPanel.apply {
            val goUpButtonPopUp =
                AnimationUtils.loadAnimation(this@RetrofitAddFlashcardActivity, R.anim.scale_up)
            goUpButtonPopUp.interpolator = OvershootInterpolator(3f)
            goUpButtonPopUp.duration = COMMON_START_UP_ANIM_DURATION
            goUpButtonPopUp.startOffset = COMMON_START_UP_ANIM_DELAY
            vwgrpBtnGoUp.startAnimation(goUpButtonPopUp)

            val flipButtonPopUp =
                AnimationUtils.loadAnimation(this@RetrofitAddFlashcardActivity, R.anim.scale_up)
            flipButtonPopUp.interpolator = OvershootInterpolator(3f)
            flipButtonPopUp.duration = COMMON_START_UP_ANIM_DURATION
            flipButtonPopUp.startOffset = COMMON_START_UP_ANIM_DELAY + 60
            vwgrpBtnFlip.startAnimation(flipButtonPopUp)

            val saveButtonPopUp =
                AnimationUtils.loadAnimation(this@RetrofitAddFlashcardActivity, R.anim.scale_up)
            saveButtonPopUp.interpolator = OvershootInterpolator(3f)
            saveButtonPopUp.duration = COMMON_START_UP_ANIM_DURATION
            saveButtonPopUp.startOffset = COMMON_START_UP_ANIM_DELAY + 160
            vwgrpBtnSave.startAnimation(saveButtonPopUp)

            val continueButtonPopUp =
                AnimationUtils.loadAnimation(this@RetrofitAddFlashcardActivity, R.anim.scale_up)
            continueButtonPopUp.interpolator = OvershootInterpolator(3f)
            continueButtonPopUp.duration = COMMON_START_UP_ANIM_DURATION
            continueButtonPopUp.startOffset = COMMON_START_UP_ANIM_DELAY + 140
            vwgrpBtnContinue.startAnimation(continueButtonPopUp)

            val cardPropButtonPopUp =
                AnimationUtils.loadAnimation(this@RetrofitAddFlashcardActivity, R.anim.scale_up)
            cardPropButtonPopUp.interpolator = OvershootInterpolator(3f)
            cardPropButtonPopUp.duration = COMMON_START_UP_ANIM_DURATION
            cardPropButtonPopUp.startOffset = COMMON_START_UP_ANIM_DELAY + 200
            vwgrpBtnCardProperties.startAnimation(cardPropButtonPopUp)

            val newDeckButtonPopUp =
                AnimationUtils.loadAnimation(this@RetrofitAddFlashcardActivity, R.anim.scale_up)
            newDeckButtonPopUp.interpolator = OvershootInterpolator(3f)
            newDeckButtonPopUp.duration = COMMON_START_UP_ANIM_DURATION
            newDeckButtonPopUp.startOffset = COMMON_START_UP_ANIM_DELAY + 230
            vwgrpBtnNewDeck.startAnimation(newDeckButtonPopUp)
        }
    }


    override fun initProperties() {
        front_illustrationDefaultDrawable = dB.imgFrontIllustrationPicture.drawable
        back_illustrationDefaultDrawable = dB.imgBackIllustrationPicture.drawable
    }

    private fun recalculateFront_IllustrationPictureWidth_ByHeightRate(heightToWidthRate: Float) {
        dB.apply {
            imgFrontIllustrationPicture.doOnPreDraw {
                imgFrontIllustrationPicture.layoutParams.width =
                    (imgFrontIllustrationPicture.height * heightToWidthRate).toInt()
                imgFrontIllustrationPicture.requestLayout()
            }
        }
    }

    private fun recalculateBack_IllustrationPictureWidth_ByHeightRate(heightToWidthRate: Float) {
        dB.apply {
            imgBackIllustrationPicture.doOnPreDraw {
                imgBackIllustrationPicture.layoutParams.width =
                    (imgBackIllustrationPicture.height * heightToWidthRate).toInt()
                imgBackIllustrationPicture.requestLayout()
            }
        }
    }

    private fun setFront_IllustrationPictureWidth(width: Int) {
        dB.apply {
            imgFrontIllustrationPicture.layoutParams.width = width
            imgFrontIllustrationPicture.requestLayout()
        }
    }

    private fun setBack_IllustrationPictureWidth(width: Int) {
        dB.apply {
            imgBackIllustrationPicture.layoutParams.width = width
            imgBackIllustrationPicture.requestLayout()
        }
    }

    private fun recalculate_Flashcard_Dimens(heightRate: Float) {
        systemOutLogging("Recalculation flashcard dimens")
        dB.apply {
            viewgroupFrontFlashcard.doOnPreDraw {
                viewgroupFrontFlashcard.layoutParams.height =
                    (heightRate * (viewgroupFrontFlashcard.width / 2)).toInt()
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
                systemOutLogging("Got Height: ${viewgroupFrontFlashcard.height}")
                systemOutLogging("Got LayoutParams.Height: ${viewgroupFrontFlashcard.layoutParams.height}")
                totalMargin += (viewgroupFrontFlashcard.height * FLASHCARD_WIDE_HEIGHT_RATE).toInt()

                totalMargin += 25.dp()
                (layoutChooseCardProperty.root.layoutParams as ConstraintLayout.LayoutParams).topMargin =
                    totalMargin
                layoutChooseCardProperty.root.requestLayout()
            }
        }
    }

    private fun recalculate_FunctionsPanel_Dimens() {
        dB.layoutFunctionsPanel.apply {
            root.doOnPreDraw {
                root.layoutParams.height = root.width
                root.requestLayout()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveUsingHistory()
    }

    private fun getRequests() {
        requestAdd_Deck = getAddFlashcardRequest()

        val editInfo = getEditFlashcardRequest()
        if (editInfo != null) {
            requestEditFlashcard = editInfo.first
            requestEditDeck = editInfo.second
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
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
                        requestAdd_Deck!!.name,
                        requestAdd_Deck!!.frontLanguage,
                        requestAdd_Deck!!.backLanguage
                    )

                    viewModel.currentFocusDeck = requestAdd_Deck!!

                } else if (thereIs_EditFlashcardRequest) {
                    showFlashcardSetInfoOnScreen(
                        requestEditDeck!!.name,
                        requestEditDeck!!.frontLanguage,
                        requestEditDeck!!.backLanguage
                    )
                    setEdtTypeCausedByEdit = true
                    showRequestEditFlashcard_Info_OnScreen(requestEditFlashcard!!)
                    viewModel.currentFocusDeck = requestEditDeck!!
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
                        viewModel.currentFocusDeck = defaultSet
                    } else { // There have been some Flashcard Sets and No AddFlashcardRequest from any specified set
                        val lastUsedFrontLang = viewModel.getCurrentFrontLanguage()
                        val lastUsedBackLang = viewModel.getCurrentBackLanguage()
                        val lastUsedFlashcardSet = viewModel.getLastedUseFlashcardSetName()

                        showFlashcardSetInfoOnScreen(
                            setName = lastUsedFlashcardSet
                            , frontLang = lastUsedFrontLang
                            , backLang = lastUsedBackLang
                        )

                        viewModel.currentFocusDeck =
                            Deck(lastUsedFlashcardSet, lastUsedFrontLang, lastUsedBackLang)
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

            setCardProperty(requestEditFlashcard.cardProperty)
            if (requestEditFlashcard.frontIllustrationPictureName != null) {
                startWaitingLoadingImageProgress()
                viewModel.loadIllustrationPicture(
                    name = requestEditFlashcard.frontIllustrationPictureName!!,
                    onGet = { ex, picture ->
                        if (ex != null) {
                            systemOutLogging("Load image error happens")
                            ex.printStackTrace()
                        } else {
                            if (picture == null) {
                                systemOutLogging("No error happens but image is null")
                                throw Exception("????")
                                // TODO ()
                            } else {
                                setFront_IllustrationAndAdjustRatio(picture)
                                if (currentCardProperty.frontSideHasText.not()) {
                                    showFrontSide_ImageOnly()
                                }
                                endFront_LoadImageProgressBar()
                            }
                        }
                    })
            }

            if (requestEditFlashcard.backIllustrationPictureName != null) {
                viewModel.loadIllustrationPicture(
                    name = requestEditFlashcard.backIllustrationPictureName!!,
                    onGet = { ex, picture ->
                        if (ex != null) {
                            systemOutLogging("Load image error happens")
                            ex.printStackTrace()
                        } else {
                            if (picture == null) {
                                systemOutLogging("No error happens but image is null")
                                throw Exception("????")
                                // TODO ()
                            } else {
                                setBack_IllustrationAndAdjustRatio(picture)
                                if (currentCardProperty.backSideHasText.not()) {
                                    showBackSide_ImageOnly()
                                }
                                endBack_LoadImageProgressBar()
                            }
                        }
                    })
            }
        }
    }

    private fun setCardProperty(cardProperty: CardProperty) {
        dB.apply {
            if (cardProperty.heightOption == HEIGHT_WIDE) {
                recalculate_Flashcard_Dimens(FLASHCARD_WIDE_HEIGHT_RATE)
            }

            if (cardProperty.frontSideHasText and cardProperty.frontSideHasImage) {
                if (requestEditFlashcard != null) {
                    if (requestEditFlashcard!!.frontIllustrationPictureName != null) {
                        showFrontSide_BothTextAndImage()
                    } else {
                        showFrontSide_TextOnly()
                    }
                }
            } else if (cardProperty.frontSideHasImage.not()) {
                showFrontSide_TextOnly()
            } else if (cardProperty.frontSideHasText.not()) {
                showFrontSide_ImageOnly()
            }

            if (cardProperty.backSideHasText and cardProperty.backSideHasImage) {
                if (requestEditFlashcard != null) {
                    if (requestEditFlashcard!!.backIllustrationPictureName != null) {
                        showBackSide_BothTextAndImage()
                    } else {
                        showBackSide_TextOnly()
                    }
                }
            } else if (cardProperty.backSideHasImage.not()) {
                showBackSide_TextOnly()
            } else if (cardProperty.backSideHasText.not()) {
                showBackSide_ImageOnly()
            }
        }
    }

    private fun startWaitingLoadingImageProgress() {
        if (currentCardProperty.frontSideHasText) {
            recalculateFront_IllustrationPictureWidth_ByHeightRate(1f)
        }
        val infiniteRotate =
            AnimationUtils.loadAnimation(this@RetrofitAddFlashcardActivity, R.anim.rotate_forever)
        infiniteRotate.duration = 300
        dB.imgFrontLoadPictureProgressBar.goVISIBLE()
        dB.imgFrontLoadPictureProgressBar.startAnimation(infiniteRotate)
    }

    private fun endFront_LoadImageProgressBar() {
        if (dB.imgFrontLoadPictureProgressBar.animation != null) {
            dB.imgFrontLoadPictureProgressBar.animation.cancel()
        }

        dB.imgFrontLoadPictureProgressBar.goGONE()
    }

    private fun endBack_LoadImageProgressBar() {
        if (dB.imgBackLoadPictureProgressBar.animation != null) {
            dB.imgBackLoadPictureProgressBar.animation.cancel()
        }

        dB.imgBackLoadPictureProgressBar.goGONE()
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

            edtText.addTextChangeListener { text, i, i2, i3 ->
                if (text.isNotEmpty()) {
                    resetTextEditTextHintToNormal()
                }
            }

            edtTranslation.addTextChangeListener { text, i1, i2, i3 ->
                if (text.isNotEmpty()) {
                    resetEditTextTranslationHintToNormal()
                }
            }

            edtPronunciation.setOnClickListener {
                showIPAKeyboard()
                hideScreenFocusToTranslationEditText()
            }

            ipaKeyboard.setOnDeviceVirtualKeyboardShow {
                hideIPAKeyboard()
            }

            ipaKeyboard.setOnBtnDoneClickListener {
                flipFrontToBackCard()
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

            val mashmallowAnim = AnimationUtils.loadAnimation(
                this@RetrofitAddFlashcardActivity,
                R.anim.mashmallow_effect
            )
            mashmallowAnim.interpolator = FastOutLinearInInterpolator()
            layoutFunctionsPanel.apply {

                vwgrpBtnSave.setOnClickListener {
                    imgSave.startAnimation(mashmallowAnim)
                    proceedAddOrSaveFlashcard()
                    userEndAdding = true
                }

                vwgrpBtnContinue.setOnClickListener {
                    imgContinue.startAnimation(mashmallowAnim)
                    proceedAddOrSaveFlashcard()
                    userEndAdding = false
                }

                vwgrpBtnFlip.setOnClickListener {
                    imgFlip.startAnimation(mashmallowAnim)
                    if (isShowingFrontCard) {
                        flipFrontToBackCard(FlipDirection.FLIP_UP)
                        isShowingFrontCard = false
                        edtTranslation.requestFocus()
                    } else {
                        flipBackToFrontCard(FlipDirection.FLIP_DOWN)
                        isShowingFrontCard = true
                        edtText.requestFocus()
                    }
                }

                vwgrpBtnGoUp.setOnClickListener {
                    imgGoUp.startAnimation(mashmallowAnim)
                    showVirtualKeyboard()
                    if (isShowingFrontCard) {
                        edtText.requestFocus()
                    } else {
                        edtTranslation.requestFocus()
                    }
                }

                vwgrpBtnNewDeck.setOnClickListener {
                    imgNewDeck.startAnimation(mashmallowAnim)
                    edtNewSetName.requestFocus()
                    edtNewSetFrontLanguage.setText(viewModel.currentFocusDeck.frontLanguage)
                    edtNewSetBackLanguage.setText(viewModel.currentFocusDeck.backLanguage)
                    if (rcvFlashcardSetName.isVisible) {
                        hideSetNameList()
                    }

                    if (btnCreateNewSetSuggestion.isVisible) {
                        hideButton_CreateNewSetSuggesstion()
                    }

                    showCreateSetNamePanel()
                }

                vwgrpBtnCardProperties.setOnClickListener {
                    imgCardProps.startAnimation(mashmallowAnim)
                    btnFrontChoosePicture.goGoneByFadeOutAnim()
                    btnBackChoosePicture.goGoneByFadeOutAnim()
                    show_ChooseCardPropertyByFadeIn()
                    hide_SaveFunctionsComponentsByFadeOut()
                }
            }

//            vwgrpBtnSave.setOnClickListener {
//                proceedAddOrSaveFlashcard()
//                userEndAdding = true
//            }
//
//            vwgrpBtnSaveAndContinue.
//
//            imgSwipeZone.setOnTouchListener(object :
//                MyGestureDetector(this@RetrofitAddFlashcardActivity) {
//
//                override fun onSwipeRight() {
//
//                }
//
//                override fun onSwipeLeft() {
//
//                }
//
//                override fun onSwipeUp() {
//                    if (hasStartedEdit.not()) {
//                        showVirtualKeyboard()
//                        edtText.requestFocus()
//                        startEditting()
//                    } else {
//                        showVirtualKeyboard()
//                        if (isShowingFrontCard and edtText.text.isEmpty()) {
//                            edtText.requestFocus()
//                            return
//                        }
//
//                        if (isShowingFrontCard.not() and edtTranslation.text.isEmpty()) {
//                            edtTranslation.requestFocus()
//                            return
//                        }
//
//                        onUserFlipCard(FlipDirection.FLIP_UP)
//                    }
//                }
//
//                override fun onSwipeDown() {
//                    if (hasStartedEdit) {
//                        onUserFlipCard(FlipDirection.FLIP_DOWN)
//                        showVirtualKeyboard()
//                    }
//                }
//            })

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
//                btnStartCreateNewSet.performClick()
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
                // Because when edit a card
                // We call edtType.setText in showRequestEditFlashcard_Info_OnScreen()
                // this trigger TextChangeListener too
                // Create setTextCausedByEdit to solve this problem
                if (setEdtTypeCausedByEdit) {
                    setEdtTypeCausedByEdit = false
                    return@addTextChangeListener
                }
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
                viewModel.currentFocusDeck = flashcardSet
                hideSetNameList()
            }

//            btnStartCreateNewSet.setOnClickListener {
//                edtNewSetName.requestFocus()
//                edtNewSetFrontLanguage.setText(viewModel.currentFocusDeck.frontLanguage)
//                edtNewSetBackLanguage.setText(viewModel.currentFocusDeck.backLanguage)
//                if (rcvFlashcardSetName.isVisible) {
//                    hideSetNameList()
//                }
//
//                if (btnCreateNewSetSuggestion.isVisible) {
//                    hideButton_CreateNewSetSuggesstion()
//                }
//
//                showCreateSetNamePanel()
//            }

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
                    viewModel.currentFocusDeck = newSet!!
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

            btnFrontChoosePicture.setOnClickListener {
                imgFrontLoadPictureProgressBar.goVISIBLE()
                btnFrontChoosePicture.goGoneByFadeOutAnim()
                if (currentCardProperty.frontSideHasText)
                    showFrontSide_BothTextAndImage()
                loadImagesFromGallery()
            }

            imgFrontIllustrationPicture.setOnClickListener {
                btnFrontChoosePicture.performClick()
            }

            btnBackChoosePicture.setOnClickListener {
                dB.imgBackLoadPictureProgressBar.goVISIBLE()
                btnBackChoosePicture.goGoneByFadeOutAnim()
                if (currentCardProperty.backSideHasText)
                    showBackSide_BothTextAndImage()
                loadImagesFromGallery()
            }

            imgBackIllustrationPicture.setOnClickListener {
                btnBackChoosePicture.performClick()
            }

//            vwgrpBtnBlueChooseCardProperty.setOnClickListener {
//                btnFrontChoosePicture.goGoneByFadeOutAnim()
//                btnBackChoosePicture.goGoneByFadeOutAnim()
//                show_ChooseCardPropertyByFadeIn()
//                hide_StartEditComponentsByFadeOut()
//            }
//
//            btnCardPropertiesOnTop.setOnClickListener {
//                btnFrontChoosePicture.goGoneByFadeOutAnim()
//                btnBackChoosePicture.goGoneByFadeOutAnim()
//                show_ChooseCardPropertyByFadeIn()
//                hide_SaveFunctionsComponentsByFadeOut()
//            }

            layoutChooseCardProperty.apply {
                rdgrCardHeight.setOnCheckedChangeListener { view, changedId ->
                    if (changedId == rdbtnHeightNormal.id) {
                        recalculate_Flashcard_Dimens(1f)
                        currentCardProperty.heightOption = HEIGHT_NORMAL
//                        txtSave.goVISIBLE()
//                        txtContinue.goVISIBLE()
                    } else if (changedId == rdbtnHeightWide.id) {
                        recalculate_Flashcard_Dimens(FLASHCARD_WIDE_HEIGHT_RATE)
                        currentCardProperty.heightOption = HEIGHT_WIDE
//                        txtSave.goGONE()
//                        txtContinue.goGONE()
                    }
                }

                rdgrCardFrontSide.setOnCheckedChangeListener { v, checkedId ->
                    if (checkedId == rdbtnFrontNormalMode.id) {
                        showFrontSide_BothTextAndImage()
                        currentCardProperty.frontSideHasText = true
                        currentCardProperty.frontSideHasImage = true
                    } else if (checkedId == rdbtnFrontImageOnly.id) {
                        showFrontSide_ImageOnly()
                        currentCardProperty.frontSideHasText = false
                        currentCardProperty.frontSideHasImage = true
                    }
                }

                rdgrCardBackSide.setOnCheckedChangeListener { v, checkedId ->
                    if (checkedId == rdbtnBackNormalMode.id) {
                        showBackSide_BothTextAndImage()
                        currentCardProperty.backSideHasText = true
                        currentCardProperty.backSideHasImage = true
                    } else if (checkedId == rdbtnBackImageOnly.id) {
                        showBackSide_ImageOnly()
                        currentCardProperty.backSideHasText = false
                        currentCardProperty.backSideHasImage = true
                    }
                }

            }
        }
    }

    private fun show_ChooseCardPropertyByFadeIn() {
        dB.apply {
            is_InAdjustCardProperty_Mode = true
            imgFrontIllustrationPicture.setBackgroundColor(Color.parseColor("#10000000"))
            hideVirtualKeyboard()
            showFront_DemoIllustration()
            showBack_DemoIllustration()
            layoutChooseCardProperty.root.goVisibleByFadeInAnim(1f)
        }
    }

    private fun showBack_DemoIllustration() {
        dB.apply {
            if (currentCardProperty.backSideHasImage) {
                if (currentCardProperty.backSideHasText) {
                    showBackSide_BothTextAndImage()
                } else {
                    showBackSide_ImageOnly()
                }
            }
        }
    }

    private fun showFront_DemoIllustration() {
        dB.apply {
            if (currentCardProperty.frontSideHasImage) {
                if (currentCardProperty.frontSideHasText) {
                    showFrontSide_BothTextAndImage()
                } else {
                    showFrontSide_ImageOnly()
                }
            }
        }
    }

    private fun hide_SaveFunctionsComponentsByFadeOut() {
        dB.apply {
            layoutFunctionsPanel.root.goGoneByFadeOutAnim()
        }
    }

    private fun showFrontSide_TextOnly() {
        dB.apply {
            (imgFrontIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).endToEnd =
                ConstraintSet.GONE
            setFront_IllustrationPictureWidth(0)
            vwgrpFrontTexts.goVISIBLE()
        }
    }

    private fun showFrontSide_ImageOnly() {
        dB.apply {
            vwgrpFrontTexts.goGONE()
            (imgFrontIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).endToEnd =
                PARENT_ID
            (imgFrontIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).width =
                MATCH_PARENT
            imgFrontIllustrationPicture.requestLayout()
        }
    }

    private fun showFrontSide_BothTextAndImage() {
        dB.apply {
            vwgrpFrontTexts.goVISIBLE()
            (imgFrontIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).endToEnd =
                ConstraintSet.GONE
            setFront_IllustrationPictureWidth((viewgroupFrontFlashcard.width - 2 * imgFrontIllustrationPicture.marginStart) / 2)
            imgFrontIllustrationPicture.requestLayout()
        }
    }


    // Back

    private fun showBackSide_TextOnly() {
        dB.apply {
            (imgBackIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).endToEnd =
                ConstraintSet.GONE
            setBack_IllustrationPictureWidth(0)
            vwgrpBackTexts.goVISIBLE()
        }
    }

    private fun showBackSide_ImageOnly() {
        dB.apply {
            vwgrpBackTexts.goGONE()
            (imgBackIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).endToEnd =
                PARENT_ID
            (imgBackIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).width =
                MATCH_PARENT
            imgBackIllustrationPicture.requestLayout()
        }
    }

    private fun showBackSide_BothTextAndImage() {
        dB.apply {
            vwgrpBackTexts.goVISIBLE()
            (imgBackIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).endToEnd =
                ConstraintSet.GONE
            setBack_IllustrationPictureWidth((viewgroupFrontFlashcard.width - 2 * imgFrontIllustrationPicture.marginStart) / 2)
            imgBackIllustrationPicture.requestLayout()
        }
    }


    private fun View.goGoneByFadeOutAnim() {
        animate().alpha(0.0f)
            .setDuration(100).setInterpolator(LinearInterpolator())
            .setLiteListener(onEnd = {
                goGONE()

                // Reset
                animate().setLiteListener()
            })

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

            val text = edtText.text.toString()
            val translation = edtTranslation.text.toString()
            val example = edtExample.text.toString()
            val meanOfExample = edtExampleTranslation.text.toString()
            val type = edtType.text.toString()
            val pronunciation = edtPronunciation.text.toString()
            val frontLanguage = viewModel.currentFocusDeck.frontLanguage
            val backLanguage = viewModel.currentFocusDeck.backLanguage
            val setName = viewModel.currentFocusDeck.name

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
                setOwner = setName,
                cardProperty = currentCardProperty
            )

            val front_IllustrationPicture =
                if (imgFrontIllustrationPicture.drawable != front_illustrationDefaultDrawable) {
                    (imgFrontIllustrationPicture.drawable as BitmapDrawable).bitmap
                } else null

            val back_IllustrationPicture =
                if (imgBackIllustrationPicture.drawable != back_illustrationDefaultDrawable) {
                    (imgBackIllustrationPicture.drawable as BitmapDrawable).bitmap
                } else null

            viewModel.proceedAddFlashcard(
                newCard,
                front_IllustrationPicture,
                back_IllustrationPicture
            )
            if (type.isNotEmpty()) {
                rcvChooseCardType.addType(type)
            }
        }
    }

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
        startActivityForResult(intent, LOAD_PICTURE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == LOAD_PICTURE_REQUEST_CODE) {

                if (resultCode != Activity.RESULT_OK) {
                    endFront_LoadImageProgressBar()
                    quickToast("Can not load picture")
                    return
                }

                val clipData = data!!.clipData
                //clip data will be null if user select one item from gallery
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val imageUri: Uri = clipData.getItemAt(i).uri
                        try {
                            val istr: InputStream? = contentResolver.openInputStream(imageUri)
                            val bitmap = BitmapFactory.decodeStream(istr)
                            onGetIllustration(bitmap)
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    val imageUri: Uri? = data.data
                    try {
                        val pictureInputStream: InputStream? =
                            contentResolver.openInputStream(imageUri!!)
                        val picture = BitmapFactory.decodeStream(pictureInputStream)
                        onGetIllustration(picture)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (ex: Exception) {
            dB.edtType.setText(ex.message)
        }
    }

    private fun onGetIllustration(bitmap: Bitmap) {
        if (isShowingFrontCard) {
            setFront_IllustrationAndAdjustRatio(bitmap)
            endFront_LoadImageProgressBar()
        } else { // Back
            setBack_IllustrationAndAdjustRatio(bitmap)
            endBack_LoadImageProgressBar()
        }
    }

    private fun setFront_IllustrationAndAdjustRatio(picture: Bitmap) {

        if (currentCardProperty.frontSideHasText) {
            val rate = picture.width.toFloat() / picture.height.toFloat()
            if (rate < 1) {
                recalculateFront_IllustrationPictureWidth_ByHeightRate(rate)
            } else {
                recalculateFront_IllustrationPictureWidth_ByHeightRate(1f)
            }
        }

        dB.imgFrontIllustrationPicture.setPadding(0)
        dB.imgFrontIllustrationPicture.setImageBitmap(picture)
    }

    private fun setBack_IllustrationAndAdjustRatio(picture: Bitmap) {

        if (currentCardProperty.backSideHasText) {
            val rate = picture.width.toFloat() / picture.height.toFloat()
            if (rate < 1) {
                recalculateBack_IllustrationPictureWidth_ByHeightRate(rate)
            } else {
                recalculateBack_IllustrationPictureWidth_ByHeightRate(1f)
            }
        }

        dB.imgBackIllustrationPicture.setPadding(0)
        dB.imgBackIllustrationPicture.setImageBitmap(picture)
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

    private fun onUserFlipCard(flipDirection: FlipDirection) {
        if (isShowingFrontCard) {
            flipFrontToBackCard(flipDirection)
            isShowingFrontCard = false
            dB.edtTranslation.requestFocus()
        } else { // is showing back card
            flipBackToFrontCard(flipDirection)
            dB.edtText.requestFocus()
        }
    }


    private fun flipBackToFrontCard(flipDirection: FlipDirection = FlipDirection.FLIP_UP) {
        dB.apply {
            if (flipDirection == FlipDirection.FLIP_UP) {
                back_Up_To_Front_EffectAnmtrSet.start()
                systemOutLogging("Back up to front")
            } else if (flipDirection == FlipDirection.FLIP_DOWN) {
                back_Down_To_Front_EffectAnmtrSet.start()
                systemOutLogging("Back down to front")
            }

            viewgroupBackFlashcard.startAnimation(turnUpFrontCard_After_TurnDownBackCard)
            isShowingFrontCard = true
        }
    }

    private fun flipFrontToBackCard(flipDirection: FlipDirection = FlipDirection.FLIP_UP) {
        dB.apply {
            if (flipDirection == FlipDirection.FLIP_UP) {
                front_Up_To_Back_EffectAnmtrSet.start()
                systemOutLogging("Front up to back")
            } else if (flipDirection == FlipDirection.FLIP_DOWN) {
                systemOutLogging("Front down to back")
                front_Down_To_Back_EffectAnmtrSet.start()
            }
            viewgroupFrontFlashcard.startAnimation(turnUpBackCard_After_TurnDownFrontCard)
            isShowingFrontCard = false
        }
    }

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
                turnOff_AdjustCardPropertyMode()
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun turnOff_AdjustCardPropertyMode() {
        dB.apply {
            is_InAdjustCardProperty_Mode = false

            val front_imgIllustration_IsEmpty =
                imgFrontIllustrationPicture.drawable == front_illustrationDefaultDrawable
            if (front_imgIllustration_IsEmpty and currentCardProperty.frontSideHasText) {
                btnFrontChoosePicture.goVisibleByFadeInAnim(0.35f)
                setFront_IllustrationPictureWidth(0)
            }

            val back_ImgIllustration_IsEmpty =
                imgBackIllustrationPicture.drawable == back_illustrationDefaultDrawable
            if (back_ImgIllustration_IsEmpty and currentCardProperty.backSideHasText) {
                btnBackChoosePicture.goVisibleByFadeInAnim(0.35f)
                setBack_IllustrationPictureWidth(0)
            }


            imgFrontIllustrationPicture.setBackgroundColor(Color.TRANSPARENT)
            layoutChooseCardProperty.root.goGoneByFadeOutAnim()
            layoutFunctionsPanel.root.goVisibleByFadeInAnim(1f)
        }
    }

    private fun getAddFlashcardRequest(): Deck? {
        val bundle = intent.extras

        if (bundle == null) {
            return null
        }

        val flashcardSet = bundle.getSerializable(ADD_REQUEST_FLASHCARD_SET) as Deck?
        return flashcardSet
    }

    private fun getEditFlashcardRequest(): Pair<Flashcard, Deck>? {
        val bundle = intent.extras

        if (bundle == null) {
            return null
        }

        val flashcard = bundle.getSerializable(EDIT_REQUEST_FLASHCARD) as Flashcard?
        val flashcardSet = bundle.getSerializable(EDIT_REQUEST_FLASHCARD_SET) as Deck?

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
            flipBackToFrontCard()
        }
        dB.edtText.requestFocus()
        dB.edtText.hint = "Text can not be empty"
        dB.edtText.setHintTextColor(Color.parseColor("#EC4444"))
    }

    override fun showTranslationInputError() {
        if (isShowingFrontCard) {
            flipFrontToBackCard()
        }
        dB.edtTranslation.requestFocus()
        dB.edtTranslation.hint = "Translation can not be empty"
        dB.edtTranslation.setHintTextColor(Color.parseColor("#EC4444"))
    }

    override fun showFrontEmptyImageError() {
        if (isShowingFrontCard.not()) {
            flipBackToFrontCard()
        }

        quickToast("Front image can not be empty")
    }

    override fun showBackEmptyImageError() {
        if (isShowingFrontCard) {
            flipFrontToBackCard()
        }
        quickToast("Front image can not be empty")
    }

    override fun showInvalidFlashcardSetError(errorMessage: String) {
        dB.txtCreateSetError.text = errorMessage
        dB.vwgrpCreateSetError.goVISIBLE()
        dB.vwgrpCreateSetError.animate().alpha(1f).duration = 200
    }

    fun hideInvalidFlashcardSetError() {
        dB.vwgrpCreateSetError.animate().alpha(0f).setDuration(200).setLiteListener(onEnd = {
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

    private val COMMON_FLIP_CARD_DURATION = 300L
    private val COMMON_FLIP_CARD_INTERPOLATOR = DecelerateInterpolator()

    @Inject
    fun initShow_FRONT_CardAnimators(
        @Named("FlipOpen") turnUpFront: Animation,
        @Named("FlipClose") turnDownBack: Animation
    ) {
        dB.apply {
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
    fun initShow_BACK_CardAnimators(
        @Named("FlipOpen") turnUpBack: Animation,
        @Named("FlipClose") turnDownFront: Animation
    ) {
        dB.apply {

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
        }
    }

    @Inject
    fun initFlipCardEffect(
        @Named("Appear100Percents") front_ShadowAppear: Animator,
        @Named("Disappear100Percents") front_ShadowDisappear: Animator,
        @Named("Appear100Percents") front_LightAppear: Animator,
        @Named("Disappear100Percents") front_LightDisappear: Animator,

        @Named("Appear100Percents") back_ShadowAppear: Animator,
        @Named("Disappear100Percents") back_ShadowDisappear: Animator,
        @Named("Appear100Percents") back_LightAppear: Animator,
        @Named("Disappear100Percents") back_LightDisappear: Animator
    ) {
        dB.apply {

            front_LightAppear.apply {
                duration = COMMON_FLIP_CARD_DURATION
                interpolator = COMMON_FLIP_CARD_INTERPOLATOR
                setTarget(imgFrontLight)
                addListener(onEnd = {
                    imgFrontLight.alpha = 0f
                })
            }

            back_ShadowDisappear.apply {
                duration = COMMON_FLIP_CARD_DURATION
                interpolator = COMMON_FLIP_CARD_INTERPOLATOR
                setTarget(imgBackShadow)
            }

            front_Up_To_Back_EffectAnmtrSet.play(front_LightAppear).before(back_ShadowDisappear)

            front_ShadowAppear.apply {
                duration = COMMON_FLIP_CARD_DURATION
                interpolator = COMMON_FLIP_CARD_INTERPOLATOR
                setTarget(imgFrontShadow)
                addListener(onEnd = {
                    imgFrontShadow.alpha = 0f
                })
            }

            back_LightDisappear.apply {
                duration = COMMON_FLIP_CARD_DURATION
                interpolator = COMMON_FLIP_CARD_INTERPOLATOR
                setTarget(imgBackLight)
            }

            front_Down_To_Back_EffectAnmtrSet.play(front_ShadowAppear).before(back_LightDisappear)

            back_LightAppear.apply {
                duration = COMMON_FLIP_CARD_DURATION
                interpolator = COMMON_FLIP_CARD_INTERPOLATOR
                setTarget(imgBackLight)
                addListener(onEnd = {
                    imgBackLight.alpha = 0f
                })
            }

            front_ShadowDisappear.apply {
                duration = COMMON_FLIP_CARD_DURATION
                interpolator = COMMON_FLIP_CARD_INTERPOLATOR
                setTarget(imgFrontShadow)
            }

            back_Up_To_Front_EffectAnmtrSet.play(back_LightAppear).before(front_ShadowDisappear)


            back_ShadowAppear.apply {
                duration = COMMON_FLIP_CARD_DURATION
                interpolator = COMMON_FLIP_CARD_INTERPOLATOR
                setTarget(imgBackShadow)
                addListener(onEnd = {
                    imgBackShadow.alpha = 0f
                })
            }

            front_LightDisappear.apply {
                duration = COMMON_FLIP_CARD_DURATION
                interpolator = COMMON_FLIP_CARD_INTERPOLATOR
                setTarget(imgFrontLight)
            }
            back_Down_To_Front_EffectAnmtrSet.play(back_ShadowAppear).before(front_LightDisappear)
        }}
}

