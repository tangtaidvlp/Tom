package com.teamttdvlp.memolang.view.Activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.Activity.mockmodel.FlashcardSet

class LanguageRCVAdapter (var context : Context, var list : ArrayList<FlashcardSet>) : RecyclerView.Adapter<LanguageRCVAdapter.ViewHolder> () {

    private var onItemClickListener : OnItemClickListener? = null

    class ViewHolder (private var item : View): RecyclerView.ViewHolder(item) {
        var txt_language = item.findViewById<TextView>(R.id.txt_language)
        var btn_view_list = item.findViewById<ImageView>(R.id.btn_view_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageRCVAdapter.ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_language_rcv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageRCVAdapter.ViewHolder, position: Int) {
        val item = list[position]
        holder.txt_language.text = "${item.sourceLang} - ${item.targetLang}"
        holder.btn_view_list.setOnClickListener {
            onItemClickListener?.onClick(item)
        }
    }

    fun setOnItemClickListener (onItemClickListener: (item: FlashcardSet) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onClick(item: FlashcardSet) {
                onItemClickListener(item)
            }
        }
    }

    fun setOnItemClickListener (onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick (item : FlashcardSet)
    }

}