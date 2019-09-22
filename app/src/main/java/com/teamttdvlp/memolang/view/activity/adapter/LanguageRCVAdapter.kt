package com.teamttdvlp.memolang.view.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.model.model.FlashcardSet

class LanguageRCVAdapter (var context : Context, var list : ArrayList<FlashcardSet> = ArrayList()) : RecyclerView.Adapter<LanguageRCVAdapter.ViewHolder> () {

    private var onBtnViewListListener : OnItemClickListener? = null

    private var onItemClickListener : View.OnClickListener? = null

    class ViewHolder (item : View): RecyclerView.ViewHolder(item) {
        var txt_language = item.findViewById<TextView>(R.id.txt_language)
        var btn_view_list = item.findViewById<ImageView>(R.id.btn_view_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_language_rcv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.txt_language.text = item.id.replace("-", " - ")
        holder.btn_view_list.setOnClickListener {
            onBtnViewListListener?.onClick(item)
        }
        holder.txt_language.setOnClickListener {
            onItemClickListener?.onClick(it)
        }
    }

    fun setOnBtnViewListClickListener (onBtnViewListListener: (item: FlashcardSet) -> Unit) {
        this.onBtnViewListListener = object : OnItemClickListener {
            override fun onClick(item: FlashcardSet) {
                onBtnViewListListener(item)
            }
        }
    }

    fun setOnBtnViewListClickListener (onBtnViewListListener: OnItemClickListener) {
        this.onBtnViewListListener = onBtnViewListListener
    }

    fun setOnItemClickListener (onItemClickListener : (View?) -> Unit) {
        this.onItemClickListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                onItemClickListener(p0)
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

}