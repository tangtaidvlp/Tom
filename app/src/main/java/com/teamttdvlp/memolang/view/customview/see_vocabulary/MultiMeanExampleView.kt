package com.teamttdvlp.memolang.view.customview.see_vocabulary

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Dimension.SP
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.helper.UnitConverter

class MultiMeanExampleView (context : Context, text : String) : LinearLayoutCompat (context) {

    private val textView : TextView
    private val icon : ImageView

    init {
        val viewGroup = inflate(context, R.layout.layout_icon_and_text, this)

        textView = viewGroup.findViewById(R.id.txt_text)
        icon = viewGroup.findViewById(R.id.img_icon)
        textView.text = text
        textView.setTextSize(SP, 18f)
        textView.setTextColor(context.resources.getColor(R.color.example_text_blue))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.typeface = context.resources.getFont(R.font.seguisb)
        } else {
            textView.typeface = ResourcesCompat.getFont(context, R.font.seguisb)
        }
        textView.includeFontPadding = false

        icon.setImageResource(R.drawable.image_icon_multi_mean_example)

        val linearLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        linearLayoutParams.leftMargin = MULTI_MEAN_EXAMPLE_MARGIN_LEFT
        layoutParams = linearLayoutParams
    }

    fun getTextView () : TextView {
        return textView
    }

    fun getIcon () : ImageView {
        return icon
    }

}

