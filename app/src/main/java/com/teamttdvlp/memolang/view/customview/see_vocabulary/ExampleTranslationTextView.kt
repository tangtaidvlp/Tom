package com.teamttdvlp.memolang.view.customview.see_vocabulary

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.widget.LinearLayout
import androidx.annotation.Dimension.SP
import androidx.core.content.res.ResourcesCompat
import com.teamttdvlp.memolang.R

class ExampleTranslationTextView(context : Context) : androidx.appcompat.widget.AppCompatTextView (context) {

    init {
        setTextSize(SP, 18f)
        setTextColor(Color.BLACK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            typeface = context.resources.getFont(R.font.seguisb)
        } else {
            typeface = ResourcesCompat.getFont(context, R.font.seguisb)
        }
        includeFontPadding = false

        val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        linearLayoutParams.leftMargin = EXAMPLE_TRANSLATION_MARGIN_LEFT
        layoutParams = linearLayoutParams
    }

}