package com.teamttdvlp.memolang.view.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ItemLanguageRcvBinding
import com.teamttdvlp.memolang.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.view.helper.*

class RCV_LanguageAdapter (var context : Context, var list : ArrayList<FlashcardSet> = ArrayList()) : RecyclerView.Adapter<RCV_LanguageAdapter.ViewHolder> () {

    private var onBtnViewListListener : OnItemClickListener? = null

    private var onBtnAddListener : OnItemClickListener? = null

    private var onItemClickListener : OnItemClickListener? = null

    private var onBtnUseFlashcardClickListener : OnItemClickListener? = null

    private var onBtnReviewFlashcardEasyClickListener : OnItemClickListener? = null

    private var onBtnReviewFlashcardHardClickListener : OnItemClickListener? = null

    private var expandedHolder : ViewHolder? = null

    private var expandedPosition : Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemLanguageRcvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        if (expandedHolder == holder) {
            if (position == expandedPosition) {
                holder.expand(false)
            } else {
                holder.collapse(false)
            }
        }

        holder.dB.txtLanguage.text = item.name
        holder.dB.btnViewList.setOnClickListener {
            onBtnViewListListener?.onClick(item)
        }

        holder.dB.btnAddFlashcard.setOnClickListener {
            onBtnAddListener?.onClick(item)
        }

        holder.dB.vwgrpReviewFlashcardHard.setOnClickListener {
            onBtnReviewFlashcardHardClickListener?.onClick(item)
        }
        holder.dB.vwgrpReviewFlashcardEasy.setOnClickListener {
            onBtnReviewFlashcardEasyClickListener?.onClick(item)
        }
        holder.dB.vwgrpUseFlashcard.setOnClickListener {
            onBtnUseFlashcardClickListener?.onClick(item)
        }
        holder.itemView.setOnClickListener {
            if (holder.isExpanded()) {
                holder.collapse(true)
                expandedHolder = null
                expandedPosition = -1
            } else {
                if (expandedHolder != null ) {
                    if (expandedHolder!!.isExpanded()) {
                        expandedHolder!!.collapse(true)
                    }
                }
                holder.expand(true)
                expandedHolder = holder
                expandedPosition = position
            }
            onItemClickListener?.onClick(item)
        }

    }

    fun setOnBtnViewListClickListener (onBtnViewListListener: (item: FlashcardSet) -> Unit) {
        this.onBtnViewListListener = object : OnItemClickListener {
            override fun onClick(item: FlashcardSet) {
                onBtnViewListListener(item)
            }
        }
    }

    fun setOnBtnAddClickListener (onBtnAddListener: (item: FlashcardSet) -> Unit) {
        this.onBtnAddListener = object : OnItemClickListener {
            override fun onClick(item: FlashcardSet) {
                onBtnAddListener(item)
            }
        }
    }

    fun setOnItemClickListener (onClick : (FlashcardSet) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onClick(item: FlashcardSet) {
                onClick(item)
            }
        }
    }


    fun setOnBtnUseFlashcardClickListener (onClick: (FlashcardSet) -> Unit) {
        onBtnUseFlashcardClickListener = object : OnItemClickListener {
            override fun onClick(item: FlashcardSet) {
                onClick.invoke(item)
            }
        }
    }


    fun setOnBtnReviewFlashcardEasyClickListener (onClick: (FlashcardSet) -> Unit) {
        onBtnReviewFlashcardEasyClickListener = object : OnItemClickListener {
            override fun onClick(item: FlashcardSet) {
                onClick.invoke(item)
            }
        }
    }


    fun setOnBtnReviewFlashcardHardClickListener (onClick: (FlashcardSet) -> Unit) {
        onBtnReviewFlashcardHardClickListener = object : OnItemClickListener {
            override fun onClick(item: FlashcardSet) {
                onClick.invoke(item)
            }
        }
    }

    fun setData (data : ArrayList<FlashcardSet>) {
        list = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick (item : FlashcardSet)
    }

    class ViewHolder (var dB : ItemLanguageRcvBinding) : RecyclerView.ViewHolder(dB.root) {

        private var view = dB.root

        companion object {

            private var expandedHeight = -1

            private var originalHeight  = -1

            private var offsetHeight = -1

        }

        private var hideVwgrpUsingOptionsAnimtr : Animation = AnimationUtils.loadAnimation(dB.root.context, R.anim.disappear)

        private var showVwgrpUsingOptionsAnimtr : Animation = AnimationUtils.loadAnimation(dB.root.context, R.anim.appear)

        init {
            val areNotSetUp =  (expandedHeight < 0) || (originalHeight < 0) || (offsetHeight < 0)

            if (areNotSetUp) setUpHeights()
            setUpAnimationListener()
        }

        private fun setUpHeights () {
            view.doOnLayout { view ->
                originalHeight = view.height
                dB.vwgrpUsingOptions.appear()
                view.doOnNextLayout {
                    expandedHeight = view.height
                    offsetHeight = expandedHeight - originalHeight
                    dB.vwgrpUsingOptions.post {
                        quickLog("POST DISAPPEAR")
                        dB.vwgrpUsingOptions.disappear()
                    }
                }
            }
        }

        private fun setUpAnimationListener () {
            showVwgrpUsingOptionsAnimtr.addAnimationLister(onStart = {
                dB.vwgrpUsingOptions.appear()
            })

            hideVwgrpUsingOptionsAnimtr.addAnimationLister (onEnd = {
                dB.vwgrpUsingOptions.clearAnimation()
                dB.vwgrpUsingOptions.doOnNextLayout {
                    dB.vwgrpUsingOptions.disappear()
                }
            })
        }

        fun expand (animate : Boolean) {
            if (animate) {
                startValueAnimator() { progress ->
                    view.layoutParams.height = originalHeight + (offsetHeight * progress).toInt()
                    view.requestLayout()
                }
                dB.vwgrpUsingOptions.startAnimation(showVwgrpUsingOptionsAnimtr)
            } else {
                view.layoutParams.height = expandedHeight
                view.requestLayout()
                dB.vwgrpUsingOptions.appear()
            }
        }

        fun collapse (animate : Boolean) {
            if (animate) {
                startValueAnimator() { progress ->
                    view.layoutParams.height = expandedHeight - (offsetHeight * progress).toInt()
                    view.requestLayout()
                }
                dB.vwgrpUsingOptions.startAnimation(hideVwgrpUsingOptionsAnimtr)
            } else {
                view.layoutParams.height = originalHeight
                view.requestLayout()
                dB.vwgrpUsingOptions.disappear()
            }
        }

        fun isExpanded () : Boolean {
            return itemView.height > originalHeight
        }

        fun startValueAnimator (updateListener : (progress : Float) -> Unit) {
            ValueAnimator.ofInt(0, 10).setDuration(600).apply {
                setTarget(view)
                setInterpolator(FastOutSlowInInterpolator())
                addUpdateListener {
                    updateListener.invoke(it.animatedFraction)
                }
            }.start()
        }

    }


}