package com.teamttdvlp.memolang.di.module.activity

import android.animation.Animator
import android.animation.AnimatorInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.view.activity.RetrofitAddFlashcardActivity
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCVRecentUsedLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCV_FlashcardSetNameAdapter
import com.teamttdvlp.memolang.view.adapter.RCV_Generic_SimpleListAdapter
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class RetrofitAddFlashcardActivityModule {

  val animDuration = 200L

  val animInterpolator = DecelerateInterpolator()

  @Provides
  @Named("FlipClose")
  fun provideFlipCloseAnimator (activity: RetrofitAddFlashcardActivity) : Animation {
      return AnimationUtils.loadAnimation(activity, R.anim.from_1_to_0_y)
  }

  @Provides
  @Named("FlipOpen")
  fun provideFlipOpenAnimator (activity: RetrofitAddFlashcardActivity) : Animation {
      return AnimationUtils.loadAnimation(activity, R.anim.from_0_to_1_y)
  }

  @Provides
  @Named("HighlightTextAnim")
  fun provideHighlightTextAnim(activity: RetrofitAddFlashcardActivity): Animation {
      return AnimationUtils.loadAnimation(activity, R.anim.hightlight_text)
  }

  @Provides
  fun provideChooseLanguageAdapter (activity : RetrofitAddFlashcardActivity) : RCVChooseLanguageAdapter{
      return RCVChooseLanguageAdapter(activity)
  }

  @Provides
  fun provideRecentUsedLanguageAdapter (activity : RetrofitAddFlashcardActivity) : RCVRecentUsedLanguageAdapter{
      return RCVRecentUsedLanguageAdapter(activity)
  }

  @Provides
  fun provideRCVFlashcardSetNameAdapter (activity : RetrofitAddFlashcardActivity) : RCV_Generic_SimpleListAdapter<FlashcardSet> {
      return RCV_Generic_SimpleListAdapter<FlashcardSet>(activity, getTextFromItem = {
          it.name
      })
  }
}