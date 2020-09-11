package com.teamttdvlp.memolang.view.customview.vocabulary_info.other_structure
import android.content.Context
import android.os.Build
import android.widget.LinearLayout
import androidx.annotation.Dimension.SP
import androidx.core.content.res.ResourcesCompat
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.customview.vocabulary_info.SUB_SINGLE_MEAN_EXAMPLE_TRANSLATION_MARGIN_LEFT

class OtherStructure_ExampleTranslation_View(context: Context) :
    androidx.appcompat.widget.AppCompatTextView(context) {

    init {
        setTextSize(SP, 17f)
        setTextColor(context.resources.getColor(R.color.light_grey))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            typeface = context.resources.getFont(R.font.segoeui)
        } else {
            typeface = ResourcesCompat.getFont(context, R.font.segoeui)
        }
        alpha = 0.85f

        val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        linearLayoutParams.leftMargin = SUB_SINGLE_MEAN_EXAMPLE_TRANSLATION_MARGIN_LEFT
        layoutParams = linearLayoutParams
    }

}