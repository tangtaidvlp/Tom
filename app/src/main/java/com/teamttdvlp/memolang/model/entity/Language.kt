package com.teamttdvlp.memolang.model.entity

import java.util.ArrayList



class Language {


    companion object {

        // First adapterPosition in languagePair detail
        public val SOURCE_LANGUAGE = 0

        // Second adapterPosition in languagePair detail
        public val TARGET_LANGUAGE = 1

        // Language devider
        public val LANG_DIVIDER = " - "

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
        val CHINESE_SIMPLIFIED = "zh-CN"
        val CHINESE_TRADITIONAL = "zh-TW"
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


        val AFRIKAANS_VALUE = "Afrikaans"
        val ALBANIAN_VALUE = "Albanian"
        val ARABIC_VALUE = "Arabic"
        val ARMENIAN_VALUE = "Armenian"
        val AZERBAIJANI_VALUE = "Azerbaijani"
        val BASQUE_VALUE = "Basque"
        val BELARUSIAN_VALUE = "Belarusian"
        val BENGALI_VALUE = "Bengali"
        val BULGARIAN_VALUE = "Bulgarian"
        val CATALAN_VALUE = "Catalan"
        val CHINESE_VALUE = "Chinese"
        val CHINESE_SIMPLIFIED_VALUE = "Chinese (Simplified)"
        val CHINESE_TRADITIONAL_VALUE = "Chinese (Traditional)"
        val CROATIAN_VALUE = "Croatian"
        val CZECH_VALUE = "Czech"
        val DANISH_VALUE = "Danish"
        val DUTCH_VALUE = "Dutch"
        val ENGLISH_VALUE = "English"
        val ESTONIAN_VALUE = "Estonian"
        val FILIPINO_VALUE = "Filipino"
        val FINNISH_VALUE = "Finnish"
        val FRENCH_VALUE = "French"
        val GALICIAN_VALUE = "Galician"
        val  GEORGIAN_VALUE = "Georgian"
        val GERMAN_VALUE = "German"
        val GREEK_VALUE = "Greek"
        val GUJARATI_VALUE = "Gujarati"
        val HAITIAN_CREOLE_VALUE = "Haitian Creole"
        val HEBREW_VALUE = "Hebrew"
        val HINDI_VALUE = "Hindi"
        val HUNGARIAN_VALUE = "Hungarian"
        val ICELANDIC_VALUE = "Icelandic"
        val INDONESIAN_VALUE = "Indonesian"
        val IRISH_VALUE = "Irish"
        val ITALIAN_VALUE = "Italian"
        val JAPANESE_VALUE = "Japanese"
        val KANNADA_VALUE = "Kannada"
        val KOREAN_VALUE = "Korean"
        val LATIN_VALUE = "Latin"
        val LATVIAN_VALUE = "Latvian"
        val LITHUANIAN_VALUE = "Lithuanian"
        val MACEDONIAN_VALUE = "Macedonian"
        val MALAY_VALUE = "Malay"
        val MALTESE_VALUE = "Maltese"
        val NORWEGIAN_VALUE = "Norwegian"
        val PERSIAN_VALUE = "Persian"
        val POLISH_VALUE = "Polish"
        val PORTUGUESE_VALUE = "Portuguese"
        val ROMANIAN_VALUE = "Romanian"
        val RUSSIAN_VALUE = "Russian"
        val SERBIAN_VALUE = "Serbian"
        val SLOVAK_VALUE = "Slovak"
        val SLOVENIAN_VALUE = "Slovenian"
        val SPANISH_VALUE = "Spanish"
        val SWAHILI_VALUE = "Swahili"
        val SWEDISH_VALUE = "Swedish"
        val TAMIL_VALUE = "Tamil"
        val TELUGU_VALUE = "Telugu"
        val THAI_VALUE = "Thai"
        val TURKISH_VALUE = "Turkish"
        val UKRAINIAN_VALUE = "Ukrainian"
        val URDU_VALUE = "Urdu"
        val VIETNAMESE_VALUE = "Vietnamese"
        val WELSH_VALUE = "Welsh"
        val YIDDISH_VALUE = "Yiddish"

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

            languageList.add(AFRIKAANS_VALUE)
            languageList.add(ALBANIAN_VALUE)
            languageList.add(ARABIC_VALUE)
            languageList.add(ARMENIAN_VALUE)
            languageList.add(AZERBAIJANI_VALUE)
            languageList.add(BASQUE_VALUE)
            languageList.add(BELARUSIAN_VALUE)
            languageList.add(BENGALI_VALUE)
            languageList.add(BULGARIAN_VALUE)
            languageList.add(CATALAN_VALUE)
            languageList.add(CHINESE_VALUE)
            languageList.add(CHINESE_SIMPLIFIED_VALUE)
            languageList.add(CHINESE_TRADITIONAL_VALUE)
            languageList.add(CROATIAN_VALUE)
            languageList.add(CZECH_VALUE)
            languageList.add(DANISH_VALUE)
            languageList.add(DUTCH_VALUE)
            languageList.add(ENGLISH_VALUE)
            languageList.add(ESTONIAN_VALUE)
            languageList.add(FILIPINO_VALUE)
            languageList.add(FINNISH_VALUE)
            languageList.add(FRENCH_VALUE)
            languageList.add(GALICIAN_VALUE)

            languageList.add(GERMAN_VALUE)
            languageList.add(GREEK_VALUE)
            languageList.add(GUJARATI_VALUE)
            languageList.add(HAITIAN_CREOLE_VALUE)
            languageList.add(HEBREW_VALUE)
            languageList.add(HINDI_VALUE)
            languageList.add(HUNGARIAN_VALUE)
            languageList.add(ICELANDIC_VALUE)
            languageList.add(INDONESIAN_VALUE)
            languageList.add(IRISH_VALUE)
            languageList.add(ITALIAN_VALUE)
            languageList.add(JAPANESE_VALUE)
            languageList.add(KANNADA_VALUE)
            languageList.add(KOREAN_VALUE)
            languageList.add(LATIN_VALUE)
            languageList.add(LATVIAN_VALUE)
            languageList.add(LITHUANIAN_VALUE)
            languageList.add(MACEDONIAN_VALUE)
            languageList.add(MALAY_VALUE)
            languageList.add(MALTESE_VALUE)
            languageList.add(NORWEGIAN_VALUE)
            languageList.add(PERSIAN_VALUE)
            languageList.add(POLISH_VALUE)
            languageList.add(PORTUGUESE_VALUE)
            languageList.add(ROMANIAN_VALUE)
            languageList.add(RUSSIAN_VALUE)
            languageList.add(SERBIAN_VALUE)
            languageList.add(SLOVAK_VALUE)
            languageList.add(SLOVENIAN_VALUE)
            languageList.add(SPANISH_VALUE)
            languageList.add(SWAHILI_VALUE)
            languageList.add(SWEDISH_VALUE)
            languageList.add(TAMIL_VALUE)
            languageList.add(TELUGU_VALUE)
            languageList.add(THAI_VALUE)
            languageList.add(TURKISH_VALUE)
            languageList.add(UKRAINIAN_VALUE)
            languageList.add(URDU_VALUE)
            languageList.add(VIETNAMESE_VALUE)
            languageList.add(WELSH_VALUE)
            languageList.add(YIDDISH_VALUE)
        }

        fun getLanguage ( languageSign : String ) : String{
            var language = languageMap[languageSign]
            return language + ""
        }

        fun getLanguageValue (language : String) : String? {
            for ((sign, lang) in languageMap) {
                if (language == lang) return sign
            }
            return null
        }

    }

}