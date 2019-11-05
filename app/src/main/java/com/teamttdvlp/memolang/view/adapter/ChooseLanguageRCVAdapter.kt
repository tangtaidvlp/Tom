package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.model.model.Language

class ChooseLanguageRCVAdapter (var context : Context) : RecyclerView.Adapter<ChooseLanguageRCVAdapter.ViewHolder> () {

    private var onItemClickListener : OnItemClickListener? = null

    private var list = Language.languageList

    class ViewHolder (item : View): RecyclerView.ViewHolder(item) {
        var txt_language = item.findViewById<TextView>(R.id.txt_language)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_choose_language_rcv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder : ViewHolder, position: Int) {
        val item = list[position]
        holder.txt_language.text = item
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(item)
        }
    }

    fun setOnItemClickListener (onItemClickListener: (item: String) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onClick(item: String) {
                onItemClickListener(item)
            }
        }
    }

    fun setOnItemClickListener (onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onClick (item : String)
    }

}