package com.teamttdvlp.memolang.model.model

import java.util.ArrayList

class Language {



    companion object {

        val AFRIKAANS = "af"
        val ALBANIAN = "sq"
        val ARABIC = "ar"
        val ARMENIAN = "hy"
        val AZERBAIJANI = "az"
        val BASQUE = "eu"
        val BELARUSIAN = "be"
        val BENGALI = "bn"
        val BULGARIAN = "bg"
        val CATALAN = "ca"
        val CHINESE = "zh-CN"
        val CROATIAN = "hr"
        val CZECH = "cs"
        val DANISH = "da"
        val DUTCH = "nl"
        val ENGLISH = "en"
        val ESTONIAN = "et"
        val FILIPINO = "tl"
        val FINNISH = "fi"
        val FRENCH = "fr"
        val GALICIAN = "gl"
        val GEORGIAN = "ka"
        val GERMAN = "de"
        val GREEK = "el"
        val GUJARATI = "gu"
        val HAITIAN_CREOLE = "ht"
        val HEBREW = "iw"
        val HINDI = "hi"
        val HUNGARIAN = "hu"
        val ICELANDIC = "is"
        val INDONESIAN = "id"
        val IRISH = "ga"
        val ITALIAN = "it"
        val JAPANESE = "ja"
        val KANNADA = "kn"
        val KOREAN = "ko"
        val LATIN = "la"
        val LATVIAN = "lv"
        val LITHUANIAN = "lt"
        val MACEDONIAN = "mk"
        val MALAY = "ms"
        val MALTESE = "mt"
        val NORWEGIAN = "no"
        val PERSIAN = "fa"
        val POLISH = "pl"
        val PORTUGUESE = "pt"
        val ROMANIAN = "ro"
        val RUSSIAN = "ru"
        val SERBIAN = "sr"
        val SLOVAK = "sk"
        val SLOVENIAN = "sl"
        val SPANISH = "es"
        val SWAHILI = "sw"
        val SWEDISH = "sv"
        val TAMIL = "ta"
        val TELUGU = "te"
        val THAI = "th"
        val TURKISH = "tr"
        val UKRAINIAN = "uk"
        val URDU = "ur"
        val VIETNAMESE = "vi"
        val WELSH = "cy"
        val YIDDISH = "yi"
        val CHINESE_SIMPLIFIED = "zh-CN"
        val CHINESE_TRADITIONAL = "zh-TW"


        private val AFRIKAANS_VALUE = "Afrikaans"
        private val ALBANIAN_VALUE = "Albanian"
        private val ARABIC_VALUE = "Arabic"
        private val ARMENIAN_VALUE = "Armenian"
        private val AZERBAIJANI_VALUE = "Azerbaijani"
        private val BASQUE_VALUE = "Basque"
        private val BELARUSIAN_VALUE = "Belarusian"
        private val BENGALI_VALUE = "Bengali"
        private val BULGARIAN_VALUE = "Bulgarian"
        private val CATALAN_VALUE = "Catalan"
        private val CHINESE_VALUE = "Chinese"
        private val CROATIAN_VALUE = "Croatian"
        private val CZECH_VALUE = "Czech"
        private val DANISH_VALUE = "Danish"
        private val DUTCH_VALUE = "Dutch"
        private val ENGLISH_VALUE = "English"
        private val ESTONIAN_VALUE = "Estonian"
        private val FILIPINO_VALUE = "Filipino"
        private val FINNISH_VALUE = "Finnish"
        private val FRENCH_VALUE = "French"
        private val GALICIAN_VALUE = "Galician"
        private val  GEORGIAN_VALUE = "Georgian"
        private val GERMAN_VALUE = "German"
        private val GREEK_VALUE = "Greek"
        private val GUJARATI_VALUE = "Gujarati"
        private val HAITIAN_CREOLE_VALUE = "Haitian Creole"
        private val HEBREW_VALUE = "Hebrew"
        private val HINDI_VALUE = "Hindi"
        private val HUNGARIAN_VALUE = "Hungarian"
        private val ICELANDIC_VALUE = "Icelandic"
        private val INDONESIAN_VALUE = "Indonesian"
        private val IRISH_VALUE = "Irish"
        private val ITALIAN_VALUE = "Italian"
        private val JAPANESE_VALUE = "Japanese"
        private val KANNADA_VALUE = "Kannada"
        private val KOREAN_VALUE = "Korean"
        private val LATIN_VALUE = "Latin"
        private val LATVIAN_VALUE = "Latvian"
        private val LITHUANIAN_VALUE = "Lithuanian"
        private val MACEDONIAN_VALUE = "Macedonian"
        private val MALAY_VALUE = "Malay"
        private val MALTESE_VALUE = "Maltese"
        private val NORWEGIAN_VALUE = "Norwegian"
        private val PERSIAN_VALUE = "Persian"
        private val POLISH_VALUE = "Polish"
        private val PORTUGUESE_VALUE = "Portuguese"
        private val ROMANIAN_VALUE = "Romanian"
        private val RUSSIAN_VALUE = "Russian"
        private val SERBIAN_VALUE = "Serbian"
        private val SLOVAK_VALUE = "Slovak"
        private val SLOVENIAN_VALUE = "Slovenian"
        private val SPANISH_VALUE = "Spanish"
        private val SWAHILI_VALUE = "Swahili"
        private val SWEDISH_VALUE = "Swedish"
        private val TAMIL_VALUE = "Tamil"
        private val TELUGU_VALUE = "Telugu"
        private val THAI_VALUE = "Thai"
        private val TURKISH_VALUE = "Turkish"
        private val UKRAINIAN_VALUE = "Ukrainian"
        private val URDU_VALUE = "Urdu"
        private val VIETNAMESE_VALUE = "Vietnamese"
        private val WELSH_VALUE = "Welsh"
        private val YIDDISH_VALUE = "Yiddish"
        private val CHINESE_SIMPLIFIED_VALUE = "Simplified Chinese"
        private val CHINESE_TRADITIONAL_VALUE = "Traditional Chinese"

        private val languageMap = HashMap<String, String>()

        val languageList : ArrayList<String> = ArrayList()

        init {
            languageMap[AFRIKAANS] =
                AFRIKAANS_VALUE
            languageMap[ALBANIAN] =
                ALBANIAN_VALUE
            languageMap[ARABIC] =
                ARABIC_VALUE
            languageMap[ARMENIAN] =
                ARMENIAN_VALUE
            languageMap[AZERBAIJANI] =
                AZERBAIJANI_VALUE
            languageMap[BASQUE] =
                BASQUE_VALUE
            languageMap[BELARUSIAN] =
                BELARUSIAN_VALUE
            languageMap[BENGALI] =
                BENGALI_VALUE
            languageMap[BULGARIAN] =
                BULGARIAN_VALUE
            languageMap[CATALAN] =
                CATALAN_VALUE
            languageMap[CHINESE] =
                CHINESE_VALUE
            languageMap[CROATIAN] =
                CROATIAN_VALUE
            languageMap[CZECH] =
                CZECH_VALUE
            languageMap[DANISH] =
                DANISH_VALUE
            languageMap[DUTCH] =
                DUTCH_VALUE
            languageMap[ENGLISH] =
                ENGLISH_VALUE
            languageMap[ESTONIAN] =
                ESTONIAN_VALUE
            languageMap[FILIPINO] =
                FILIPINO_VALUE
            languageMap[FINNISH] =
                FINNISH_VALUE
            languageMap[FRENCH] =
                FRENCH_VALUE
            languageMap[GALICIAN] =
                GALICIAN_VALUE
            languageMap[GEORGIAN] =
                GEORGIAN_VALUE
            languageMap[GERMAN] =
                GERMAN_VALUE
            languageMap[GREEK] =
                GREEK_VALUE
            languageMap[GUJARATI] =
                GUJARATI_VALUE
            languageMap[HAITIAN_CREOLE] =
                HAITIAN_CREOLE_VALUE
            languageMap[HEBREW] =
                HEBREW_VALUE
            languageMap[HINDI] =
                HINDI_VALUE
            languageMap[HUNGARIAN] =
                HUNGARIAN_VALUE
            languageMap[ICELANDIC] =
                ICELANDIC_VALUE
            languageMap[INDONESIAN] =
                INDONESIAN_VALUE
            languageMap[IRISH] =
                IRISH_VALUE
            languageMap[ITALIAN] =
                ITALIAN_VALUE
            languageMap[JAPANESE] =
                JAPANESE_VALUE
            languageMap[KANNADA] =
                KANNADA_VALUE
            languageMap[KOREAN] =
                KOREAN_VALUE
            languageMap[LATIN] =
                LATIN_VALUE
            languageMap[LATVIAN] =
                LATVIAN_VALUE
            languageMap[LITHUANIAN] =
                LITHUANIAN_VALUE
            languageMap[MACEDONIAN] =
                MACEDONIAN_VALUE
            languageMap[MALAY] =
                MALAY_VALUE
            languageMap[MALTESE] =
                MALTESE_VALUE
            languageMap[NORWEGIAN] =
                NORWEGIAN_VALUE
            languageMap[PERSIAN] =
                PERSIAN_VALUE
            languageMap[POLISH] =
                POLISH_VALUE
            languageMap[PORTUGUESE] =
                PORTUGUESE_VALUE
            languageMap[ROMANIAN] =
                ROMANIAN_VALUE
            languageMap[RUSSIAN] =
                RUSSIAN_VALUE
            languageMap[SERBIAN] =
                SERBIAN_VALUE
            languageMap[SLOVAK] =
                SLOVAK_VALUE
            languageMap[SLOVENIAN] =
                SLOVENIAN_VALUE
            languageMap[SPANISH] =
                SPANISH_VALUE
            languageMap[SWAHILI] =
                SWAHILI_VALUE
            languageMap[SWEDISH] =
                SWEDISH_VALUE
            languageMap[TAMIL] =
                TAMIL_VALUE
            languageMap[TELUGU] =
                TELUGU_VALUE
            languageMap[THAI] =
                THAI_VALUE
            languageMap[TURKISH] =
                TURKISH_VALUE
            languageMap[UKRAINIAN] =
                UKRAINIAN_VALUE
            languageMap[URDU] =
                URDU_VALUE
            languageMap[VIETNAMESE] =
                VIETNAMESE_VALUE
            languageMap[WELSH] =
                WELSH_VALUE
            languageMap[YIDDISH] =
                YIDDISH_VALUE
            languageMap[CHINESE_SIMPLIFIED] =
                CHINESE_SIMPLIFIED_VALUE
            languageMap[CHINESE_TRADITIONAL] =
                CHINESE_TRADITIONAL_VALUE

            for ( (key, value) in languageMap) {
                languageList.add(value)
            }
        }

        fun getLanguage ( languageSign : String ) : String{
            var language = languageMap[languageSign]
            return language + ""
        }

        fun getLanguageSign (language : String) : String? {
            for ((sign, lang) in languageMap) {
                if (language == lang) return sign
            }
            return null
        }

    }

}