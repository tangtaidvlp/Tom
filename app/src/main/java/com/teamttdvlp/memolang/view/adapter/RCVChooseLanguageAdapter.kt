package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.model.entity.Language
import com.teamttdvlp.memolang.model.entity.User
import com.teamttdvlp.memolang.database.sql.entity.user.UserConverter
import com.teamttdvlp.memolang.database.sql.repository.UserRepository
import com.teamttdvlp.memolang.view.helper.notContains
import com.teamttdvlp.memolang.view.helper.quickLog
import java.lang.Exception
import kotlin.collections.ArrayList

class RCVChooseLanguageAdapter (var context : Context) : RecyclerView.Adapter<RCVChooseLanguageAdapter.ViewHolder> () {

    private val MAX_ROW = 5

    private var onItemClickListener : OnItemClickListener? = null

    private var list = ArrayList<String>().apply {
        addAll(Language.languageList)
    }

    var assistant : Assistant? = null

    class ViewHolder (item : View): RecyclerView.ViewHolder(item) {
        var txt_language = item.findViewById<TextView>(R.id.txt_language)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_simple_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder : ViewHolder, position: Int) {
        val item = list[position]
        holder.txt_language.text = item
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(item)
        }
    }

    fun setOnItemClickListener (onItemClickListener: (item: String) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onClick(item: String) {
                assistant?.doExTask(item)
                onItemClickListener(item)
            }
        }
    }

    fun addLanguage (language : String) {
        if (list.notContains(language)) {
            if (list.size == MAX_ROW) {
                list.removeAt(0)
            }
            list.add(language)
            notifyDataSetChanged()
        }
    }

    fun setOnItemClickListener (onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setData (newData : ArrayList<String>) {
        list.clear()
        if (newData.size > MAX_ROW) {
            // Get MAX_ROW last items
            for (i in newData.size - MAX_ROW..newData.size - 1) {
                list.add(newData[i])
            }
        } else {
            list.addAll(newData)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onClick (item : String)
    }


    class UserSavingAssitant : Assistant () {

        companion object {
            val USER_REPOSITORY_POS = 0
        }

        override fun getAssistantCount(): Int {
            return 1
        }

        override fun doExTask (item : Any) {
            if (item is String) {
            val userRepository = assistantList.get(USER_REPOSITORY_POS) as UserRepository
            val saveUserRecentChosenLang : (String) -> Unit = { language ->
                User.getInstance().addLanguageToRecentUseList(language)
                userRepository.updateUser(UserConverter.toUserEntity(User.getInstance()))
            }
            saveUserRecentChosenLang.invoke(item)
        }}

    }

    abstract class Assistant {

        private val PLACE_HOLDER_VALUE = 0

        protected val assistantList = ArrayList<Any>()

        constructor() {
            createAssistantListVirtualSize()
        }

        abstract fun doExTask (item : Any)

        abstract fun getAssistantCount () : Int

        open fun addAssistant (position: Int, assistantObject: Any) {
            if ((position >= 0) and (position < assistantList.size)) {
                assistantList[position] = assistantObject
            } else throw AssistantNotValidException()
        }

        private fun createAssistantListVirtualSize () {
            for (i in 1..getAssistantCount()) {
                assistantList.add(PLACE_HOLDER_VALUE)
            }
        }
    }

    class AssistantNotValidException : Exception ("Assistant vocaList does not contain that languagePair of assistant. Check the position variable again")
}

