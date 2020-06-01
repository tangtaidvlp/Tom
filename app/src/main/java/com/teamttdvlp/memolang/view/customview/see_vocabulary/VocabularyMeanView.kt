package com.teamttdvlp.memolang.view.customview.see_vocabulary

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Dimension.SP
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.helper.UnitConverter
import com.teamttdvlp.memolang.view.helper.dp

class VocabularyMeanView (context : Context, text : String) : LinearLayoutCompat(context) {

    val textView : TextView

    val icon : ImageView

    val button : ImageView

    init {
        val viewGroup = inflate(context, R.layout.layout_icon_and_text_and_button, this)
        weightSum = 3f

        textView = viewGroup.findViewById(R.id.txt_text)
        icon = viewGroup.findViewById(R.id.img_icon)
        button = viewGroup.findViewById(R.id.img_button)

        textView.text = text
        textView.setTextSize(SP, 18f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.typeface = context.resources.getFont(R.font.seguisb)
        } else {
            textView.typeface = ResourcesCompat.getFont(context, R.font.seguisb)
        }

        icon.setImageResource(R.drawable.image_icon_vocabulary_mean)

        val linearLayoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        linearLayoutParams.leftMargin = VOCABULARY_MEAN_MARGIN_LEFT
        layoutParams = linearLayoutParams
    }

}