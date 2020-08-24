package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.data.model.entity.flashcard.SetNameUtils
import com.teamttdvlp.memolang.databinding.ItemSimpleFlashcardSetBinding
import com.teamttdvlp.memolang.view.helper.goGONE


class RCV_SimpleChooseFlashcardSetAdapter(var context: Context) :
    RecyclerView.Adapter<RCV_SimpleChooseFlashcardSetAdapter.DataViewHolder>() {

    private var onItemClickListener: ((FlashcardSet) -> Unit)? = null
    var flashcardSetList = ArrayList<FlashcardSet>()

    class DataViewHolder(var dB: ItemSimpleFlashcardSetBinding) : RecyclerView.ViewHolder(dB.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val dB = ItemSimpleFlashcardSetBinding.inflate(LayoutInflater.from(context), parent, false)
        return DataViewHolder(dB)
    }

    override fun getItemCount(): Int {
        return flashcardSetList.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val item = flashcardSetList[position]
        holder.dB.apply {
            txtSetName.text = item.name
            txtSetLanguages.text =
                SetNameUtils.getLanguagePairForm(item.frontLanguage, item.backLanguage)
            root.setOnClickListener {
                onItemClickListener?.invoke(item)
            }
            if (position == flashcardSetList.size - 1) {
                deviderLine.goGONE()
            }
        }
    }

    fun setOnItemClickListener(onItemClick: (FlashcardSet) -> Unit) {
        this.onItemClickListener = onItemClick
    }

    fun setData(data: ArrayList<FlashcardSet>) {
        flashcardSetList.clear()
        flashcardSetList.addAll(data)
        notifyDataSetChanged()
    }

    fun addFlashcardSetAtTop(flashcardSet: FlashcardSet) {
        flashcardSetList.add(0, flashcardSet)
        notifyItemInserted(0)
    }
}