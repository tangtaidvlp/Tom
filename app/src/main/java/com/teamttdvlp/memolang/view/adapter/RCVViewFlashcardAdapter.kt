package com.teamttdvlp.memolang.view.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.CardQuizInfor
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.databinding.ItemFlashcardRcvBinding
import com.teamttdvlp.memolang.view.customview.interpolator.NormalOutExtraSlowIn
import com.teamttdvlp.memolang.view.helper.*

class RCVViewFlashcardAdapter (var context : Context, var list : ArrayList<Flashcard>, var flashcardQuizList : ArrayList<CardQuizInfor>) : RecyclerView.Adapter<RCVViewFlashcardAdapter.ViewHolder> () {

    var isInEditAnswer_Quiz_Mode: Boolean = false
    private set

    var isIn_Delete_Mode : Boolean = false
    private set

    var isIn_MoveCard_Mode : Boolean = false
        private set

    private var onItemClickListener : OnItemClickListener? = null

    private var onEndDeleteModeListener : (() -> Unit)? = null

    private var viewHolderList = ArrayList<ViewHolder>()

    private var checkedCardsPosList = ArrayList<Int>()

    private var viewHolderMark = 0

    private val EXTRA_COUNT = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dB = ItemFlashcardRcvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(dB, viewHolderMark++, context)
    }

    override fun onBindViewHolder(vHder: ViewHolder, position: Int) {
        val goToEmptySpace = position > list.size - 1
        if (goToEmptySpace) {
            vHder.dB.root.goINVISIBLE()
            return
        } else {
            // Because viewholder is reused
            // So, when RecyclerView reuses Viewholder which was made INVISIBLE before
            // on Normal Item. That's a big bug
            vHder.dB.root.goVISIBLE()
        }
//
//        if (position == list.size - 1) {
////            vHder.dB.line.goINVISIBLE()
////            vHder.dB.root.elevation = 3.dp().toFloat()
//////            (vHder.dB.root.layoutParams as RecyclerView.LayoutParams).bottomMargin = 3.dp()
////            vHder.dB.root.requestLayout()
//        }
////        else {
////            // Reset it
////            vHder.dB.line.goVISIBLE()
////            vHder.dB.root.elevation = 0.dp().toFloat()
////            (vHder.dB.root.layoutParams as RecyclerView.LayoutParams).bottomMargin = 0.dp()
////            vHder.dB.root.requestLayout()
////        }

        val flashcard = list[position]
        vHder.bind(flashcard)

        vHder.itemView.setOnClickListener {
            onItemClickListener?.onClick(flashcard)
        }

        if (isIn_Delete_Mode) {
            val isItemChecked = checkedCardsPosList.contains(position)
            vHder.showDeleteModeState(isItemChecked)

        } else if (isInEditAnswer_Quiz_Mode) {
            val is_QuizInfor_EditedByUser = checkQuizInforStatus (flashcard.id)
            if (is_QuizInfor_EditedByUser) {
                vHder.showEditQuizModeState (is_QuizInfor_EditedByUser)
            }
        } else {
            vHder.showNormalState()
        }

        if (viewHolderList.notContains(vHder)) {
            viewHolderList.add(vHder)
        }

//        viewHolderList.add(vHder)

        /**
         * Delete mode set up
         */
        vHder.dB.btnSwitchDeleteState.setOnClickListener {
            if (checkedCardsPosList.contains(vHder.adapterPosition)) {
                vHder.uncheckDeleted()
                checkedCardsPosList.remove(vHder.adapterPosition)
            } else {
                vHder.checkDeleted()
                checkedCardsPosList.add(vHder.adapterPosition)
            }
        }

        val is_OnEndDeleteMode_NOT_SetUp = (position == 0)
        if (is_OnEndDeleteMode_NOT_SetUp) {
            vHder.itemOnEndDeleteModeListener = {
                this@RCVViewFlashcardAdapter.onEndDeleteModeListener?.invoke()
            }
        }

        /**
         * Move card mode set up
         */

        vHder.dB.btnSwitchMoveState.setOnClickListener {
            if (checkedCardsPosList.contains(vHder.adapterPosition)) {
                vHder.uncheckSelectedMove()
                checkedCardsPosList.remove(vHder.adapterPosition)
            } else {
                vHder.checkSelectedMove()
                checkedCardsPosList.add(vHder.adapterPosition)
            }
        }

    }

    private fun checkQuizInforStatus(flashcardID: Int): Boolean {
        for (cardQuizInfor in flashcardQuizList) {
            if (cardQuizInfor.cardId == flashcardID) {
                flashcardQuizList.remove(cardQuizInfor)
                return true
            }
        }

        return false
    }

    override fun getItemCount(): Int = list.size + EXTRA_COUNT


    fun setOnEndDeleteModeListener (onEnd : () -> Unit) {
        this.onEndDeleteModeListener = {
            notifyDataSetChanged()
            onEnd()
        }
    }

    fun getSelectedFlashcardList () : ArrayList<Flashcard> {
        val result = ArrayList<Flashcard>()
        for (pos in checkedCardsPosList) {
            result.add(list[pos])
        }
        return result
    }

    fun deleteSelectedFlashcards () {
        val needDeletingCards = getSelectedFlashcardList()

        for (card in needDeletingCards) {
            val pos = list.indexOf(card)
            list.removeAt(pos)
            notifyItemRemoved(pos)
        }

        checkedCardsPosList.clear()
    }


    fun setOnItemClickListener (onItemClickListener: (item: Flashcard) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onClick(item: Flashcard) {
                onItemClickListener(item)
            }
        }
    }

    fun turnOnEditAnswerQuizMode () {
        isInEditAnswer_Quiz_Mode = true
        for (vh in viewHolderList) {
            vh.turnOnEditAnswerQuizMode()
        }
    }

    fun turnOffEditAnswerQuizMode () {
        isInEditAnswer_Quiz_Mode = false
        for (vh in viewHolderList) {
            vh.turnOffEditAnswerQuizMode()
        }
    }

    fun turnOnDeleteMode () {
        isIn_Delete_Mode = true
        for (vh in viewHolderList) {
            vh.turnOnDeleteMode()
        }
    }

    fun turnOffDeleteMode () {
        isIn_Delete_Mode = false
        for (vh in viewHolderList) {
            vh.turnOffDeleteMode()
            vh.uncheckDeleted()
        }
        checkedCardsPosList.clear()
    }

    fun turnOnMoveCardsMode() {
        isIn_MoveCard_Mode = true
        for (vh in viewHolderList) {
            vh.turnOnMoveCardMode()
        }
    }

    fun turnOffMoveCardMode () {
        isIn_MoveCard_Mode = false
        for (vh in viewHolderList) {
            vh.turnOffMoveCardMode()
            vh.uncheckSelectedMove()
        }
        checkedCardsPosList.clear()
    }

    interface OnItemClickListener {
        fun onClick (item : Flashcard)
    }

    class ViewHolder (var dB : ItemFlashcardRcvBinding, var id : Int, context: Context): RecyclerView.ViewHolder (dB.root){

        companion object {
            var itemViewHeight = -1
        }

        var itemOnEndDeleteModeListener : (() -> Unit)? = null

        private var turnOnDeleteModeAnimator : AnimatorSet = AnimatorSet()

        private var turnOffDeleteModeAnimator : AnimatorSet = AnimatorSet()

        private var turnOnMoveCardModeAnimator : AnimatorSet = AnimatorSet()

        private var turnOffMoveCardModeAnimator : AnimatorSet = AnimatorSet()

        private val CHECKED_MOVED_IMAGE : Drawable

        private val CHECKED_DELETE_IMAGE : Drawable

        private val UNCHECKED_DELETE_IMAGE : Drawable

        private val CREATED_QUIZ_INFOR_IMAGE : Drawable

        private val DEFAULT_QUIZ_INFOR_IMAGE : Drawable

        val `28dp` : Float

        init {
            if (itemViewHeight == -1) dB.root.doOnPreDraw {
                itemViewHeight = dB.root.height
                systemOutLogging("ItemViewHeight: $itemViewHeight")
            }

            `28dp` = 28.dp().toFloat()
            CHECKED_DELETE_IMAGE = context.getDrawable(R.drawable.image_checked_delete)!!
            UNCHECKED_DELETE_IMAGE = context.getDrawable(R.drawable.image_unchecked_delete)!!

            CHECKED_MOVED_IMAGE = context.getDrawable(R.drawable.image_checked_moved)!!

            CREATED_QUIZ_INFOR_IMAGE = context.getDrawable(R.drawable.image_icon_answers_created)!!
            DEFAULT_QUIZ_INFOR_IMAGE = context.getDrawable(R.drawable.image_icon_answers_default)!!

            initAnimations()
        }

        fun bind (flashcard : Flashcard) {
            dB.flashcard = flashcard
        }

        fun turnOnDeleteMode () {
            turnOnDeleteModeAnimator.start()
        }

        fun turnOffDeleteMode () {
            turnOffDeleteModeAnimator.start()
        }

        fun turnOnMoveCardMode () {
            turnOnMoveCardModeAnimator.start()
        }

        fun turnOffMoveCardMode () {
            turnOffMoveCardModeAnimator.start()
        }


        fun showNormalState () {
            val isStill_InDeleteMode = dB.btnSwitchDeleteState.isVisible
            if (isStill_InDeleteMode) {
                dB.viewgroupText.alpha = 1f

                dB.viewgroupText.translationX = 0f
                dB.txtTextForDelete.alpha = 0f
                dB.imgDeleteStatus.alpha = 0f

                dB.btnSwitchDeleteState.goGONE()
            }
        }

        fun showDeleteModeState (isChecked : Boolean) { dB. apply {
            val isNot_InDeleteMode = not(btnSwitchDeleteState.isVisible)

            if (isNot_InDeleteMode) {
                viewgroupText.translationX = `28dp`
                imgDeleteStatus.alpha = 1.0f
                btnSwitchDeleteState.goVISIBLE()
                viewgroupText.alpha = 0f
                txtTextForDelete.alpha = 1f
            }

            if (isChecked) {
                imgDeleteStatus.setImageDrawable(CHECKED_DELETE_IMAGE)
            } else {
                imgDeleteStatus.setImageDrawable(UNCHECKED_DELETE_IMAGE)
            }
        } }

        fun uncheckDeleted () {
            dB.imgDeleteStatus.setImageDrawable(UNCHECKED_DELETE_IMAGE)
        }

        fun checkDeleted () {
            dB.imgDeleteStatus.setImageDrawable(CHECKED_DELETE_IMAGE)
        }

        fun checkSelectedMove () {
            dB.imgDeleteStatus.setImageDrawable(CHECKED_MOVED_IMAGE)
        }

        fun uncheckSelectedMove () {
            dB.imgDeleteStatus.setImageDrawable(UNCHECKED_DELETE_IMAGE)
        }

        private fun initAnimations () {
            init_TurnOn_Mode_Animations()
            init_TurnOff_Mode_Animations()
        }

        private fun init_TurnOn_Mode_Animations () {
            val showStatusDelete_Alpha = ObjectAnimator.ofFloat(dB.imgDeleteStatus, View.ALPHA, 0f, 1f)
            showStatusDelete_Alpha.duration = 100

            val textMoveLeftToRight = ObjectAnimator.ofFloat(dB.viewgroupText, View.TRANSLATION_X, 0f, `28dp`)
            textMoveLeftToRight.duration = 100

            val hideTranslationAndText = ObjectAnimator.ofFloat(dB.viewgroupText, View.ALPHA, 1f, 0f)

            val showTextForDeleting = ObjectAnimator.ofFloat(dB.txtTextForDelete, View.ALPHA, 0f, 1f)

            val showTextForDeleteSet = AnimatorSet().apply {
                play(hideTranslationAndText).with(showTextForDeleting)
                duration = 100
            }

            // Delete mode
            turnOnDeleteModeAnimator.playSequentially(textMoveLeftToRight, showTextForDeleteSet, showStatusDelete_Alpha)
            turnOnDeleteModeAnimator.addListener (
                onStart = {
                    this@ViewHolder.itemView.isClickable = false
                },
                onEnd =  {
                    dB.btnSwitchDeleteState.goVISIBLE()
                })


            // Move card mode
            turnOnMoveCardModeAnimator.playSequentially(textMoveLeftToRight, showTextForDeleteSet, showStatusDelete_Alpha)
            turnOnMoveCardModeAnimator.addListener (
                onStart = {
                    this@ViewHolder.itemView.isClickable = false
                },
                onEnd =  {
                    dB.btnSwitchMoveState.goVISIBLE()
                })
        }

        private fun init_TurnOff_Mode_Animations () {
            val hideStatusDelete_Alpha = ObjectAnimator.ofFloat(dB.imgDeleteStatus, View.ALPHA, 1f, 0f)
            hideStatusDelete_Alpha.duration = 100

            val textMoveRightToLeft = ObjectAnimator.ofFloat(dB.viewgroupText, View.TRANSLATION_X, `28dp`, 0f)
            textMoveRightToLeft.duration = 100

            val showTranslationAndText = ObjectAnimator.ofFloat(dB.viewgroupText, View.ALPHA, 0f, 1f)

            val hideTextForDeleting = ObjectAnimator.ofFloat(dB.txtTextForDelete, View.ALPHA, 1f, 0f)

            val hideTextForDeleteSet = AnimatorSet().apply {
                play(showTranslationAndText).with(hideTextForDeleting)
                duration = 100
            }

            // Delete
            turnOffDeleteModeAnimator.playSequentially(hideStatusDelete_Alpha, hideTextForDeleteSet, textMoveRightToLeft)
            turnOffDeleteModeAnimator.startDelay = 300
            turnOffDeleteModeAnimator.addListener (
                onEnd =  {
                    this@ViewHolder.itemView.isClickable = true
                    dB.btnSwitchDeleteState.goGONE()
                    itemOnEndDeleteModeListener?.invoke()
                })


            // Move card mode
            turnOffMoveCardModeAnimator.playSequentially(hideStatusDelete_Alpha, hideTextForDeleteSet, textMoveRightToLeft)
            turnOffMoveCardModeAnimator.addListener (
                onEnd =  {
                    this@ViewHolder.itemView.isClickable = true
                    dB.btnSwitchMoveState.goGONE()
                    // TODO (on End move mode)
                })
        }

        override fun equals(other: Any?): Boolean {
            if (other is ViewHolder) {
                return this.id == other.id
            }
            return super.equals(other)
        }

        fun turnOnEditAnswerQuizMode() { dB.apply {
            vwgrpEditAnswersQuiz.goVISIBLE()
            vwgrpEditAnswersQuiz.scaleX = 0f
            vwgrpEditAnswersQuiz.scaleY = 0f
            vwgrpEditAnswersQuiz.animate().scaleY(1f).scaleX(1f).setDuration(100).setInterpolator(NormalOutExtraSlowIn()).clearListeners()
        }}


        fun turnOffEditAnswerQuizMode() { dB.apply {
            vwgrpEditAnswersQuiz.animate().scaleY(0f).scaleX(0f)
                .setDuration(100).setInterpolator(NormalOutExtraSlowIn()).setLiteListener(onEnd = {
                    vwgrpEditAnswersQuiz.goGONE()
                })
        }}

        fun showEditQuizModeState(is_QuizInfor_Edited_ByUser: Boolean) { dB.apply {
            if (is_QuizInfor_Edited_ByUser == true)  {
                btnEditAnswersQuiz.setImageDrawable(CREATED_QUIZ_INFOR_IMAGE)
            } else {
                btnEditAnswersQuiz.setImageDrawable(DEFAULT_QUIZ_INFOR_IMAGE)
            }
        }}

    }
}
