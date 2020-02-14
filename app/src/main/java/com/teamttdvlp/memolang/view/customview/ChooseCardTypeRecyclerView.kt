package com.teamttdvlp.memolang.view.customview

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.adapter.RCVSimpleListAdapter
import kotlin.collections.ArrayList


class ChooseCardTypeRecyclerView (context : Context, attributeSet: AttributeSet) : RecyclerView(context, attributeSet) {

    private lateinit var customedAdapter : RCVSimpleListAdapter

    private var wordTypeList = ArrayList<String>()

    init {
        wordTypeList.add("Nội động từ")
        wordTypeList.add("Ngoại động từ")
        wordTypeList.add("Danh từ")
        wordTypeList.add("Tính từ")
        wordTypeList.add("Trạng từ")
        wordTypeList.add("Giới từ")
        wordTypeList.add("Từ viết tắt")
    }

    init {
        initAdapter()
        background = context.getDrawable(R.drawable.round_10dp_white_background)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        layoutParams.width = WRAP_CONTENT
    }

    private fun initAdapter () {
        customedAdapter =
            RCVSimpleListAdapter (
                context
            )
        customedAdapter.setData(wordTypeList)
        adapter = customedAdapter
        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    fun setOnItemClickListener (onItemClickListener: (item: String) -> Unit) {
        customedAdapter.setOnItemClickListener(onItemClickListener)
    }

}
