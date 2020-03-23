package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.helper.appear
import com.teamttdvlp.memolang.view.helper.disappear
import com.teamttdvlp.memolang.view.helper.notContains

class RCVSimpleListAdapter (var context : Context) : RecyclerView.Adapter<RCVSimpleListAdapter.ViewHolder> () {

    private var onItemClickListener : OnItemClickListener? = null

    private var list = ArrayList<String>()

    class ViewHolder (item : View) : RecyclerView.ViewHolder(item) {
        var txt_language = item.findViewById<TextView>(R.id.txt_language)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_simple_text, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder : ViewHolder, position: Int) {
        val item = list[position]
        holder.txt_language.text = item
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(item)
        }
    }

    fun setData (list : ArrayList<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setOnItemClickListener (onItemClickListener: (item: String) -> Unit) {
        this.onItemClickListener = object :
            OnItemClickListener {
            override fun onClick(item: String) {
                onItemClickListener(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onClick (item : String)
    }

}

// The difference is that RCVSimpleListAdapter2's layout doesn't have
// marginStart and marginEnd
// So we can custom them by example padding in layout.xml
class RCVSimpleListAdapter2 (var context : Context) : RecyclerView.Adapter<RCVSimpleListAdapter2.ViewHolder> () {

    private var onItemClickListener : OnItemClickListener? = null

    private var list = ArrayList<String>()

    class ViewHolder (item : View) : RecyclerView.ViewHolder(item) {
        var txt_language = item.findViewById<TextView>(R.id.txt_language)
        var txt_line = item.findViewById<TextView>(R.id.line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_simple_text_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder : ViewHolder, position: Int) {
        val item = list[position]
        holder.txt_language.text = item
        if (position == list.size - 1) {
            holder.txt_line.disappear()
        } else {
            holder.txt_line.appear()
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(item)
        }
    }

    fun setData (data : ArrayList<String>) {
        this.list.clear()
        this.list.addAll(data)
        notifyDataSetChanged()
    }

    fun addToFirst (newSet : String) {
        if (list.notContains(newSet)) {
            this.list.add(0, newSet)
            notifyItemInserted(0)
        }
    }

    fun add (newSet : String) {
        if (list.notContains(newSet)) {
            this.list.add(newSet)
            notifyItemInserted(list.size - 1)
        }
    }

    fun setOnItemClickListener (onItemClickListener: (item: String) -> Unit) {
        this.onItemClickListener = object :
            OnItemClickListener {
            override fun onClick(item: String) {
                onItemClickListener(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onClick (item : String)
    }

}
