package com.teamttdvlp.memolang.view.customview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Dimension.SP
import androidx.core.content.res.ResourcesCompat
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.helper.UnitConverter

class TypeVocabularyTextView(context : Context) : TextView (context) {

    init {
        setTextColor(Color.parseColor("#1F1F1F"))
        setTextSize(SP, 19f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setTypeface(context.resources.getFont(R.font.seguisb))
        } else {
            setTypeface(ResourcesCompat.getFont(context, R.font.seguisb))
        }
        val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        linearLayoutParams.topMargin = UnitConverter.`5dp` * 3
        layoutParams = linearLayoutParams
    }

}