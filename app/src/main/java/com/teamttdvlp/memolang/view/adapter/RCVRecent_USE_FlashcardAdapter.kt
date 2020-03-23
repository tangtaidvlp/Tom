package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ItemRecentUseFlashcardRcvBinding
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard

class RCVRecent_USE_FlashcardAdapter (var context : Context, var list : ArrayList<Flashcard> = ArrayList()) : RecyclerView.Adapter<RCVRecent_USE_FlashcardAdapter.ViewHolder> () {

    private var onItemClickListener : OnItemClickListener? = null

    class ViewHolder (private var dB : ItemRecentUseFlashcardRcvBinding): RecyclerView.ViewHolder(dB.root) {
        fun bind (flashcard : Flashcard) {
            dB.txtText.text = flashcard.text
            dB.txtTranslation.text = flashcard.translation
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding = ItemRecentUseFlashcardRcvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
                onItemClickListener?.onClick(item)
        }
    }

    fun setOnItemClickListener (onItemClickListener: (item: Flashcard) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onClick(item: Flashcard) {
                onItemClickListener(item)
            }
        }
    }

    fun setOnItemClickListener (onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setData (dataList : ArrayList<Flashcard>) {
        list = dataList
        notifyDataSetChanged()
    }

    fun addFlashcardAtTheFirstPosition (newFlashcard : Flashcard) {
        list.add(0, newFlashcard)
        notifyItemInserted(0)
    }

    fun addFlashcard (newFlashcard : Flashcard) {
        list.add(newFlashcard)
        notifyItemInserted(list.size - 1)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick (item : Flashcard)
    }
}