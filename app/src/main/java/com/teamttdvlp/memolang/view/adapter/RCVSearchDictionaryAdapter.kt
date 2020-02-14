package com.teamttdvlp.memolang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ItemSearchDictionaryBinding
import com.teamttdvlp.memolang.view.helper.capitalizeFirstLetter
import com.teamttdvlp.memolang.view.helper.not
import com.teamttdvlp.memolang.viewmodel.NAVIGATE_TAG
import com.teamttdvlp.memolang.viewmodel.PARTS_DEVIDER
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

class RCVSearchDictionaryAdapter(var context : Context) : RecyclerView.Adapter<RCVSearchDictionaryAdapter.ViewHolder>() {

    private var vocabularyList : ArrayList<String> = ArrayList()

    var vocaContentList : ArrayList<String> = ArrayList()
    private set

    private var onItemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RCVSearchDictionaryAdapter.ViewHolder {
        val dataBinding = ItemSearchDictionaryBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return vocabularyList.size
    }

    override fun onBindViewHolder(holder: RCVSearchDictionaryAdapter.ViewHolder, position: Int) {
        val vocabulary : String = vocabularyList[position]
        val content : String = vocaContentList.get(position)
        holder.bind(vocabulary)
        val voca_is_A_NavigatableWord = content.contains(NAVIGATE_TAG)
        if (voca_is_A_NavigatableWord) {
            // The word which is navigated
            val navigatedWord = content.substring(NAVIGATE_TAG.length)
            val navigatedWordPos = vocabularyList.indexOf(navigatedWord)
            val navigatedWordContent : String = navigatedWord + PARTS_DEVIDER + vocaContentList[navigatedWordPos]
            holder.dataBinding.root.setOnClickListener {
                onItemClickListener?.onClick(navigatedWord, navigatedWordContent, vocabulary)
            }
        } else {
            val content = "$vocabulary$PARTS_DEVIDER ${vocaContentList[position]}"
            holder.dataBinding.root.setOnClickListener {
                onItemClickListener?.onClick(vocabulary, content, null)
            }
        }
    }

    fun setData (inputList : ArrayList<String>) {
        vocabularyList.clear()
        vocaContentList.clear()
        for (i in 0..inputList.size - 1) {
            val deviderTagPos = inputList[i].indexOf(PARTS_DEVIDER)
            vocabularyList.add(inputList[i].substring(0, deviderTagPos))
            vocaContentList.add(inputList[i].substring(deviderTagPos + PARTS_DEVIDER.length))
        }
        notifyDataSetChanged()
    }

    fun addData (item : String) {
        var deviderTagPos = item.indexOf(PARTS_DEVIDER)
        vocabularyList.add(0, item.substring(0, deviderTagPos))
        vocaContentList.add(0, item.substring(deviderTagPos + PARTS_DEVIDER.length))
        notifyItemInserted(0)
    }

