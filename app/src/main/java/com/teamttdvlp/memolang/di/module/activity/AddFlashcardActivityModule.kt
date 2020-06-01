package com.teamttdvlp.memolang.di.module.activity

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.activity.AddFlashcardActivity
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCVRecentUsedLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCV_FlashcardSetNameAdapter
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class AddFlashcardActivityModule {

  @Provides
  @Named("HighlightTextAnim")
  fun provideHighlightTextAnim(activity: AddFlashcardActivity): Animation {
    return AnimationUtils.loadAnimation(activity, R.anim.hightlight_text)
  }

  @Provides
  fun provideChooseLanguageAdapter (activity : AddFlashcardActivity) : RCVChooseLanguageAdapter{
    return RCVChooseLanguageAdapter(activity)
  }

  @Provides
  fun provideRecentUsedLanguageAdapter (activity : AddFlashcardActivity) : RCVRecentUsedLanguageAdapter{
    return RCVRecentUsedLanguageAdapter(activity)
  }

  @Provides
  fun provideRCVFlashcardSetNameAdapter (activity : AddFlashcardActivity) : RCV_FlashcardSetNameAdapter {
    return RCV_FlashcardSetNameAdapter(activity)
  }
}