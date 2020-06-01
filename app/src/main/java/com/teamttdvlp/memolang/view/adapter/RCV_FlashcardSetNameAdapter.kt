package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.databinding.ItemFlashcardSetNameBinding
import com.teamttdvlp.memolang.view.helper.goGONE

class RCV_FlashcardSetNameAdapter (private var context : Context): RecyclerView.Adapter<RCV_FlashcardSetNameAdapter.DataViewHolder>() {

    var list : ArrayList<FlashcardSet> = ArrayList()

    private var onItemClickListener : ((FlashcardSet) -> Unit)? = null

    class DataViewHolder (var dB : ItemFlashcardSetNameBinding): RecyclerView.ViewHolder(dB.root) {
        fun bind (set : FlashcardSet) {
            dB.txtText.text = set.name
        }
    }

    fun setData (data : ArrayList<FlashcardSet>) {
        this.list.clear()
        this.list.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(ItemFlashcardSetNameBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item)
        }
        if (position == list.size - 1) {
            holder.dB.line.goGONE()
        }
    }

    fun setOnItemClickListener (onClick : (FlashcardSet) -> Unit) {
        this.onItemClickListener = onClick
    }

}