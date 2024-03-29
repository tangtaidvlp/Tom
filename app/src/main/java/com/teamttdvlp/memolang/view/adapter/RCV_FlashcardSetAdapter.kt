package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.databinding.ItemFlashcardSetBinding
import com.teamttdvlp.memolang.databinding.ItemFlashcardSetViewHolderBinding
import com.teamttdvlp.memolang.databinding.PopupFlashcardSetOtherEditOptionsBinding

class RCV_FlashcardSetAdapter(
    var context: Context,
    var list: ArrayList<Deck> = ArrayList()
) : RecyclerView.Adapter<RCV_FlashcardSetAdapter.ViewHolder>() {

    private var originalList = ArrayList<Deck>()

    private var onBtnViewListListener: OnItemClickListener? = null

    private var onBtnAddListener: OnItemClickListener? = null

    private var onItemClickListener: OnItemClickListener? = null

    private var onBtnUseFlashcardClickListener: OnItemClickListener? = null

    private var onBtnReviewFlashcardEasyClickListener: OnItemClickListener? = null

    private var onBtnReviewFlashcardHardClickListener: OnItemClickListener? = null

    private var onBtn_QuizFlashcard_ClickListener: OnItemClickListener? = null

    private var onBtn_Edit_FlashcardSetClickListener: OnItemClickListener? = null

    private var onBtn_Delete_FlashcardSetClickListener: OnItemClickListener? = null


    private val TYPE_NORMAL = 0
    private val TYPE_VIEW_HOLDER = 1

    private val VIEW_HOLDER_TAIL = 1

    private val MINIMUM_ITEM_COUNT_ON_SCREEN = 5

    override fun getItemViewType(position: Int): Int {
//        val thereIsTooFewItemOnScreen = list.size <= MINIMUM_ITEM_COUNT_ON_SCREEN
//        if (thereIsTooFewItemOnScreen) {
//            if (position < list.size)
//                return TYPE_NORMAL
//            else
//                return TYPE_VIEW_HOLDER
//        } else { // There is more than #MINIMUM_ITEM_COUNT_ON_SCREEN item on screen
//            if (position == list.size - 1 + VIEW_HOLDER_TAIL) {
//                return TYPE_VIEW_HOLDER
//            }
//            return  TYPE_NORMAL
//        }

        return TYPE_NORMAL
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

                dataBinding.vwgrpLearning.setOnClickListener {
                    onBtn_QuizFlashcard_ClickListener?.onClick(item)
                }

                // Popups's Events
                val inflater = getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popUpBinding = PopupFlashcardSetOtherEditOptionsBinding.inflate(inflater)
                val popupWindow = PopupWindow(popUpBinding.root, WRAP_CONTENT, WRAP_CONTENT, true)

                popUpBinding.vwgrpEdit.setOnClickListener {
                    onBtn_Edit_FlashcardSetClickListener!!.onClick(item)
                    popupWindow.dismiss()
                }

                popUpBinding.vwgrpDeleteSet.setOnClickListener {
                    onBtn_Delete_FlashcardSetClickListener!!.onClick(item)
                    popupWindow.dismiss()
                }

                dataBinding.btnOtherOptions.setOnClickListener { view ->
                    popupWindow.showAsDropDown(view)
                }
            } else {
                throw Exception("Unknown card exception: RCV_FlashcardSetAdapter.kt")
            }
        } else {
            // DO NOTHING, BECAUSE THIS IS VIEW HOLDER
        }
    }


    fun setOnBtnEdit_FlashcardSetClickListener(onBtnEditFlashcardListener: (item: Deck) -> Unit) {
        this.onBtn_Edit_FlashcardSetClickListener = object : OnItemClickListener {
            override fun onClick(item: Deck) {
                onBtnEditFlashcardListener(item)
            }
        }
    }

    fun setOnBtnDelete_FlashcardSetClickListener(onBtnDeleteFlashcardListener: (item: Deck) -> Unit) {
        this.onBtn_Delete_FlashcardSetClickListener = object : OnItemClickListener {
            override fun onClick(item: Deck) {
                onBtnDeleteFlashcardListener(item)
            }
        }
    }

    fun setOnBtnViewListClickListener(onBtnViewListListener: (item: Deck) -> Unit) {
        this.onBtnViewListListener = object : OnItemClickListener {
            override fun onClick(item: Deck) {
                onBtnViewListListener(item)
            }
        }
    }


    fun setOnBtn_GoToQuizActivity_ClickListener(onBtnGoToQuizActivityClickListener: (item: Deck) -> Unit) {
        this.onBtn_QuizFlashcard_ClickListener = object : OnItemClickListener {
            override fun onClick(item: Deck) {
                onBtnGoToQuizActivityClickListener(item)
            }
        }
    }

    fun setOnBtnAddClickListener(onBtnAddListener: (item: Deck) -> Unit) {
        this.onBtnAddListener = object : OnItemClickListener {
            override fun onClick(item: Deck) {
                onBtnAddListener(item)
            }
        }
    }

    fun setOnItemClickListener(onClick: (Deck) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onClick(item: Deck) {
                onClick(item)
            }
        }
    }

    fun setOnBtnUseFlashcardClickListener(onClick: (Deck) -> Unit) {
        onBtnUseFlashcardClickListener = object : OnItemClickListener {
            override fun onClick(item: Deck) {
                onClick.invoke(item)
            }
        }
    }


    fun setOnBtn_GoToPuzzleActivity_ClickListener(onClick: (Deck) -> Unit) {
        onBtnReviewFlashcardEasyClickListener = object : OnItemClickListener {
            override fun onClick(item: Deck) {
                onClick.invoke(item)
            }
        }
    }


    fun setOnBtn_GoToWritingActivity_ClickListener(onClick: (Deck) -> Unit) {
        onBtnReviewFlashcardHardClickListener = object : OnItemClickListener {
            override fun onClick(item: Deck) {
                onClick.invoke(item)
            }
        }
    }

    fun setData(data: ArrayList<Deck>) {
        list.clear()
        list.addAll(data)
        originalList.clear()
        originalList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
//        return if (list.size < MINIMUM_ITEM_COUNT_ON_SCREEN)
//            MINIMUM_ITEM_COUNT_ON_SCREEN
//        else list.size + VIEW_HOLDER_TAIL
        return list.size
    }

    fun deleteFlashcardSet(deck: Deck) {
        val deletedPos = list.indexOf(deck)
        this.list.remove(deck)
        this.originalList.remove(deck)
        notifyItemRemoved(deletedPos)
    }

    // TODO (Process user press 2 times and then add the same 2 flashcard sets)
    fun updateFlashcardSetName(deck: Deck, newName: String) {
        val updatedPos = list.indexOf(deck)
        deck.name = newName
        this.list[updatedPos] = deck
        this.originalList[updatedPos] = deck
        notifyItemChanged(updatedPos)
    }

    fun addNewSet(newSet: Deck) {
        this.list.add(0, newSet)
        this.originalList.add(0, newSet)
        notifyDataSetChanged()
    }

    fun search(keyWord: String) {
        list.clear()
        for (set in originalList) {
            if (set.name.startsWith(keyWord)) {
                list.add(set)
            }
        }
        notifyDataSetChanged()
    }

    fun stopSearching() {
        list.clear()
        list.addAll(originalList)
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onClick(item: Deck)
    }

    class ViewHolder(var dB: ViewDataBinding) : RecyclerView.ViewHolder(dB.root)


}