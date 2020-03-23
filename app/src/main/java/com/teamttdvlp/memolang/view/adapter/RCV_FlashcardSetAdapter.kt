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
import com.teamttdvlp.memolang.databinding.ItemFlashcardSetBinding
import com.teamttdvlp.memolang.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.view.helper.*

class RCV_FlashcardSetAdapter (var context : Context, var list : ArrayList<FlashcardSet> = ArrayList()) : RecyclerView.Adapter<RCV_FlashcardSetAdapter.ViewHolder> () {

    private var onBtnViewListListener : OnItemClickListener? = null

    private var onBtnAddListener : OnItemClickListener? = null

    private var onItemClickListener : OnItemClickListener? = null

    private var onBtnUseFlashcardClickListener : OnItemClickListener? = null

    private var onBtnReviewFlashcardEasyClickListener : OnItemClickListener? = null

    private var onBtnReviewFlashcardHardClickListener : OnItemClickListener? = null

//    private var expandedHolder : ViewHolder? = null

//    private var expandedPosition : Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemFlashcardSetBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

//        if (expandedHolder == holder) {
//            if (position == expandedPosition) {
//                holder.expand(false)
//            } else {
//                holder.collapse(false)
//            }
//        }

        holder.dB.txtCardCount.text = item.flashcards.size.toString()
        holder.dB.txtSetName.text = item.name
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

//        holder.dB.txtSetName.setOnClickListener {
//            if (holder.isExpanded()) {
//                holder.collapse(true)
//                expandedHolder = null
//                expandedPosition = -1
//            } else {
//                if (expandedHolder != null ) {
//                    if (expandedHolder!!.isExpanded()) {
//                        expandedHolder!!.collapse(true)
//                    }
//                }
//                holder.expand(true)
//                expandedHolder = holder
//                expandedPosition = position
//            }
//            onItemClickListener?.onClick(item)
//        }

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

    class ViewHolder (var dB : ItemFlashcardSetBinding) : RecyclerView.ViewHolder(dB.root) {
    }


}