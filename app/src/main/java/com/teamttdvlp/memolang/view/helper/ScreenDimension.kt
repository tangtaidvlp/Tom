package com.teamttdvlp.memolang.view.helper

import android.content.res.Resources

object ScreenDimension {
    var screenWidth: Int = 0
        get() {
            return Resources.getSystem().displayMetrics.widthPixels
        }

    val screenHeight: Int
        get() {
            return Resources.getSystem().displayMetrics.heightPixels
        }


}