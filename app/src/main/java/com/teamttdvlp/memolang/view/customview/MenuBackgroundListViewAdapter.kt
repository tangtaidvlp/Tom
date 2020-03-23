package com.teamttdvlp.memolang.view.customview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SimpleAdapter
import android.widget.TextView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.helper.dp
import java.util.*

class MenuBackgroundListViewAdapter (var context : Context): BaseAdapter () {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = LayoutInflater.from(context).inflate(R.layout.item_black_background, parent, false)
        var txtTex = view.findViewById<TextView>(R.id.txt_background_text)
        txtTex.layoutParams.width = 100.dp() + Random().nextInt(200.dp())
        var txtTrans = view.findViewById<TextView>(R.id.txt_background_translation)
        txtTrans.layoutParams.width = 100.dp() + Random().nextInt(200.dp())
        return view
    }

    override fun getItem(position: Int): Any {
        return ""
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return 8
    }

}