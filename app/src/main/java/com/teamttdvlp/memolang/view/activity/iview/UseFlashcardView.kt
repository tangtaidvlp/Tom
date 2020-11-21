package com.teamttdvlp.memolang.view.activity.iview

import android.graphics.Bitmap

interface UseFlashcardView : View {

    fun onEndReviewing()

    fun showSpeakTextError(error: String)

    fun lock_ShowPreviousCard_Function()

    fun unlock_ShowPreviousCard_Function()

    fun onPassACard(passedCardCount: Int, forgottenCardCount: Int)

    fun onGetFrontIllustration(illustration: Bitmap)

    fun onGetBackIllustration(illustration: Bitmap)

    fun onLoadAllIllustrationFinish()
}