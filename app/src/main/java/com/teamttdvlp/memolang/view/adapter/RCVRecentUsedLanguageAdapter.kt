package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.language.Language
import com.teamttdvlp.memolang.view.helper.notContains
import kotlin.collections.ArrayList

class RCVRecentUsedLanguageAdapter (context : Context) : RCVChooseLanguageAdapter (context) {

    private val MAX_ROW = 4

    fun addLanguage (language : String) {
        if (list.notContains(language)) {
            if (list.size == MAX_ROW) {
                list.removeAt(0)
            }
            list.add(language)
            notifyDataSetChanged()
        }
    }

    override fun setData (newData : ArrayList<String>) {
        list.clear()
        if (newData.size > MAX_ROW) {
            // Get MAX_ROW last items
            for (i in newData.size - MAX_ROW..newData.size - 1) {
                list.add(newData[i])
            }
        } else {
            list.addAll(newData)
        }
    }

}

