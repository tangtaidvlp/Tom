package com.teamttdvlp.memolang.viewmodel

import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.view.activity.iview.ViewFlashcardSetView
import com.teamttdvlp.memolang.view.base.BaseViewModel

class ViewFlashcardSetViewModel(var flashcardSetRepos: FlashcardSetRepos) :
    BaseViewModel<ViewFlashcardSetView>()
