package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.databinding.ItemChooseTargetSetBinding

class ChooseTargetSetRCVAdapter (private var context : Context, var dataList : ArrayList<String>): RecyclerView.Adapter<ChooseTargetSetRCVAdapter.DataViewHolder>() {

    private var onItemClickListener : ((View, String) -> Unit)? = null

    class DataViewHolder (var dB : ItemChooseTargetSetBinding) : RecyclerView.ViewHolder (dB.root) {
        fun bind(data: String) {
            dB.txtSetName.text =  data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(ItemChooseTargetSetBinding.inflate(LayoutInflater.from(context), parent,false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(dataList.get(position))
        holder.dB.root.setOnClickListener {
            onItemClickListener?.invoke(it , dataList.get(position))
        }
    }

    fun setOnItemClickListener(onItemClick: (View, String) -> Unit) {
        this.onItemClickListener = onItemClick
    }

}