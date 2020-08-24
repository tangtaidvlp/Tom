package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R

class RCV_Generic_SimpleListAdapter<T> (
    var context : Context, private val getTextFromItem : (item : T) -> String) : RecyclerView.Adapter<RCV_Generic_SimpleListAdapter.ViewHolder> () {

    private var onItemClickListener : OnItemClickListener<T>? = null

    private var list = ArrayList<T>()

    class ViewHolder (item : View) : RecyclerView.ViewHolder(item) {
        var txt_text = item.findViewById<TextView>(R.id.txt_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_simple_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder : ViewHolder, position: Int) {
        val item = list[position]
        holder.txt_text.text = getTextFromItem(item)
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(item)
        }
    }


    fun setData (list : ArrayList<T>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener (onItemClickListener: (item: T) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener<T> {
            override fun onClick(item: T) {
                onItemClickListener(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addNewFlashcardSet(newSet: T) {
        list.add(0, newSet)
        notifyItemInserted(0)
    }

    interface OnItemClickListener<T> {
        fun onClick(item: T)
    }

}
