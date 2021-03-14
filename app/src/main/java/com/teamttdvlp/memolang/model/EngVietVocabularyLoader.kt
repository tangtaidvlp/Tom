package com.teamttdvlp.memolang.model

import android.content.Context
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.NavigableRawVocabulary
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.RawVocabulary
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.view.helper.clearAll
import com.teamttdvlp.memolang.view.helper.systemOutLogging
import java.io.BufferedReader
import java.io.InputStreamReader

class EngVietVocabularyLoader (private var context : Context) {

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
                systemOutLogging("Unknown prefix $prefixChar")
                null
            }
        }
    }

    private val NAVIGABLE_KEY = 0
    private val TARGET_VOCA = 1

    fun getOfflineVocaFromRawFile_ByPrefix (prefix : String) : ArrayList<RawVocabulary> {
        try {
            val inputStream = context.resources.openRawResource(getResourceFileBaseOnPrefix(prefix)!!)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferInputStream = BufferedReader(inputStreamReader)
            var text : String? = null

            var typicalRawVoca_Holder : TypicalRawVocabulary? = null
            val result = ArrayList<RawVocabulary>()
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


}