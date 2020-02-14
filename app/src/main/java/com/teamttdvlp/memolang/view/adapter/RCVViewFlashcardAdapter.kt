package com.teamttdvlp.memolang.view.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.helper.appear
import com.teamttdvlp.memolang.view.helper.disappear
import com.teamttdvlp.memolang.view.helper.notContains
import java.lang.Exception

class RCVViewFlashcardAdapter (var context : Context, var list : ArrayList<Flashcard>) : RecyclerView.Adapter<RCVViewFlashcardAdapter.ViewHolder> () {

    var isInDeleteMode : Boolean = false

    private var onDeleteButtonClickListener : OnItemClickListener? = null

    private var onItemClickListener : OnItemClickListener? = null

    private var onEndDeleteModeListener : (() -> Unit)? = null

    private var viewHolderList = ArrayList<ViewHolder>()

    private var checkedCardsPosList = ArrayList<Int>()

    private var viewHolderMark = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_flashcard_rcv, parent, false)
        return ViewHolder(view, viewHolderMark++)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        if (checkViewHolderNotInList(viewHolder)) {
            viewHolderList.add(viewHolder)
        }

        viewHolder.apply {
            viewHolderList.add(this)
            val item = list[position]
            txtText.text = item.text
            txtTranslation.text = item.translation

            if (position == 0) {
                onEndDeleteModeListener = {
                    this@RCVViewFlashcardAdapter.onEndDeleteModeListener?.invoke()
                }
            }
            itemView.setOnClickListener {
                onItemClickListener?.onClick(item)
            }

            btnDelete.setOnClickListener {
                selectCard()
                onDeleteButtonClickListener?.onClick(item)
            }

            btnTransparentDelete.setOnClickListener {
                if (!isSelectedDelete) {
                    selectCard()
                } else {
                    unSelectCard()
                }
            }
            updateViewBaseOnStatus(position)
        }
    }

    override fun getItemCount(): Int = list.size

    fun setOnEndDeleteModeListener (onEnd : () -> Unit) {
        this.onEndDeleteModeListener = onEnd
    }

    fun checkViewHolderNotInList(viewHolder: ViewHolder) : Boolean {
        var viewHolderHasBeenAddedToList = false
        for (vh in viewHolderList) {
            if (viewHolder.mark == vh.mark) {
                viewHolderHasBeenAddedToList = true
                break
            }
        }
        return viewHolderHasBeenAddedToList
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
            list.remove(card)
            notifyItemRemoved(pos)
            viewHolderList.removeAt(pos)
        }

        for (pos in 0..viewHolderList.size - 1) {
            viewHolderList[pos].pos = pos
        }

