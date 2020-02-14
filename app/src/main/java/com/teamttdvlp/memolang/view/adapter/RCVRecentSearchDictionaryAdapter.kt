package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.databinding.ItemSearchDictionaryBinding
import com.teamttdvlp.memolang.model.entity.vocabulary.SearchVocaHistoryHolder

class RCVRecentSearchDictionaryAdapter(var context : Context) : RecyclerView.Adapter<RCVRecentSearchDictionaryAdapter.ViewHolder>() {

    var searchHistoryList = ArrayList<SearchVocaHistoryHolder>()
    private set

    private var onItemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RCVRecentSearchDictionaryAdapter.ViewHolder {
        val dataBinding = ItemSearchDictionaryBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return searchHistoryList.size
    }

    override fun onBindViewHolder(holder: RCVRecentSearchDictionaryAdapter.ViewHolder, position: Int) {
        val item = searchHistoryList.get(position)
        holder.bind(item)
        holder.dataBinding.root.setOnClickListener {
            onItemClickListener?.onClick(item.key, item.content)
        }
    }

    fun setData (inputList : ArrayList<SearchVocaHistoryHolder>) {
        searchHistoryList.addAll(inputList)
        notifyDataSetChanged()
    }

    fun addData (item : SearchVocaHistoryHolder) {
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

    fun setOnItemClickListener(onItemClickListener : (vocabulary : String, content : String) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onClick(vocabulary: String, content: String) {
                onItemClickListener.invoke(vocabulary, content)
            }
        }
    }

    class ViewHolder (val dataBinding : ItemSearchDictionaryBinding): RecyclerView.ViewHolder (dataBinding.root) {

        fun bind(item : SearchVocaHistoryHolder) {
            dataBinding.txtText.text = item.key
        }
    }
    interface OnItemClickListener {
        fun onClick (vocabulary : String, content : String)
    }

    data class VocaTextHolder (var id : Int = 0, var text : String = "")


}