    fun setOnItemClickListener(onItemClickListener : (vocabulary : String, content : String, navigatableKey : String?) -> Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onClick(key: String, content: String, navigatableKey : String?) {
                onItemClickListener.invoke(key, content, navigatableKey)
            }
        }
    }

    class ViewHolder (val dataBinding : ItemSearchDictionaryBinding): RecyclerView.ViewHolder (dataBinding.root) {

        fun bind(item : String) {
            dataBinding.txtText.text = item
        }
    }
    interface OnItemClickListener {
        fun onClick (key : String, content : String, navigatableKey : String? = null)
    }

    data class VocaTextHolder (var id : Int = 0, var text : String = "")


    // SEARCHING CODE (BADE CODE :(((, I tried my best, it will be refactored later)

    var previousTextSize = 0

    val cached_LoadedList_Map = HashMap<String, ArrayList<String>?>()

    // For text, if the text is "Done", we have 4 character, 'D' would give a list first
    // Then 'o' will give a list which is filted from list of 'D', so it must be less or equal.
    // similarly to 'n'. They have its own list, cached them make searching more quickly
    val cached_SearchingList_List = ArrayList<ArrayList<String>>()

    fun search (text : String) {
        val cleanText = trimAllSpacesPrefix(text).toLowerCase()
        if (textChangeInLength(cleanText) or (cleanText == "w")) {
            val result = searchVocabulary(cleanText.capitalizeFirstLetter())
            setData(result)
        }
    }

    // Purpose of this function is that in Vietnamese
    // When we type 'aa', TELEX input method would give us 'â'.
    // So the text has changed but only in the way it form but not length
    // while Searching Algothrim listens for text change in length listener
    fun textChangeInLength (text : String) : Boolean {
        return (text.length != previousTextSize)
    }

    fun trimAllSpacesPrefix (text : String) : String {
        var result = text
        while (result.startsWith(" ")) {
            result = result.substring(1)
        }
        return result
    }

    private fun updatePreviousTextSize (text : String) {
        previousTextSize = text.length
    }

    private fun isUserWriting(text : String) : Boolean {
        return (text.length >= previousTextSize)
    }

    private fun textIsFirstChar (text : String) : Boolean {
        return text.length == 1
    }

    private fun check_corresPrefixList_WasNotLoadedBefore (prefix : String) : Boolean {
        return (cached_LoadedList_Map.get(prefix) == null)
    }

    private fun proceedToCacheLoadedList (prefix : String, corresPrefixList : ArrayList<String>) {
        cached_SearchingList_List.add(corresPrefixList)
        cached_LoadedList_Map[prefix] = corresPrefixList
    }

    private fun proceedToCacheSearchingLists_List (list : ArrayList<String>) {
        cached_SearchingList_List.add(list)
    }

    private fun getFiltedList (vocabularyList : ArrayList<String>, text : String) : ArrayList<String>{
        return vocabularyList.filter { vocabulary ->
            vocabulary.startsWith(text)
        } as ArrayList<String>
    }

    private fun giveAEmptyList () : ArrayList<String> = ArrayList()

    private fun removeCachingCorrespondToTextLength (text : String) {
        cached_SearchingList_List.removeAt(text.length)
    }

    private fun userClearAllText (text : String) : Boolean {
        return text.length == 0
    }

    private fun searchVocabulary (text : String) : ArrayList<String> {
        val userIsWriting = isUserWriting(text)
        val userIsDeleting = not(userIsWriting)
        updatePreviousTextSize(text)

        if (userIsWriting) {
            if (textIsFirstChar(text)) {
                val prefix = text
                if (check_corresPrefixList_WasNotLoadedBefore(prefix)) {
                    val corresPrefixList = getOfflineVocabularyFromRawFile(prefix)
                    proceedToCacheLoadedList(prefix, corresPrefixList)
                    return corresPrefixList
                } else {
                    val corresPrefixList = cached_LoadedList_Map.get(prefix)
                    proceedToCacheSearchingLists_List(corresPrefixList!!)
                    return corresPrefixList
                }
            }
            else {
                val previousCachedListPos = text.length - 2
                val previousList = cached_SearchingList_List.get(previousCachedListPos)
                val tailOfTextAreSpace = text.endsWith(" ")

                if (tailOfTextAreSpace) {
                    proceedToCacheSearchingLists_List(previousList)
                    return previousList
                }

                val newFiltedList = getFiltedList(previousList, text)
                proceedToCacheSearchingLists_List(newFiltedList)
                return newFiltedList
            }
        }
        else if (userIsDeleting) {
            removeCachingCorrespondToTextLength(text)
            if (userClearAllText(text)) {
                return giveAEmptyList()
            } else {
                val previousCachedList = cached_SearchingList_List[text.length - 1]
                return previousCachedList
            }
        }

        throw Exception("Impossible error happened. RCVSearchDictionaryAdapter.kt")
    }


    fun getOfflineVocabularyFromRawFile (prefixChar : String) : ArrayList<String> {

        val matchedResource = getResourceFileBaseOnPrefix (prefixChar)
        val result = ArrayList<String>()
        if (matchedResource == null) return ArrayList()
        try {
            val inputStream = context.resources.openRawResource(matchedResource!!)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferInputStream = BufferedReader(inputStreamReader)
            var line : String? = null
            while ({line = bufferInputStream.readLine(); line != null} ()) {
                if (line!!.length == 0) continue
                result.add(line!!)
            }
            inputStreamReader.close()
            inputStream.close()
            bufferInputStream.close()

            return result
        } catch (ex : Exception) {
            ex.printStackTrace()
            return giveAEmptyList()
        }
    }

    private fun getResourceFileBaseOnPrefix(prefixChar: String): Int? {
        return when (prefixChar) {
            "A" -> R.raw.a_prefix_vocabulary
            "B" -> R.raw.b_prefix_vocabulary
            "C" -> R.raw.c_prefix_vocabulary
            "D" -> R.raw.d_prefix_vocabulary
            "E" -> R.raw.e_prefix_vocabulary
            "F" -> R.raw.f_prefix_vocabulary
            "G" -> R.raw.g_prefix_vocabulary
            "H" -> R.raw.h_prefix_vocabulary
            "I" -> R.raw.i_prefix_vocabulary
            "J" -> R.raw.j_prefix_vocabulary
            "K" -> R.raw.k_prefix_vocabulary
            "L" -> R.raw.l_prefix_vocabulary
            "M" -> R.raw.m_prefix_vocabulary
            "N" -> R.raw.n_prefix_vocabulary
            "O" -> R.raw.o_prefix_vocabulary
            "P" -> R.raw.p_prefix_vocabulary
            "Q" -> R.raw.q_prefix_vocabulary
            "R" -> R.raw.r_prefix_vocabulary
            "S" -> R.raw.s_prefix_vocabulary
            "T" -> R.raw.t_prefix_vocabulary
            "U" -> R.raw.u_prefix_vocabulary
            "V" -> R.raw.v_prefix_vocabulary
            "W" -> R.raw.w_prefix_vocabulary
            "X" -> R.raw.x_prefix_vocabulary
            "Y" -> R.raw.y_prefix_vocabulary
            "Z" -> R.raw.z_prefix_vocabulary
            else -> null
        }
    }


}

