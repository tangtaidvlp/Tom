package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.view.helper.goGONE
import com.teamttdvlp.memolang.view.helper.goVISIBLE
import com.teamttdvlp.memolang.view.helper.notContains
import com.teamttdvlp.memolang.view.helper.systemOutLogging

class RCVSimpleListAdapter (var context : Context) : RecyclerView.Adapter<RCVSimpleListAdapter.ViewHolder> () {

    private var onItemClickListener : OnItemClickListener? = null

    private var list = ArrayList<String>()

    var fullListCached = ArrayList<String>()

    class ViewHolder (item : View) : RecyclerView.ViewHolder(item) {
        var textView = item.findViewById<TextView>(R.id.txt_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_simple_text, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder : ViewHolder, position: Int) {
        val item = list[position]
        holder.textView.text = item
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(item)
        }
    }

    fun setData (list : ArrayList<String>) {
        this.list.clear()
        this.list.addAll(list)
        this.fullListCached.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener (onItemClickListener: (item: String) -> Unit) {
        this.onItemClickListener = object :
            OnItemClickListener {
            override fun onClick(item: String) {
                onItemClickListener(item)
            }
        }
    }

    fun filtType (type : String) : ArrayList<String> {
        val result = ArrayList<String>()
        if (type.isEmpty()) return result

        fullListCached.forEach { validType ->
            if (validType.toLowerCase().trim().startsWith(type.toLowerCase().trim())) {
                result.add(validType)
            }
        }
        this.list = result
        notifyDataSetChanged()
        return result
    }

    fun getTypeList () = fullListCached

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onClick (item : String)
    }

}

// The difference is that RCVSimpleListAdapter2's layout doesn't have
// marginStart and marginEnd
// So we can custom them by example padding in layout.xml
class RCVSimpleListAdapter2 (var context : Context) : RecyclerView.Adapter<RCVSimpleListAdapter2.ViewHolder> () {

    private var onItemClickListener : OnItemClickListener? = null

    private var list = ArrayList<String>()

    class ViewHolder (item : View) : RecyclerView.ViewHolder(item) {
        var txt_language = item.findViewById<TextView>(R.id.txt_language)
        var txt_line = item.findViewById<TextView>(R.id.line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_simple_text_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder : ViewHolder, position: Int) {
        val item = list[position]
        holder.txt_language.text = item
        if (position == list.size - 1) {
            holder.txt_line.goGONE()
        } else {
            holder.txt_line.goVISIBLE()
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(item)
        }
    }

    fun setData (data : ArrayList<String>) {
        this.list.clear()
        this.list.addAll(data)
        notifyDataSetChanged()
    }

    fun addToFirst (newSet : String) {
        if (list.notContains(newSet)) {
            this.list.add(0, newSet)
            notifyItemInserted(0)
        }
    }

    fun add (newSet : String) {
        if (list.notContains(newSet)) {
            this.list.add(newSet)
            notifyItemInserted(list.size - 1)
        }
    }

    fun setOnItemClickListener (onItemClickListener: (item: String) -> Unit) {
        this.onItemClickListener = object :
            OnItemClickListener {
            override fun onClick(item: String) {
                onItemClickListener(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onClick (item : String)
    }

}

class RCVGenericSimpleListAdapter2 <T> (var context : Context, private var getItem : (T) -> String) : RecyclerView.Adapter<RCVGenericSimpleListAdapter2.ViewHolder> () {

    private var onItemClickListener : OnItemClickListener<T>? = null

    private var list = ArrayList<T>()

    class ViewHolder (item : View) : RecyclerView.ViewHolder(item) {
        var txt_language = item.findViewById<TextView>(R.id.txt_language)
        var txt_line = item.findViewById<TextView>(R.id.line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_simple_text_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder : ViewHolder, position: Int) {
        val item = list[position]
        holder.txt_language.text = getItem(item)
        if (position == list.size - 1) {
            holder.txt_line.goGONE()
        } else {
            holder.txt_line.goVISIBLE()
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(item)
        }
    }

    fun setData (data : ArrayList<T>) {
        this.list.clear()
        this.list.addAll(data)
        notifyDataSetChanged()
    }

    fun addToFirst (newItem : T) {
        if (list.notContains(newItem)) {
            this.list.add(0, newItem)
            notifyItemInserted(0)
        }
    }

    fun add (newItem : T) {
        if (list.notContains(newItem)) {
            this.list.add(newItem)
            notifyItemInserted(list.size - 1)
        }
    }

    fun setOnItemClickListener (onItemClickListener: (item: T) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener<T> {
            override fun onClick(item: T) {
                onItemClickListener(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener<T> {
        fun onClick (item : T)
    }

}

class RCVSimpleListChooseSetNameAdapter (var context : Context) : RecyclerView.Adapter<RCVSimpleListChooseSetNameAdapter.ViewHolder> () {

    private var onItemClickListener : OnItemClickListener? = null

    private var flashcardSetList = ArrayList<Deck>()

    private var filtedFlashcardSetList = ArrayList<Deck>()

    class ViewHolder (item : View) : RecyclerView.ViewHolder(item) {
        var txtLanguage : TextView = item.findViewById<TextView>(R.id.txt_language)
        var txtLine : TextView = item.findViewById<TextView>(R.id.line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_simple_text_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder : ViewHolder, position: Int) {
        val flashcardSet = flashcardSetList[position]
        holder.txtLanguage.text = flashcardSet.name
        if (position == flashcardSetList.size - 1) {
            holder.txtLine.goGONE()
        } else {
            holder.txtLine.goVISIBLE()
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(flashcardSet)
        }
    }

    fun setData(data: ArrayList<Deck>) {
        this.flashcardSetList.clear()
        this.flashcardSetList.addAll(data)
        notifyDataSetChanged()
    }

    fun addToFirst(newSet: Deck) {
        systemOutLogging("ADD $newSet")
        if (flashcardSetList.notContains(newSet)) {
            this.flashcardSetList.add(0, newSet)
            notifyItemInserted(0)
        }
    }

    fun add(newSet: Deck) {
        if (flashcardSetList.notContains(newSet)) {
            this.flashcardSetList.add(newSet)
            notifyItemInserted(flashcardSetList.size - 1)
        }
    }

    fun filtFlashcardSetByLanguagePair (frontLanguage : String, backLanguage : String) {
        filtedFlashcardSetList = ArrayList<Deck>()
        for (set in flashcardSetList) {
            if ((set.frontLanguage == frontLanguage) and (set.backLanguage == backLanguage)) {
                filtedFlashcardSetList.add(set)
            }
        }
        setData(filtedFlashcardSetList)
    }

    fun setOnItemClickListener(onItemClickListener: (item: Deck) -> Unit) {
        this.onItemClickListener = object :
            OnItemClickListener {
            override fun onClick(item: Deck) {
                onItemClickListener(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return flashcardSetList.size
    }

    interface OnItemClickListener {
        fun onClick(item: Deck)
    }

}
