package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.NavigableRawVocabulary
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.RawVocabulary
import com.teamttdvlp.memolang.databinding.ItemSearchDictionaryBinding
import com.teamttdvlp.memolang.view.helper.capitalizeFirstLetter
import com.teamttdvlp.memolang.view.helper.clearAll
import com.teamttdvlp.memolang.view.helper.not
import com.teamttdvlp.memolang.view.helper.quickLog
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import kotlin.math.abs

class RCVSearchDictionaryAdapter(var context : Context) : RecyclerView.Adapter<RCVSearchDictionaryAdapter.ViewHolder>() {

    private val DEFAULT = null

    var textColor : Int? = DEFAULT

    private var vocaList : ArrayList<RawVocabulary> = ArrayList()

    private var full_SpecifiedPrefix_VocaList : ArrayList<RawVocabulary> = ArrayList()

    private var onItemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding = ItemSearchDictionaryBinding.inflate(LayoutInflater.from(context), parent, false)
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
            val targetVocabulary : TypicalRawVocabulary = getNavigatedRawVocabulary(vocabulary)
            holder.dataBinding.root.setOnClickListener {
                onItemClickListener?.onClick(targetVocabulary)
            }
        } else {
            holder.dataBinding.root.setOnClickListener {
                onItemClickListener?.onClick(vocabulary)
            }
        }
    }

    fun setData (data : ArrayList<RawVocabulary>) {
        vocaList.clear()
        vocaList.addAll(data)
        notifyDataSetChanged()
    }

    private fun getNavigatedRawVocabulary(navigableRawVocabulary: NavigableRawVocabulary): TypicalRawVocabulary {
        val targetKey = navigableRawVocabulary.targetVocaKey
        try {
            val targetRawVocabulary = full_SpecifiedPrefix_VocaList.first {
                it.key == targetKey
            } as TypicalRawVocabulary
            return targetRawVocabulary
        } catch (ex : Exception) {
            ex.printStackTrace()
            quickLog("Error Word: $targetKey")
        }
        throw Exception()
    }


    fun addData (stringVocaItem : String) {
//        vocaList.add(toRawVocabulary(stringVocaItem))
        notifyItemInserted(0)
    }

    fun setOnItemClickListener(onItemClickListener : (item : RawVocabulary) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onClick(item : RawVocabulary) {
                onItemClickListener.invoke(item)
            }
        }
    }

    class ViewHolder (val dataBinding : ItemSearchDictionaryBinding): RecyclerView.ViewHolder (dataBinding.root) {

        fun bind(item : String) {
            dataBinding.txtText.text = item
        }
    }

    interface OnItemClickListener {
        fun onClick (item : RawVocabulary)
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
    private fun textChangeInLength (text : String) : Boolean = (text.length != previousText.length)
    private fun trimAllSpacesPrefix (text : String) : String {
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
            val resultList = getMatchedVocabularyList(cleanText)
            setData(resultList)
        }

        if (text == "") {
            resetSearchMode()
        }
    }

    fun getVocabularyByKey (key : String) : TypicalRawVocabulary? {
        val trueFormKey = key.toLowerCase().trim()
        val prefix = trueFormKey.get(0).toString()
        val corresPrefix_VocaList : ArrayList<RawVocabulary> = getCorresPrefixVocaList_AndCacheIt(prefix)
        for (rawVoca in corresPrefix_VocaList) {
            quickLog("Gotten voca: $rawVoca")
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
            val userIsDeleting = not(userIsWriting)
            updatePreviousText(text)

            if (userIsWriting) {
                if (textIsFirstChar(text)) {
                    val prefix = text
                    val corresPrefixList : ArrayList<RawVocabulary> = getCorresPrefixVocaList_AndCacheIt(prefix)
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

//    private fun getOfflineVocaFromRawFile_ByPrefix (prefixChar : String) : ArrayList<String> {
//        val matchedResource = getResourceFileBaseOnPrefix (prefixChar)
//        val result = ArrayList<String>()
//        if (matchedResource == null) return ArrayList()
//        try {
//            val inputStream = context.resources.openRawResource(matchedResource!!)
//            val inputStreamReader = InputStreamReader(inputStream)
//            val bufferInputStream = BufferedReader(inputStreamReader)
//            var line : String? = null
//            while ({line = bufferInputStream.readLine(); line != null} ()) {
//                if (line!!.length == 0) continue
//                result.add(line!!)
//            }
//            inputStreamReader.close()
//            inputStream.close()
//            bufferInputStream.close()
//
//            return result
//        } catch (ex : Exception) {
//            ex.printStackTrace()
//            return ArrayList<String>()
//        }
//    }

    private val NAVIGABLE_KEY = 0
    private val TARGET_VOCA = 1

    private fun getOfflineVocaFromRawFile_ByPrefix (prefix : String) : ArrayList<RawVocabulary> {
        try {
            val inputStream = context.resources.openRawResource(getResourceFileBaseOnPrefix(prefix)!!)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferInputStream = BufferedReader(inputStreamReader)
            var text : String? = null

            var typicalRawVoca_Holder : TypicalRawVocabulary? = null
            var result = ArrayList<RawVocabulary>()
            while ({text = bufferInputStream.readLine(); text != null} ()) {

                if (text!!.isEmpty()) {
                    continue
                }

                if (text!!.startsWith("@") || ((text!!.length > 1) && (text!![1] == '@'))) {


                    if (text!!.contains("->")) {
                        val parts = text!!.split("->")
                        result.add(NavigableRawVocabulary(parts[NAVIGABLE_KEY], parts[TARGET_VOCA]))
                        continue
                    }

                    if (typicalRawVoca_Holder != null) {
                        result.add(typicalRawVoca_Holder.deepClone())
                    }

                    val deviderPos = text!!.indexOf(" /")
                    if (deviderPos != -1) {
                        typicalRawVoca_Holder = TypicalRawVocabulary(text!!.substring(0, deviderPos).clearAll("@").trim())
                    } else {
                        typicalRawVoca_Holder = TypicalRawVocabulary(text!!.trim().clearAll("@"))
                    }
                }

                typicalRawVoca_Holder!!.fullContent.add(text!!)
            }

            inputStreamReader.close()
            inputStream.close()
            bufferInputStream.close()

            return result
        } catch (ex : Exception) {
            ex.printStackTrace()
            return ArrayList()
        }

    }

    data class TextAndPronun(var text : String, var pronun: String)

    private fun getResourceFileBaseOnPrefix(prefixChar: String): Int? {
        return when (prefixChar) {
            "A"  -> R.raw.a_prefix_vocabulary
            "a"  -> R.raw.a_prefix_vocabulary
            "b"  -> R.raw.b_prefix_vocabulary
            "B"  -> R.raw.b_prefix_vocabulary
            "c"  -> R.raw.c_prefix_vocabulary
            "C"  -> R.raw.c_prefix_vocabulary
            "d"  -> R.raw.d_prefix_vocabulary
            "D"  -> R.raw.d_prefix_vocabulary
            "e"  -> R.raw.e_prefix_vocabulary
            "E"  -> R.raw.e_prefix_vocabulary
            "f"  -> R.raw.f_prefix_vocabulary
            "F"  -> R.raw.f_prefix_vocabulary
            "g"  -> R.raw.g_prefix_vocabulary
            "G"  -> R.raw.g_prefix_vocabulary
            "h"  -> R.raw.h_prefix_vocabulary
            "H"  -> R.raw.h_prefix_vocabulary
            "i"  -> R.raw.i_prefix_vocabulary
            "I"  -> R.raw.i_prefix_vocabulary
            "j"  -> R.raw.j_prefix_vocabulary
            "J"  -> R.raw.j_prefix_vocabulary
            "k"  -> R.raw.k_prefix_vocabulary
            "K"  -> R.raw.k_prefix_vocabulary
            "l"  -> R.raw.l_prefix_vocabulary
            "L"  -> R.raw.l_prefix_vocabulary
            "m"  -> R.raw.m_prefix_vocabulary
            "M"  -> R.raw.m_prefix_vocabulary
            "n"  -> R.raw.n_prefix_vocabulary
            "N"  -> R.raw.n_prefix_vocabulary
            "o"  -> R.raw.o_prefix_vocabulary
            "O"  -> R.raw.o_prefix_vocabulary
            "p"  -> R.raw.p_prefix_vocabulary
            "P"  -> R.raw.p_prefix_vocabulary
            "q"  -> R.raw.q_prefix_vocabulary
            "Q"  -> R.raw.q_prefix_vocabulary
            "r"  -> R.raw.r_prefix_vocabulary
            "R"  -> R.raw.r_prefix_vocabulary
            "s"  -> R.raw.s_prefix_vocabulary
            "S"  -> R.raw.s_prefix_vocabulary
            "t"  -> R.raw.t_prefix_vocabulary
            "T"  -> R.raw.t_prefix_vocabulary
            "u"  -> R.raw.u_prefix_vocabulary
            "U"  -> R.raw.u_prefix_vocabulary
            "v"  -> R.raw.v_prefix_vocabulary
            "V"  -> R.raw.v_prefix_vocabulary
            "w"  -> R.raw.w_prefix_vocabulary
            "W"  -> R.raw.w_prefix_vocabulary
            "x"  -> R.raw.x_prefix_vocabulary
            "X"  -> R.raw.x_prefix_vocabulary
            "y"  -> R.raw.y_prefix_vocabulary
            "Y"  -> R.raw.y_prefix_vocabulary
            "z"  -> R.raw.z_prefix_vocabulary
            "Z"  -> R.raw.z_prefix_vocabulary
            else -> {
                quickLog("Unknown prefix $prefixChar")
                null
            }
        }
    }

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
            corresPrefix_RawVocaList = getOfflineVocaFromRawFile_ByPrefix(prefix)
            proceedCache_LoadedRawList(prefix, corresPrefix_RawVocaList)
        } else {
            corresPrefix_RawVocaList = cached_LoadedList_Map.get(prefix)!!
        }
        full_SpecifiedPrefix_VocaList.addAll(corresPrefix_RawVocaList)
        return corresPrefix_RawVocaList
    }

}

