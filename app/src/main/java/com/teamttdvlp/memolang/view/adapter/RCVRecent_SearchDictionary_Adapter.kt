package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.RawVocabulary
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.databinding.ItemSearchDictionaryBinding
import com.teamttdvlp.memolang.view.helper.systemOutLogging

class RCVRecent_SearchDictionary_Adapter(var context : Context) : RecyclerView.Adapter<RCVRecent_SearchDictionary_Adapter.ViewHolder>() {

    val DEFAULT_COLOR = null

    var textColor: Int? = DEFAULT_COLOR

    var searchHistoryList = ArrayList<TypicalRawVocabulary>()
        private set

    private var onItemClickListener: OnItemClickListener? = null

    private var onBtnBringTextUpClickListener: RCVSearchDictionaryAdapter.OnItemClickListener? =
        null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding =
            ItemSearchDictionaryBinding.inflate(LayoutInflater.from(context), parent, false)
        if (textColor != DEFAULT_COLOR) {
            dataBinding.txtText.setTextColor(textColor!!)
        }
        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return searchHistoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = searchHistoryList.get(position)
        holder.bind(item)
        holder.dataBinding.root.setOnClickListener {
            onItemClickListener!!.onClick(item)
        }
        holder.dataBinding.btnBringTextUp.setOnClickListener {
            onBtnBringTextUpClickListener!!.onClick(item)
        }
    }

    fun setData (inputList : ArrayList<TypicalRawVocabulary>) {
        searchHistoryList.addAll(inputList)
        notifyDataSetChanged()
    }

    fun addData (item : TypicalRawVocabulary) {
        for (holder in searchHistoryList) {
            if (item.key == holder.key) {
                searchHistoryList.remove(holder)
                break
            }
        }
        searchHistoryList.add(0, item)
        if (searchHistoryList.size > 20) {
            searchHistoryList.removeAt(searchHistoryList.size - 1)
        }
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: (item: TypicalRawVocabulary) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onClick(item: TypicalRawVocabulary) {
                onItemClickListener.invoke(item)
            }
        }
    }

    fun setOnBtnBringTextUpClickListener(onBtnBringTextUpClickListener: (item: RawVocabulary) -> Unit) {
        this.onBtnBringTextUpClickListener =
            object : RCVSearchDictionaryAdapter.OnItemClickListener {
                override fun onClick(item: RawVocabulary) {
                    onBtnBringTextUpClickListener.invoke(item)
                }
            }

        systemOutLogging("Setted")
    }

    class ViewHolder(val dataBinding: ItemSearchDictionaryBinding) :
        RecyclerView.ViewHolder(dataBinding.root) {

        fun bind(item: TypicalRawVocabulary) {
            dataBinding.txtText.text = item.key
        }
    }

    interface OnItemClickListener {
        fun onClick(item: TypicalRawVocabulary)
    }

    data class VocaTextHolder (var id : Int = 0, var text : String = "")


}