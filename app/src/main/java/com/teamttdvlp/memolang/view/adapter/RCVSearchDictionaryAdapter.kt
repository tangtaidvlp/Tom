package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.NavigableRawVocabulary
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.RawVocabulary
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.databinding.ItemSearchDictionaryBinding
import com.teamttdvlp.memolang.model.EngVietVocabularyLoader
import com.teamttdvlp.memolang.view.helper.clearAll
import com.teamttdvlp.memolang.view.helper.systemOutLogging
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.abs

class RCVSearchDictionaryAdapter(var context : Context, var engVietVocabularyLoader : EngVietVocabularyLoader) : RecyclerView.Adapter<RCVSearchDictionaryAdapter.ViewHolder>() {

    private val DEFAULT = Color.BLACK

    var textColor: Int? = DEFAULT

    private var vocaList: ArrayList<RawVocabulary> = ArrayList()

    private var full_SpecifiedPrefix_VocaList: ArrayList<RawVocabulary> = ArrayList()

    private var onItemClickListener: OnItemClickListener? = null

    private var onBtnBringTextUpClickListener: OnItemClickListener? = null

    private var onGetFiltedResult: ((ArrayList<RawVocabulary>) -> Unit)? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding =
            ItemSearchDictionaryBinding.inflate(LayoutInflater.from(context), parent, false)
        if (textColor != null) {
            dataBinding.txtText.setTextColor(textColor!!)
        }
        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return vocaList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vocabulary = vocaList.get(position)
        holder.bind(vocabulary.key)
        if (vocabulary is NavigableRawVocabulary) {
            val targetVocabulary: TypicalRawVocabulary = getNavigatedRawVocabulary(vocabulary)
            holder.dataBinding.root.setOnClickListener {
                onItemClickListener?.onClick(targetVocabulary)
            }

            holder.dataBinding.btnBringTextUp.setOnClickListener {
                onBtnBringTextUpClickListener?.onClick(targetVocabulary)
            }
        } else {
            holder.dataBinding.root.setOnClickListener {
                onItemClickListener?.onClick(vocabulary)
            }

            holder.dataBinding.btnBringTextUp.setOnClickListener {
                onBtnBringTextUpClickListener?.onClick(vocabulary)
            }
        }
    }

    fun setData(data: ArrayList<RawVocabulary>) {
        passTwoFirstVocaToListener(data)
        vocaList.clear()
        vocaList.addAll(data)
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): RawVocabulary {
        return vocaList.get(position)
    }

    private fun getNavigatedRawVocabulary(navigableRawVocabulary: NavigableRawVocabulary): TypicalRawVocabulary {
        val targetKey = navigableRawVocabulary.targetVocaKey
        try {
            val targetRawVocabulary = full_SpecifiedPrefix_VocaList.first {
                it.key == targetKey
            } as TypicalRawVocabulary
            return targetRawVocabulary
        } catch (ex: Exception) {
            ex.printStackTrace()
            systemOutLogging("Error Word: $targetKey")
        }
        throw Exception()
    }

    fun setOnGetTwoFirstVocabulary(onGet_TwoFirstVoca: (ArrayList<RawVocabulary>) -> Unit) {
        this.onGetFiltedResult = onGet_TwoFirstVoca
    }

    fun addData(stringVocaItem: String) {
//        vocaList.add(toRawVocabulary(stringVocaItem))
        notifyItemInserted(0)
    }

    fun setOnItemClickListener(onItemClickListener: (item: RawVocabulary) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onClick(item: RawVocabulary) {
                onItemClickListener.invoke(item)
            }
        }
    }

    fun setOnBtnBringTextUpClickListener(onBtnBringTextUpClickListener: (item: RawVocabulary) -> Unit) {
        systemOutLogging("Setted")
        this.onBtnBringTextUpClickListener = object : OnItemClickListener {
            override fun onClick(item: RawVocabulary) {
                onBtnBringTextUpClickListener.invoke(item)
            }
        }
    }

    class ViewHolder(val dataBinding: ItemSearchDictionaryBinding) :
        RecyclerView.ViewHolder(dataBinding.root) {

        fun bind(item: String) {
            dataBinding.txtText.text = item
        }
    }

    interface OnItemClickListener {
        fun onClick(item: RawVocabulary)
    }

    data class VocaTextHolder (var id : Int = 0, var text : String = "")




    // SEARCH FUNCTION

    private var previousText = ""

    private var isNormalSearchMode = true

    private val cached_LoadedList_Map = HashMap<String, ArrayList<RawVocabulary>?>()
    private val MAX_ITEM_PER_SEARCH_TIMES = 25


      /**
      For text, if the text is "Done", we have 4 character, 'D' would give a list first
     Then 'o' will give a list which is filted from list of 'D', so it must be less or equal.
     similarly to 'n'. They have its own list, cached them make searching more quickly
      **/
    val cached_SearchingList_List = ArrayList<ArrayList<RawVocabulary>>()


    /**
     * Purpose of this function is that in Vietnamese
    When we type 'aa', TELEX input method would give us 'â'.
    So the text has changed but only in the way it form but not length
    while Searching Algothrim listens for text change in length listener
     **/
    private fun textChangeInLength(text: String): Boolean {
        systemOutLogging("----------------------------------------------")
        systemOutLogging("Prev: ${previousText}")
        systemOutLogging("Current: $text")
        return (text.length != previousText.length)
    }

    private fun trimAllSpacesPrefix(text: String): String {
        var result = text
        while (result.startsWith(" ")) {
            result = result.substring(1)
        }
        return result
    }
    private fun resetSearchMode () {
        isNormalSearchMode = true
    }

    fun search (text : String) {
        val cleanText = trimAllSpacesPrefix(text).toLowerCase()
        if (textChangeInLength(cleanText) or (cleanText == "w")) {
            val matchedList = getMatchedVocabularyList(cleanText)
            setData(matchedList)
        }

        if (text == "") {
            resetSearchMode()
        }
    }

    private fun passTwoFirstVocaToListener(matchedList: java.util.ArrayList<RawVocabulary>) {
        val twoFirst_VocaList = ArrayList<RawVocabulary>()
        if (matchedList.size >= 2) {
            twoFirst_VocaList.add(matchedList[0])
            twoFirst_VocaList.add(matchedList[1])
        } else { // matchedList.size is '0' or '1'
            for (voca in matchedList) {
                twoFirst_VocaList.add(voca)
            }
        }
        onGetFiltedResult?.invoke(twoFirst_VocaList)
    }

    fun getVocabularyByKey(key: String): TypicalRawVocabulary? {
        val trueFormKey = key.toLowerCase().trim()

        if (vocaList.size != 0) {
            val matchKey = vocaList.first().key.toLowerCase().trim() == trueFormKey
            if (matchKey) {
                return vocaList.first() as TypicalRawVocabulary
            }
        }


        val prefix = trueFormKey.get(0).toString()
        val corresPrefix_VocaList: ArrayList<RawVocabulary> =
            getCorresPrefixVocaList_AndCacheIt(prefix)
        for (rawVoca in corresPrefix_VocaList) {
            if (rawVoca.key == trueFormKey) {
                if (rawVoca is NavigableRawVocabulary) {
                    return getNavigatedRawVocabulary(rawVoca)
                } else {
                    return rawVoca as TypicalRawVocabulary
                }
            }
        }
        return null
    }


    private fun updatePreviousText (text : String) {
        previousText = text
    }

    private fun isUserWriting(text : String) : Boolean =  (text.length >= previousText.length)
    private fun textIsFirstChar (text : String) : Boolean =  text.length == 1
    private fun getEmptyList () : ArrayList<RawVocabulary> = ArrayList()
    private fun userClearAllText (text : String) : Boolean = text.isEmpty()

    private fun getMatchedVocabularyList (text : String) : ArrayList<RawVocabulary> {

        val add_Or_Delete_Or_EditAChar = abs(text.length - previousText.length) <= 1
        val userAddCharToLast = text.startsWith(previousText)
        val userDeleteLastChar = previousText.startsWith(text)

        val userEditTextNormally =
            add_Or_Delete_Or_EditAChar    and    (userAddCharToLast  or  userDeleteLastChar)    and    isNormalSearchMode

        if (userEditTextNormally) {
            val userIsWriting = isUserWriting(text)
            val userIsDeleting = userIsWriting.not()
            updatePreviousText(text)

            if (userIsWriting) {
                if (textIsFirstChar(text)) {
                    val prefix = text
                    val corresPrefixList : ArrayList<RawVocabulary> = getCorresPrefixVocaList_AndCacheIt(prefix)
                    systemOutLogging(corresPrefixList.first().key)
                    proceedCacheSearchingLists_List(corresPrefixList)
                    return corresPrefixList
                } else { // Text is not first character
                    val userAddCharToTextTail = text.startsWith(previousText)
                    if (userAddCharToTextTail) {
                        val previousCachedListPos = text.length - 2
                        val previousList = cached_SearchingList_List.get(previousCachedListPos)
                        val tailOfTextAreSpace = text.endsWith(" ")

                        if (tailOfTextAreSpace) {
                            proceedCacheSearchingLists_List(previousList)
                            return previousList
                        }

                        val newFiltedList = getFiltedListWithLimitedSize(previousList, text, MAX_ITEM_PER_SEARCH_TIMES)
                        proceedCacheSearchingLists_List(newFiltedList)
                        return newFiltedList
                    }
                }
            }
            else if (userIsDeleting) {
                removeCachingCorrespondToTextLength(text)
                if (userClearAllText(text)) {
                    return getEmptyList()
                } else {
                    val previousCachedList = cached_SearchingList_List[text.length - 1]
                    return previousCachedList
                }
            }
        }
        else { // User copy, cut or paste or insert/delete in the mid of the text
            isNormalSearchMode = false
            cached_SearchingList_List.clear()
            updatePreviousText(text)

            if (text.isEmpty()) {
                return getEmptyList()
            }

            val prefix = text.get(0).toString()
            val corresPrefixList : ArrayList<RawVocabulary> = getCorresPrefixVocaList_AndCacheIt(prefix)
            return getFiltedListWithLimitedSize(corresPrefixList, text, MAX_ITEM_PER_SEARCH_TIMES)
        }

        throw Exception("Impossible error happened. RCVSearchDictionaryAdapter.kt")

    }

    data class TextAndPronun(var text : String, var pronun: String)

    private fun check_corresPrefixList_WasNotLoadedBefore (prefix : String) : Boolean {
        return (cached_LoadedList_Map.get(prefix) == null)
    }

    private fun proceedCache_LoadedRawList (prefix : String, corresPrefixList : ArrayList<RawVocabulary>) {
        cached_LoadedList_Map[prefix] = corresPrefixList
    }

    private fun proceedCacheSearchingLists_List (list : ArrayList<RawVocabulary>) {
        cached_SearchingList_List.add(list)
    }

    private fun getFiltedListWithLimitedSize (vocabularyList : ArrayList<RawVocabulary>, text : String, limitAmount : Int) : ArrayList<RawVocabulary>{
        val result = ArrayList<RawVocabulary>()
        for (voca in vocabularyList) {
//            if (result.size >= limitAmount) {
//                break
//            }
            if (voca.key.startsWith(text)) {
                result.add(voca)
            }
        }
        return result
    }

    private fun removeCachingCorrespondToTextLength (text : String) {
        cached_SearchingList_List.removeAt(text.length)
    }

    private fun getCorresPrefixVocaList_AndCacheIt (prefix : String) : ArrayList<RawVocabulary> {
        val corresPrefix_RawVocaList : ArrayList<RawVocabulary>
        if (check_corresPrefixList_WasNotLoadedBefore(prefix)) {
            val result = engVietVocabularyLoader.getOfflineVocaFromRawFile_ByPrefix(prefix)
            if (result != null) {
                corresPrefix_RawVocaList = result
            } else {
                corresPrefix_RawVocaList = ArrayList()
            }
            proceedCache_LoadedRawList(prefix, corresPrefix_RawVocaList)
        } else {
            corresPrefix_RawVocaList = cached_LoadedList_Map.get(prefix)!!
        }
        full_SpecifiedPrefix_VocaList.addAll(corresPrefix_RawVocaList)
        return corresPrefix_RawVocaList
    }

}

