package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.databinding.ItemFlashcardSetBinding
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.databinding.ItemFlashcardSetViewHolderBinding

class RCV_FlashcardSetAdapter (var context : Context, var list : ArrayList<FlashcardSet> = ArrayList()) : RecyclerView.Adapter<RCV_FlashcardSetAdapter.ViewHolder> () {

    private var onBtnViewListListener : OnItemClickListener? = null

    private var onBtnAddListener : OnItemClickListener? = null

    private var onItemClickListener : OnItemClickListener? = null

    private var onBtnUseFlashcardClickListener : OnItemClickListener? = null

    private var onBtnReviewFlashcardEasyClickListener : OnItemClickListener? = null

    private var onBtnReviewFlashcardHardClickListener : OnItemClickListener? = null


    private val TYPE_NORMAL = 0
    private val TYPE_VIEW_HOLDER = 1

    private val VIEW_HOLDER_TAIL = 1

    private val MINIMUM_ITEM_COUNT_ON_SCREEN = 4

    override fun getItemViewType(position: Int): Int {
        val thereIsTooFewItemOnScreen = list.size <= MINIMUM_ITEM_COUNT_ON_SCREEN
        if (thereIsTooFewItemOnScreen) {
            if (position < list.size)
                return TYPE_NORMAL
            else
                return TYPE_VIEW_HOLDER
        } else { // There is more than #MINIMUM_ITEM_COUNT_ON_SCREEN item on screen
            if (position == list.size - 1 + VIEW_HOLDER_TAIL) {
                return TYPE_VIEW_HOLDER
            }
            return  TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = if (viewType == TYPE_NORMAL)
                                            ItemFlashcardSetBinding.inflate(LayoutInflater.from(context), parent, false)
                                    else
                                            ItemFlashcardSetViewHolderBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemIsNotViewHolder = (position <= list.size - 1)
        if (itemIsNotViewHolder) {
            val item = list[position]
            val dataBinding = holder.dB
            if (dataBinding is ItemFlashcardSetBinding) {
                dataBinding.txtCardCount.text = item.flashcards.size.toString()
                dataBinding.txtSetName.text = item.name
                dataBinding.btnViewList.setOnClickListener {
                    onBtnViewListListener?.onClick(item)
                }

                dataBinding.btnAddFlashcard.setOnClickListener {
                    onBtnAddListener?.onClick(item)
                }

                dataBinding.vwgrpReviewFlashcardHard.setOnClickListener {
                    onBtnReviewFlashcardHardClickListener?.onClick(item)
                }

                dataBinding.vwgrpReviewFlashcardEasy.setOnClickListener {
                    onBtnReviewFlashcardEasyClickListener?.onClick(item)
                }

                dataBinding.vwgrpUseFlashcard.setOnClickListener {
                    onBtnUseFlashcardClickListener?.onClick(item)
                }
            } else {
                throw Exception ("Unknown card exception: RCV_FlashcardSetAdapter.kt")
            }
        }
        else {
            // DO NOTHING, BECAUSE THIS IS VIEW HOLDER
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
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (list.size <= MINIMUM_ITEM_COUNT_ON_SCREEN)
            MINIMUM_ITEM_COUNT_ON_SCREEN
        else list.size + VIEW_HOLDER_TAIL
    }

    interface OnItemClickListener {
        fun onClick (item : FlashcardSet)
    }

    class ViewHolder (var dB : ViewDataBinding) : RecyclerView.ViewHolder(dB.root) {



    }


}