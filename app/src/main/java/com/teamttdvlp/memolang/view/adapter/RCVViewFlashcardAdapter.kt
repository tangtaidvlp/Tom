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
import com.teamttdvlp.memolang.databinding.ItemFlashcardRcvBinding
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.helper.*

class RCVViewFlashcardAdapter (var context : Context, var list : ArrayList<Flashcard>) : RecyclerView.Adapter<RCVViewFlashcardAdapter.ViewHolder> () {

    var isInDeleteMode : Boolean = false

    private var onDeleteButtonClickListener : OnItemClickListener? = null

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

        if (position == list.size - 1) {
            vHder.dB.line.goINVISIBLE()
            vHder.dB.root.elevation = 3.dp().toFloat()
            (vHder.dB.root.layoutParams as RecyclerView.LayoutParams).bottomMargin = 3.dp()
            vHder.dB.root.requestLayout()
        }

        val flashcard = list[position]
        vHder.bind(flashcard)

        val isItemChecked = checkedCardsPosList.contains(position)

        if (isInDeleteMode) {
            vHder.showDeleteModeState(isItemChecked)
        } else {
            vHder.showNormalState()
        }

        if (viewHolderList.notContains(vHder)) {
            viewHolderList.add(vHder)
        }

        viewHolderList.add(vHder)

        vHder.itemView.setOnClickListener {
            onItemClickListener?.onClick(flashcard)
        }

        vHder.dB.btnDelete.setOnClickListener {
            if (not(isInDeleteMode)) {
                turnOnDeleteMode()
            }
            onDeleteButtonClickListener?.onClick(flashcard)
        }

        vHder.dB.btnSwitchDeleteState.setOnClickListener {
            quickLog("Pos: " +vHder.adapterPosition)
            if (checkedCardsPosList.contains(vHder.adapterPosition)) {
                quickLog("Contains: " +vHder.adapterPosition)
                vHder.uncheck()
                checkedCardsPosList.remove(vHder.adapterPosition)
            } else {
                vHder.check()
                checkedCardsPosList.add(vHder.adapterPosition)
                quickLog("Not contains: " +vHder.adapterPosition)
            }
        }

        if (position == 0) {
            vHder.itemOnEndDeleteModeListener = {
                this@RCVViewFlashcardAdapter.onEndDeleteModeListener?.invoke()
            }
        }
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
            quickLog("POS: " + pos)
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

    fun setOnDeleteButtonClickListener (onDeleteButtonClickListener: (item: Flashcard) -> Unit) {
        this.onDeleteButtonClickListener = object : OnItemClickListener {
            override fun onClick(item: Flashcard) {
                onDeleteButtonClickListener(item)
            }
        }
    }

