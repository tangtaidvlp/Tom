package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.room.Room
import com.teamttdvlp.memolang.data.sql.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.data.sql.MemoLangSqliteDataBase.Companion.DB_NAME
import com.teamttdvlp.memolang.model.repository.FlashcardRepos
import com.teamttdvlp.memolang.model.repository.UserRepos
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.view.activity.iview.ViewFlashcardSetView
import com.teamttdvlp.memolang.view.base.BaseViewModel

class ViewFlashcardSetViewModel(var flashcardSetRepos: FlashcardSetRepos) : BaseViewModel<ViewFlashcardSetView>() {


}
