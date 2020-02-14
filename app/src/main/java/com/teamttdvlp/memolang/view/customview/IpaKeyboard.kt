package com.teamttdvlp.memolang.view.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.teamttdvlp.memolang.databinding.SupportIpaKeyboardBinding
import com.teamttdvlp.memolang.view.helper.quickLog

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
            focusedTextView.setText(newText)
        }

        btnI.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnI.text + "]"
            focusedTextView.setText(newText)
        }

        btnU.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnU.text + "]"
            focusedTextView.setText(newText)
        }

        btnLongU.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnLongU.text + "]"
            focusedTextView.setText(newText)
        }

        btnEre.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnEre.text + "]"
            focusedTextView.setText(newText)
        }

        btnEi.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnEi.text + "]"
            focusedTextView.setText(newText)
        }

        btnE.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnE.text + "]"
            focusedTextView.setText(newText)
        }

        btnIr.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnIr.text + "]"
            focusedTextView.setText(newText)
        }

        btnLongIr.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnLongIr.text + "]"
            focusedTextView.setText(newText)
        }

        btnLongO.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnLongO.text + "]"
            focusedTextView.setText(newText)
        }

        btnTour.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnTour.text + "]"
            focusedTextView.setText(newText)
        }

        btnOi.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnOi.text + "]"
            focusedTextView.setText(newText)
        }

        btnOw.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnOw.text + "]"
            focusedTextView.setText(newText)
        }

        btnEa.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnEa.text + "]"
            focusedTextView.setText(newText)
        }

        btnUp.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnUp.text + "]"
            focusedTextView.setText(newText)
        }

        btnLongA.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnLongA.text + "]"
            focusedTextView.setText(newText)
        }

        btnOn.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnOn.text + "]"
            focusedTextView.setText(newText)
        }

        btnAir.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnAir.text + "]"
            focusedTextView.setText(newText)
        }

        btnAi.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnAi.text + "]"
            focusedTextView.setText(newText)
        }

        btnAu.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnAu.text + "]"
            focusedTextView.setText(newText)
        }

        btnP.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnP.text + "]"
            focusedTextView.setText(newText)
        }

        btnB.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnB.text + "]"
            focusedTextView.setText(newText)
        }

        btnT.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnT.text + "]"
            focusedTextView.setText(newText)
        }

        btnD.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnD.text + "]"
            focusedTextView.setText(newText)
        }

        btnSch.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnSch.text + "]"
            focusedTextView.setText(newText)
        }

        btnDz.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnDz.text + "]"
            focusedTextView.setText(newText)
        }

        btnK.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnK.text + "]"
            focusedTextView.setText(newText)
        }

        btnG.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnG.text + "]"
            focusedTextView.setText(newText)
        }

        btnF.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnF.text + "]"
            focusedTextView.setText(newText)
        }

        btnV.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnV.text + "]"
            focusedTextView.setText(newText)
        }

        btnTh.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnTh.text + "]"
            focusedTextView.setText(newText)
        }

        btnDThis.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnDThis.text + "]"
            focusedTextView.setText(newText)
        }

        btnS.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnS.text + "]"
            focusedTextView.setText(newText)
        }

        btnZ.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnZ.text + "]"
            focusedTextView.setText(newText)
        }

        btnLongS.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnLongS.text + "]"
            focusedTextView.setText(newText)
        }

        btn3.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btn3.text + "]"
            focusedTextView.setText(newText)
        }

        btnM.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnM.text + "]"
            focusedTextView.setText(newText)
        }

        btnN.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnN.text + "]"
            focusedTextView.setText(newText)
        }

        btnNg.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnNg.text + "]"
            focusedTextView.setText(newText)
        }

        btnH.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnH.text + "]"
            focusedTextView.setText(newText)
        }

        btnL.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnL.text + "]"
            focusedTextView.setText(newText)
        }

        btnR.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnR.text + "]"
            focusedTextView.setText(newText)
        }

        btnW.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnW.text + "]"
            focusedTextView.setText(newText)
        }

        btnJ.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnJ.text + "]"
            focusedTextView.setText(newText)
        }

        btnI.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnI.text + "]"
            focusedTextView.setText(newText)
        }

        btnStress.setOnClickListener {
            val newText = "[" + focusedTextView.text.toString().removePrefix("[").removeSuffix("]") + btnStress.text + "]"
            focusedTextView.setText(newText)
        }

        btnDelete.setOnClickListener {
            val text = focusedTextView.text.toString()
            if (text?.length != 0) {
                /* "/a/" => "" */
                if (text!!.length >= 3) {
                    val newText = text.substring(0, text.length - 2) + "]"
                    focusedTextView.setText(newText)
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
            quickLog("Android Virtual Keyboard is being showed y: $y and lowestPos: $lowestPosition")
            onDeviceVirtualKeyboardShow?.invoke()
        } else {
            quickLog("Android Virtual Keyboard is being hidden")
        }

    }

}