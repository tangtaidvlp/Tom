package com.teamttdvlp.memolang.view.customview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.Typeface.ITALIC
import android.os.Build
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Dimension.SP
import androidx.core.content.res.ResourcesCompat
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.customview.MeanVocabularyTextView.Companion.CONTENT_MARGIN_LEFT
import com.teamttdvlp.memolang.view.helper.UnitConverter

class ExampleVocabularyTextView(context : Context) : androidx.appcompat.widget.AppCompatTextView (context) {

    init {
        setTextColor(Color.parseColor("#1F1F1F"))
        setTextSize(SP, 17f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setTypeface(context.resources.getFont(R.font.segoeui))
        } else {
            setTypeface(ResourcesCompat.getFont(context, R.font.segoeui))
        }
        val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        linearLayoutParams.leftMargin = CONTENT_MARGIN_LEFT
        linearLayoutParams.topMargin = UnitConverter.`1dp` * 2
        layoutParams = linearLayoutParams
    }

}