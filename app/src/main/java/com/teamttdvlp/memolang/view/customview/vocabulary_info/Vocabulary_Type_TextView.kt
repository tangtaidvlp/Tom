package com.teamttdvlp.memolang.view.customview.vocabulary_info

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.widget.LinearLayout
import androidx.annotation.Dimension.SP
import androidx.core.content.res.ResourcesCompat
import com.teamttdvlp.memolang.R

class Vocabulary_Type_TextView(context: Context) :
    androidx.appcompat.widget.AppCompatTextView(context) {

    init {
        setTextColor(Color.BLACK)
        setTextSize(SP, 22f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            typeface = context.resources.getFont(R.font.segoeuib)
        } else {
            typeface = ResourcesCompat.getFont(context, R.font.segoeuib)
        }
        val linearLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayoutParams.topMargin = VOCABULARY_TYPE_MARGIN_TOP
        linearLayoutParams.leftMargin = VOCABULARY_TYPE_MARGIN_LEFT
        layoutParams = linearLayoutParams
    }

    fun clearMarginTop() {
        (layoutParams as LinearLayout.LayoutParams).topMargin = 0
        requestLayout()
    }

}