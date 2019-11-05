package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import androidx.core.animation.addListener
import androidx.core.view.isGone
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityEditFlashcardBinding
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.model.sqlite.repository.FlashcardRepository
import com.teamttdvlp.memolang.viewmodel.edit_flashcard.EditFlashcardActivityViewModel
import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
import javax.inject.Inject
import javax.inject.Named

const val UPDATED_FLASHCARD = "updated_flashcard"

class EditFlashcardActivity : BaseActivity<ActivityEditFlashcardBinding, EditFlashcardActivityViewModel>() {

    // After this time, saved flashcard will move right and disappear
    private val SAVED_FLASHCARD_APPEAR_INTERVAL = 400L

    private val animatorSetSaving = AnimatorSet()

    private val animatorSetCancelSavingAppear = AnimatorSet()

    private val animatorSetCancelSavingDisappear = AnimatorSet()



    // These variables are used for checking if the user change card's informations
    // OG: Original
    private lateinit var OG_SourceLang: String
    private lateinit var OG_TargetLang: String
    private lateinit var OG_Text: String
    private lateinit var OG_Translation: String
    private lateinit var OG_Using: String

    private lateinit var edittingCard: Flashcard

    lateinit var flashcardRepository: FlashcardRepository
    @Inject set

    lateinit var onlineFlashcardManager: OnlineFlashcardDBManager
    @Inject set

    override fun getLayoutId(): Int = R.layout.activity_edit_flashcard

    override fun takeViewModel(): EditFlashcardActivityViewModel = getActivityViewModel() {
        EditFlashcardActivityViewModel(onlineFlashcardManager, flashcardRepository)
    }

    override fun initProperties() {
        dataBinding.apply {
            edittingCard = intent.getSerializableExtra(FLASHCARD_KEY) as Flashcard
            edittingCard.apply {
                val sourceLang = type.substring(0, type.indexOf("-", 0, true))
                val targetLang = type.removePrefix("$sourceLang-")
                txtSourceLang.text = sourceLang
                txtTargetLang.text = targetLang
                edtText.setText(toBeTranslatedWord)
                edtTranslation.setText(translatedWord)
                edtUsing.setText(using)
                OG_SourceLang = sourceLang
                OG_TargetLang = targetLang
                OG_Text = toBeTranslatedWord
                OG_Translation = translatedWord
                OG_Using = using
            }
        }
    }

    override fun addViewControls() {
        dataBinding.apply {

        }
    }

    override fun addViewEvents() {
        dataBinding.apply {

            btnSave.setOnClickListener {
                if (doesUserChangeInfo()) {
                    val text = edtText.text.toString()
                    val translation = edtTranslation.text.toString()
                    val using = edtUsing.text.toString()
                    val type = txtSourceLang.text.toString() + "-" + txtTargetLang.text.toString()
                    val newCard = Flashcard(edittingCard.id, text, translation, type, using)
                    viewModel.updateOfflineFlashcard(newCard) { isSuccess, insertedCardId, ex ->
                        if (isSuccess) {
                            newCard.id = insertedCardId.toInt()
                            viewModel.updateOnlineFlashcard(
                                edittingCard.type,
                                edittingCard,
                                newCard
                            ) { isSuccessful, exception ->
                                if (isSuccessful) {
                                    quickLog("Update Online Success")
                                } else {
                                    quickLog("Update Online Failed")
                                    exception?.printStackTrace()
                                }
                            }
                            returnUpdatedFlashcardResult(newCard)
                            animatorSetSaving.start()
                            hideVirtualKeyboard()
                            currentFocus?.clearFocus()
                        } else {
                            quickToast("Update flashcard in offline database failed. Please try again")
                        }
                    }


                }
            }

            btnCancel.setOnClickListener {
                if (doesUserChangeInfo()) {
                    animatorSetCancelSavingAppear.start()
                    hideVirtualKeyboard()
                } else finish()
            }

            imgBlackBackgroundCancelSavingWidgets.setOnClickListener {
                animatorSetCancelSavingDisappear.start()
            }

            btnCancelIt.setOnClickListener {
                animatorSetCancelSavingDisappear.addListener(onEnd = {
                    finish()
                })
                animatorSetCancelSavingDisappear.start()
            }

        }
    }

    override fun onBackPressed() {
        dataBinding.apply {
            if (viewgroupCancelSaving.isGone) {
                btnCancel.performClick()
            }
        }
    }


    fun returnUpdatedFlashcardResult (updatedFlashcard : Flashcard) {
        val intent = Intent()
        intent.putExtra(UPDATED_FLASHCARD, updatedFlashcard)
        setResult(Activity.RESULT_OK, intent)
    }


    private fun doesUserChangeInfo(): Boolean {
        dataBinding.apply {
            val newSourceLang = txtSourceLang.text.toString()
            val newTargetLang = txtTargetLang.text.toString()
            val newText = edtText.text.toString()
            val newTranslation = edtTranslation.text.toString()
            val newUsing = edtUsing.text.toString()

            return (OG_SourceLang != newSourceLang) || (OG_TargetLang != newTargetLang) || (OG_Text != newText) || (OG_Translation != newTranslation) || (OG_Using != newUsing)
        }
    }


    /**
     * Dependencies Injected Block0
     */

    @Inject
    fun initSaveAnimation(
        @Named("ZoomToNothingAndRotate") groupDisappear: Animator,
        @Named("ZoomFromNothingToOversizeThenNormalSize") savedImageAppear: Animator,
        @Named("MoveRightAndFadeOut") savedImageDisappear: Animator
    ) {
        dataBinding.apply {

            groupDisappear.apply {
                setTarget(viewgroupAddFlashcard)
                addListener(onEnd = { viewgroupAddFlashcard.disappear() })
            }

            savedImageAppear.setTarget(imgSavedFlashcard)
            savedImageDisappear.setTarget(imgSavedFlashcard)

            val appearSet = AnimatorSet().apply { play(groupDisappear).before(savedImageAppear) }
            val disappearSet = AnimatorSet().apply { play(savedImageDisappear).after(SAVED_FLASHCARD_APPEAR_INTERVAL) }
            disappearSet.addListener(onEnd = { finish() })
            animatorSetSaving.play(appearSet).before(disappearSet)
        }
    }

    @Inject
    fun initCancelSavingAnimation(
        @Named("FromNormalSizeToNothing") viewgroupCancelSavingDisappear: Animator,
        @Named("Disappear50percents") blackBackgroundDisappear: Animator,
        @Named("FromNothingToNormalSize") viewgroupCancelSavingAppear: Animator,
        @Named("Appear50percents") blackBackgroundAppear: Animator
    ) {
        dataBinding.apply {

            viewgroupCancelSavingDisappear.setTarget(viewgroupCancelSaving)
            blackBackgroundDisappear.setTarget(imgBlackBackgroundCancelSavingWidgets)
            animatorSetCancelSavingDisappear.play(viewgroupCancelSavingDisappear).before(blackBackgroundDisappear)
            animatorSetCancelSavingDisappear.addListener(onEnd = { groupCancelSavingWidgets.disappear() })

            viewgroupCancelSavingAppear.setTarget(viewgroupCancelSaving)
            blackBackgroundAppear.setTarget(imgBlackBackgroundCancelSavingWidgets)
            animatorSetCancelSavingAppear.play(blackBackgroundAppear).before(viewgroupCancelSavingAppear)
            animatorSetCancelSavingAppear.addListener(onStart = { groupCancelSavingWidgets.appear() })
        }
    }

}
