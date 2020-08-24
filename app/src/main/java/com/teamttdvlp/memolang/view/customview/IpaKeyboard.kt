package com.teamttdvlp.memolang.view.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.teamttdvlp.memolang.databinding.SupportIpaKeyboardBinding
import com.teamttdvlp.memolang.view.helper.log

class IpaKeyboard (context : Context, attrSet: AttributeSet): LinearLayout(context, attrSet) {

    val db : SupportIpaKeyboardBinding

    private var lowestPosition = 0f

    private var onDeviceVirtualKeyboardShow : (() -> Unit)? = null

    private lateinit var focusedTextView : TextView

    private var onBtnDoneClickListener : OnClickListener? = null

    init {
        db = SupportIpaKeyboardBinding.inflate(LayoutInflater.from(context), this, true)
        addEvents()
    }

    private fun addEvents() { db.apply {

        btnLongI.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnLongI.text + "]"
            focusedTextView.text = newText
        }

        btnI.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnI.text + "]"
            focusedTextView.text = newText
        }

        btnU.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnU.text + "]"
            focusedTextView.text = newText
        }

        btnLongU.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnLongU.text + "]"
            focusedTextView.text = newText
        }

        btnEre.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnEre.text + "]"
            focusedTextView.text = newText
        }

        btnEi.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnEi.text + "]"
            focusedTextView.text = newText
        }

        btnE.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnE.text + "]"
            focusedTextView.text = newText
        }

        btnIr.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnIr.text + "]"
            focusedTextView.text = newText
        }

        btnLongIr.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnLongIr.text + "]"
            focusedTextView.text = newText
        }

        btnLongO.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnLongO.text + "]"
            focusedTextView.text = newText
        }

        btnTour.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnTour.text + "]"
            focusedTextView.text = newText
        }

        btnOi.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnOi.text + "]"
            focusedTextView.text = newText
        }

        btnOw.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnOw.text + "]"
            focusedTextView.text = newText
        }

        btnEa.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnEa.text + "]"
            focusedTextView.text = newText
        }

        btnUp.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnUp.text + "]"
            focusedTextView.text = newText
        }

        btnLongA.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnLongA.text + "]"
            focusedTextView.text = newText
        }

        btnOn.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnOn.text + "]"
            focusedTextView.text = newText
        }

        btnAir.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnAir.text + "]"
            focusedTextView.text = newText
        }

        btnAi.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnAi.text + "]"
            focusedTextView.text = newText
        }

        btnAu.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnAu.text + "]"
            focusedTextView.text = newText
        }

        btnP.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnP.text + "]"
            focusedTextView.text = newText
        }

        btnB.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnB.text + "]"
            focusedTextView.text = newText
        }

        btnT.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnT.text + "]"
            focusedTextView.text = newText
        }

        btnD.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnD.text + "]"
            focusedTextView.text = newText
        }

        btnSch.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnSch.text + "]"
            focusedTextView.text = newText
        }

        btnDz.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnDz.text + "]"
            focusedTextView.text = newText
        }

        btnK.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnK.text + "]"
            focusedTextView.text = newText
        }

        btnG.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnG.text + "]"
            focusedTextView.text = newText
        }

        btnF.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnF.text + "]"
            focusedTextView.text = newText
        }

        btnV.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnV.text + "]"
            focusedTextView.text = newText
        }

        btnTh.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnTh.text + "]"
            focusedTextView.text = newText
        }

        btnDThis.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnDThis.text + "]"
            focusedTextView.text = newText
        }

        btnS.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnS.text + "]"
            focusedTextView.text = newText
        }

        btnZ.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnZ.text + "]"
            focusedTextView.text = newText
        }

        btnLongS.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnLongS.text + "]"
            focusedTextView.text = newText
        }

        btn3.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btn3.text + "]"
            focusedTextView.text = newText
        }

        btnM.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnM.text + "]"
            focusedTextView.text = newText
        }

        btnN.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnN.text + "]"
            focusedTextView.text = newText
        }

        btnNg.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnNg.text + "]"
            focusedTextView.text = newText
        }

        btnH.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnH.text + "]"
            focusedTextView.text = newText
        }

        btnL.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnL.text + "]"
            focusedTextView.text = newText
        }

        btnR.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnR.text + "]"
            focusedTextView.text = newText
        }

        btnW.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnW.text + "]"
            focusedTextView.text = newText
        }

        btnJ.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnJ.text + "]"
            focusedTextView.text = newText
        }

        btnI.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnI.text + "]"
            focusedTextView.text = newText
        }

        btnStress.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnStress.text + "]"
            focusedTextView.text = newText
        }

        btnDelete.setOnClickListener {
            val text = focusedTextView.text.toString()
            if (text.length != 0) {
                /* "/a/" => "" */
                if (text.length >= 3) {
                    val newText = text.substring(0, text.length - 2) + "]"
                    focusedTextView.text = newText
                }
            }
        }

        btnDone.setOnClickListener {
            onBtnDoneClickListener?.onClick(it)
        }

    }}

    fun setOnBtnDoneClickListener (onClick: () ->Unit) {
        onBtnDoneClickListener = object : OnClickListener {
            override fun onClick(v: View?) {
                onClick.invoke()
            }
        }
    }

    fun setOnDeviceVirtualKeyboardShow (onShow : () -> Unit) {
        onDeviceVirtualKeyboardShow = onShow
    }

    fun setFocusedText (textView : TextView) {
        focusedTextView = textView
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        if (lowestPosition == 0f) {
            lowestPosition = y
        } else {
            // The y axis of android points down, so multiply to minus 1 to make it easier
            // to understand
            val isNotLowest = -y < -lowestPosition
            if (isNotLowest) {
                lowestPosition = y
            }
        }

        if (-y > -lowestPosition) {
            log("Android Virtual Keyboard is being showed y: $y and lowestPos: $lowestPosition")
            onDeviceVirtualKeyboardShow?.invoke()
        } else {
            log("Android Virtual Keyboard is being hiddenk")
        }

    }

}