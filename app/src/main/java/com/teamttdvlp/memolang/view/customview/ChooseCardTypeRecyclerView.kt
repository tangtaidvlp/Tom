package com.teamttdvlp.memolang.view.customview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.model.CardType
import com.teamttdvlp.memolang.view.adapter.RCVSimpleListAdapter
import kotlin.collections.ArrayList


class ChooseCardTypeRecyclerView  : RecyclerView {

    private lateinit var cardTypeListAdapter : RCVSimpleListAdapter

    constructor (context : Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initAdapter()
    }

    fun getTypeList () : ArrayList<String> {
        return cardTypeListAdapter.getTypeList()
    }

    private fun initAdapter () {
        cardTypeListAdapter = RCVSimpleListAdapter (context)
        cardTypeListAdapter.setData(CardType.TYPE_LIST)
        adapter = cardTypeListAdapter
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
    }

    fun addTypes (types : ArrayList<String>) {
        for (type in types) {
            if (getTypeList().contains(type)) {
                getTypeList().remove(type)
            }
            getTypeList().add(0, type)
        }
        cardTypeListAdapter.notifyDataSetChanged()
    }

    fun addType (type : String) {
        if (getTypeList().contains(type)) {
            getTypeList().remove(type)
        }
        getTypeList().add(0, type)
        cardTypeListAdapter.notifyItemInserted(0)
    }

    fun setOnItemClickListener (onItemClickListener: (item: String) -> Unit) {
        cardTypeListAdapter.setOnItemClickListener(onItemClickListener)
    }

    fun filtTypeAndUpdateView(type: String) : ArrayList<String> {
        return cardTypeListAdapter.filtType(type)
    }
}
