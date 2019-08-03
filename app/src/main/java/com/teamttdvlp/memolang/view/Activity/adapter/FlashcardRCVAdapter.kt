package com.teamttdvlp.memolang.view.Activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.Activity.mockmodel.Flashcard

class FlashcardRCVAdapter (var context : Context, var list : ArrayList<Flashcard>) : RecyclerView.Adapter<FlashcardRCVAdapter.ViewHolder> () {

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
        holder.txtText.text = item.text
        holder.txtTranslation.text = item.translation
        holder.btnEdit.setOnClickListener {
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

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick (item : Flashcard)
    }
}