//        turnOffDeleteMode()
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
            vh.resetViewState()
        }
        checkedCardsPosList.clear()
    }

    interface OnItemClickListener {
        fun onClick (item : Flashcard)
    }

    inner class ViewHolder : RecyclerView.ViewHolder {

        var txtText : TextView
        var txtTranslation : TextView
        var btnDelete : ImageView
        var btnTransparentDelete : Button
        var imgDeleteStatus : ImageView
        var viewgroupText : LinearLayout

        val CHECKED_DELETE_IMAGE : Drawable

        val UNCHECKED_DELETE_IMAGE : Drawable

        var pos = -1

        var isSelectedDelete = false

        val `39dp` : Float

        val mark : Int

        var onEndDeleteModeListener : (() -> Unit)? = null

        private var turnOnDeleteModeAnimator : AnimatorSet = AnimatorSet()

        private var turnOffDeleteModeAnimator : AnimatorSet = AnimatorSet()


        constructor(item : View, mark : Int) : super(item){
            this.mark = mark
            txtText = item.findViewById(R.id.txt_text)
            txtTranslation = item.findViewById(R.id.txt_translation)
            btnDelete = item.findViewById(R.id.btn_delete)
            btnTransparentDelete = item.findViewById(R.id.btn_transparent_delete)
            imgDeleteStatus = item.findViewById(R.id.img_delete_status)
            viewgroupText = item.findViewById(R.id.viewgroup_text)

            `39dp` = btnDelete.layoutParams.width * 39f / 20

            CHECKED_DELETE_IMAGE = context.getDrawable(R.drawable.image_checked_delete)!!
            UNCHECKED_DELETE_IMAGE = context.getDrawable(R.drawable.image_unchecked_delete)!!

            initAnimations()
        }

        fun updateViewBaseOnStatus (viewHolderPosition : Int) {
            this.pos = viewHolderPosition
            if (isInDeleteMode) {
                viewgroupText.animate().translationX(`39dp`).setDuration(1)
                imgDeleteStatus.alpha = 1.0f
                btnDelete.alpha = 0.0f
                btnTransparentDelete.appear()

                isSelectedDelete = checkedCardsPosList.contains(this.pos)
                if (isSelectedDelete) {
                    imgDeleteStatus.setImageDrawable(CHECKED_DELETE_IMAGE)
                } else {
                    imgDeleteStatus.setImageDrawable(UNCHECKED_DELETE_IMAGE)
                }
            }
        }

        fun turnOnDeleteMode () {
            turnOnDeleteModeAnimator.start()
        }

        fun turnOffDeleteMode () {
            turnOffDeleteModeAnimator.start()
        }

        fun selectCard () {
            isSelectedDelete = true
            imgDeleteStatus.setImageDrawable(CHECKED_DELETE_IMAGE)
            if (pos == -1) throw Exception ("View Holder position has not been set")
            if (checkedCardsPosList.notContains(pos)) {
                checkedCardsPosList.add(pos)
            }
        }

        fun unSelectCard () {
            isSelectedDelete = false
            imgDeleteStatus.setImageDrawable(UNCHECKED_DELETE_IMAGE)
            if (pos == -1) throw Exception("View Holder position has not been set")
            if (checkedCardsPosList.notContains(pos)) throw Exception("Position vocaList delete dB that it does not contain")
            checkedCardsPosList.remove(pos)
        }

        fun resetViewState () {
            isSelectedDelete = false
            imgDeleteStatus.setImageDrawable(UNCHECKED_DELETE_IMAGE)
        }

        fun initAnimations () {
            val showStatusDelete_Alpha = ObjectAnimator.ofFloat(imgDeleteStatus, View.ALPHA, 0f, 1f)
            showStatusDelete_Alpha .duration = 100

            // btnDelete width is 20dp so 39dp would be its 39/20

            val textMoveLeftToRight = ObjectAnimator.ofFloat(viewgroupText, View.TRANSLATION_X, 0f, `39dp`)
            textMoveLeftToRight.duration = 100

            val hideStatusDelete_Alpha = ObjectAnimator.ofFloat(imgDeleteStatus, View.ALPHA, 1f, 0f)
            hideStatusDelete_Alpha .duration = 100


            val showButtonDelete_Alpha = ObjectAnimator.ofFloat(btnDelete, View.ALPHA, 0f, 1f)
            showStatusDelete_Alpha .duration = 100

            val textMoveRightToLeft = ObjectAnimator.ofFloat(viewgroupText, View.TRANSLATION_X, `39dp`, 0f)
            textMoveLeftToRight.duration = 100

            val hideButtonDelete_Alpha = ObjectAnimator.ofFloat(btnDelete, View.ALPHA, 1f, 0f)
            hideStatusDelete_Alpha .duration = 100

            turnOnDeleteModeAnimator.playSequentially(hideButtonDelete_Alpha, textMoveLeftToRight, showStatusDelete_Alpha)
            turnOnDeleteModeAnimator.addListener (onEnd =  {
                btnTransparentDelete.appear()
            })

            turnOffDeleteModeAnimator.playSequentially(hideStatusDelete_Alpha, textMoveRightToLeft, showButtonDelete_Alpha)
            turnOffDeleteModeAnimator.addListener (onEnd =  {
                btnTransparentDelete.disappear()
                onEndDeleteModeListener?.invoke()
            })
        }
    }
}
