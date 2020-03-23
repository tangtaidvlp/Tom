package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.databinding.ItemFlashcardSetNameBinding
import com.teamttdvlp.memolang.view.helper.disappear

class RCV_FlashcardSetNameAdapter (private var context : Context): RecyclerView.Adapter<RCV_FlashcardSetNameAdapter.DataViewHolder>() {

    private val CREATE_NEW_SET_ITEM = "+ Create new set"

    var list : ArrayList<String> = ArrayList()

    private var onItemClickListener : ((String) -> Unit)? = null

    private var onCreateNewSetItemClick : (() -> Unit)? = null

    class DataViewHolder (var dB : ItemFlashcardSetNameBinding): RecyclerView.ViewHolder(dB.root) {
        fun bind (setName : String) {
            dB.txtText.text = setName
        }
    }

    fun setData (data : ArrayList<String>) {
        this.list.clear()
        this.list.addAll(data)
        this.list.add(CREATE_NEW_SET_ITEM)
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
        if (item == CREATE_NEW_SET_ITEM) {
            holder.itemView.setOnClickListener {
                onCreateNewSetItemClick?.invoke()
            }
            holder.dB.line.disappear()
        } else {
            holder.itemView.setOnClickListener {
                onItemClickListener?.invoke(item)
            }
        }

    }

    fun setOnItemClickListener (onClick : (String) -> Unit) {
        this.onItemClickListener = onClick
    }

    fun setOnCreateNewSet_ItemClick (onClick : () -> Unit) {
        this.onCreateNewSetItemClick = onClick
    }

}