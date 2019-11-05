package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.model.model.Flashcard

class FlashcardRCVAdapter (var context : Context, var list : ArrayList<Flashcard>) : RecyclerView.Adapter<FlashcardRCVAdapter.ViewHolder> () {

    private var onEditButtonClickListener : OnItemClickListener? = null

    private var onItemClickListener : OnItemClickListener? = null

    class ViewHolder (item : View): RecyclerView.ViewHolder(item) {
        var txtText = item.findViewById<TextView>(R.id.txt_text)
        var txtTranslation = item.findViewById<TextView>(R.id.txt_language)
        var btnEdit = item.findViewById<ImageView>(R.id.btn_view_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_flashcard_rcv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.txtText.text = item.toBeTranslatedWord
        holder.txtTranslation.text = item.translatedWord
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(item)
        }
        holder.btnEdit.setOnClickListener {
            onEditButtonClickListener?.onClick(item)
        }
    }

    fun setOnEditButtonClickListener (onEditButtonClickListener: (item: Flashcard) -> Unit) {
        this.onEditButtonClickListener = object : OnItemClickListener {
            override fun onClick(item: Flashcard) {
                onEditButtonClickListener(item)
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

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick (item : Flashcard)
    }
}