package com.teamttdvlp.memolang.view.customview

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.model.CardType
import com.teamttdvlp.memolang.view.adapter.RCVSimpleListAdapter
import kotlin.collections.ArrayList


class ChooseCardTypeRecyclerView  : RecyclerView {

    private lateinit var customedAdapter : RCVSimpleListAdapter

    private var wordTypeList = CardType.TYPE_LIST


    constructor (context : Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initAdapter()
        background = context.getDrawable(R.drawable.round_10dp_white_background)
    }

    private fun initAdapter () {
        customedAdapter =RCVSimpleListAdapter (context)
        customedAdapter.setData(wordTypeList)
        adapter = customedAdapter
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
    }

    fun setOnItemClickListener (onItemClickListener: (item: String) -> Unit) {
        customedAdapter.setOnItemClickListener(onItemClickListener)
    }

}
