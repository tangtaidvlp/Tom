package com.teamttdvlp.memolang.view.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.Navigation
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.model.sqlite.entity.MemoCardEntity
import com.teamttdvlp.memolang.view.Activity.helper.TestEverything
import com.teamttdvlp.memolang.view.Activity.helper.quickLog
import com.teamttdvlp.memolang.view.Activity.helper.quickStartActivity
import com.teamttdvlp.memolang.view.Activity.mockmodel.FlashcardSet
import com.teamttdvlp.memolang.view.Activity.mockmodel.MemoCard
import com.teamttdvlp.memolang.view.Activity.viewmodel.reusable.OnlineFlashcardDBManager
import dagger.android.AndroidInjection
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mockList = ArrayList<FlashcardSet>()
        var set1 = FlashcardSet("English", "Vietnamese", TestEverything().mockList())
        var set2 = FlashcardSet("Vietnamese", "English", TestEverything().mockList())
        var card = MemoCard("Daddy", "Cha", "en-vi", "Daddy is guy who helps us to be born", "Papa, Dad", "Noun")
        quickStartActivity(AuthActivity::class.java)

    }
}