    fun setOnItemClickListener (onItemClickListener: (item: Flashcard) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onClick(item: Flashcard) {
                onItemClickListener(item)
            }
        }
    }

    fun turnOnDeleteMode () {
        isInDeleteMode = true
        for (vh in viewHolderList) {
            vh.turnOnDeleteMode()
        }
    }

    fun turnOffDeleteMode () {
        isInDeleteMode = false
        for (vh in viewHolderList) {
            vh.turnOffDeleteMode()
            vh.uncheck()
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

        val CHECKED_DELETE_IMAGE : Drawable

        val UNCHECKED_DELETE_IMAGE : Drawable

        val `28dp` : Float

        init {
            if (itemViewHeight == -1) dB.root.doOnPreDraw {
                itemViewHeight = dB.root.height
                quickLog("ItemViewHeight: $itemViewHeight")
            }

            `28dp` = 28.dp().toFloat()
            CHECKED_DELETE_IMAGE = context.getDrawable(R.drawable.image_checked_delete)!!
            UNCHECKED_DELETE_IMAGE = context.getDrawable(R.drawable.image_unchecked_delete)!!
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

        fun showNormalState () {
            val isStill_InDeleteMode = dB.btnSwitchDeleteState.isVisible
            if (isStill_InDeleteMode) {
                dB.viewgroupText.alpha = 1f
                dB.btnDelete.alpha = 1f

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
                btnDelete.alpha = 0.0f
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

        fun uncheck () {
            dB.imgDeleteStatus.setImageDrawable(UNCHECKED_DELETE_IMAGE)
        }

        fun check () {
            dB.imgDeleteStatus.setImageDrawable(CHECKED_DELETE_IMAGE)
        }

        fun initAnimations () {
            initTurnOnDeleteModeAnimations()
            initTurnOffDeleteModeAnimations()
        }

        fun initTurnOnDeleteModeAnimations () {
            val showStatusDelete_Alpha = ObjectAnimator.ofFloat(dB.imgDeleteStatus, View.ALPHA, 0f, 1f)
            showStatusDelete_Alpha.duration = 100

            val textMoveLeftToRight = ObjectAnimator.ofFloat(dB.viewgroupText, View.TRANSLATION_X, 0f, `28dp`)
            textMoveLeftToRight.duration = 100

            val hideButtonDelete_Alpha = ObjectAnimator.ofFloat(dB.btnDelete, View.ALPHA, 1f, 0f)
            hideButtonDelete_Alpha.duration = 100

            val hideTranslationAndText = ObjectAnimator.ofFloat(dB.viewgroupText, View.ALPHA, 1f, 0f)

            val showTextForDeleting = ObjectAnimator.ofFloat(dB.txtTextForDelete, View.ALPHA, 0f, 1f)

            val showTextForDeleteSet = AnimatorSet().apply {
                play(hideTranslationAndText).with(showTextForDeleting)
                duration = 100
            }

            turnOnDeleteModeAnimator.playSequentially(hideButtonDelete_Alpha, textMoveLeftToRight, showTextForDeleteSet, showStatusDelete_Alpha)
            turnOnDeleteModeAnimator.addListener (
                onStart = {
                    this@ViewHolder.itemView.isClickable = false
                },
                onEnd =  {
                    dB.btnSwitchDeleteState.goVISIBLE()
                })
        }

        fun initTurnOffDeleteModeAnimations () {
            val hideStatusDelete_Alpha = ObjectAnimator.ofFloat(dB.imgDeleteStatus, View.ALPHA, 1f, 0f)
            hideStatusDelete_Alpha.duration = 100

            val showButtonDelete_Alpha = ObjectAnimator.ofFloat(dB.btnDelete, View.ALPHA, 0f, 1f)
            showButtonDelete_Alpha.duration = 100

            val textMoveRightToLeft = ObjectAnimator.ofFloat(dB.viewgroupText, View.TRANSLATION_X, `28dp`, 0f)
            textMoveRightToLeft.duration = 100

            val showTranslationAndText = ObjectAnimator.ofFloat(dB.viewgroupText, View.ALPHA, 0f, 1f)

            val hideTextForDeleting = ObjectAnimator.ofFloat(dB.txtTextForDelete, View.ALPHA, 1f, 0f)

            val hideTextForDeleteSet = AnimatorSet().apply {
                play(showTranslationAndText).with(hideTextForDeleting)
                duration = 100
            }

            turnOffDeleteModeAnimator.playSequentially(hideTextForDeleteSet, hideStatusDelete_Alpha, textMoveRightToLeft, showButtonDelete_Alpha)
            turnOffDeleteModeAnimator.startDelay = 300
            turnOffDeleteModeAnimator.addListener (
                onEnd =  {
                    this@ViewHolder.itemView.isClickable = true
                    dB.btnSwitchDeleteState.goGONE()
                    itemOnEndDeleteModeListener?.invoke()
                })
        }

        override fun equals(other: Any?): Boolean {
            if (other is ViewHolder) {
                return this.id == other.id
            }
            return super.equals(other)
        }


    }
}
