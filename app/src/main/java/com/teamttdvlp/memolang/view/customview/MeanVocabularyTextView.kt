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


class MeanVocabularyTextView(context : Context) : TextView (context) {

    companion object {
        val CONTENT_MARGIN_LEFT = UnitConverter.`10dp`
    }

    init {
        setTextColor(context.resources.getColor(R.color.app_blue))
        setTextSize(SP, 19f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setTypeface(context.resources.getFont(R.font.segoeui))
        } else {
            setTypeface(ResourcesCompat.getFont(context, R.font.segoeui))
        }
        val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        linearLayoutParams.topMargin = UnitConverter.`5dp` * 2
        linearLayoutParams.leftMargin = CONTENT_MARGIN_LEFT
        layoutParams = linearLayoutParams
    }

    fun clearMarginTop() {
        val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        linearLayoutParams.topMargin = 0
        linearLayoutParams.leftMargin = CONTENT_MARGIN_LEFT
        layoutParams = linearLayoutParams
    }
}