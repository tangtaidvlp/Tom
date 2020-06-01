package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.databinding.ItemFlashcardSetBinding
import com.teamttdvlp.memolang.view.helper.goINVISIBLE

class RCV_FlashcardSetsBackground_Adapter (var context : Context) : RecyclerView.Adapter<RCV_FlashcardSetsBackground_Adapter.ViewHolder> () {

    override fun getItemCount(): Int {
        return 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemFlashcardSetBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        for (viewChild in (holder.itemView as ViewGroup).children) {
            viewChild.goINVISIBLE()
        }
        holder.dB.root.setBackgroundColor(Color.BLACK)
    }


    class ViewHolder (var dB : ItemFlashcardSetBinding) : RecyclerView.ViewHolder(dB.root) {

    }


}