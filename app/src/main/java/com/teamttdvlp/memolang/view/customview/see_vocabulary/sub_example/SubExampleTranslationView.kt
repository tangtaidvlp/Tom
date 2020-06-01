package com.teamttdvlp.memolang.view.customview.see_vocabulary.sub_example
import android.content.Context
import android.os.Build
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Dimension.SP
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.customview.see_vocabulary.SUB_SINGLE_MEAN_EXAMPLE_MARGIN_LEFT
import com.teamttdvlp.memolang.view.customview.see_vocabulary.SUB_SINGLE_MEAN_EXAMPLE_TRANSLATION_MARGIN_LEFT

class SubExampleTranslationView (context : Context) : androidx.appcompat.widget.AppCompatTextView (context) {

    init {
        setTextSize(SP, 17f)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            typeface = context.resources.getFont(R.font.seguisb)
        } else {
            typeface = ResourcesCompat.getFont(context, R.font.seguisb)
        }

        val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        linearLayoutParams.leftMargin = SUB_SINGLE_MEAN_EXAMPLE_TRANSLATION_MARGIN_LEFT
        layoutParams = linearLayoutParams
    }

}