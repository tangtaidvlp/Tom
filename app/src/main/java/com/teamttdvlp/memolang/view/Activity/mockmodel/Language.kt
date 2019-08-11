package com.teamttdvlp.memolang.view.Activity.mockmodel

class Language {

    constructor() {

        languageMap[AFRIKAANS] = AFRIKAANS
        languageMap[ALBANIAN] = ALBANIAN
        languageMap[ARABIC] = ARABIC
        languageMap[ARMENIAN] = ARMENIAN
        languageMap[AZERBAIJANI] = AZERBAIJANI
        languageMap[BASQUE] = BASQUE
        languageMap[BELARUSIAN] = BELARUSIAN
        languageMap[BENGALI] = BENGALI
        languageMap[BULGARIAN] = BULGARIAN
        languageMap[CATALAN] = CATALAN
        languageMap[CHINESE] = CHINESE
        languageMap[CROATIAN] = CROATIAN
        languageMap[CZECH] = CZECH
        languageMap[DANISH] = DANISH
        languageMap[DUTCH] = DUTCH
        languageMap[ENGLISH] = ENGLISH
        languageMap[ESTONIAN] = ESTONIAN
        languageMap[FILIPINO] = FILIPINO
        languageMap[FINNISH] = FINNISH
        languageMap[FRENCH] = FRENCH
        languageMap[GALICIAN] = GALICIAN
        languageMap[GEORGIAN] = GEORGIAN
        languageMap[GERMAN] = GERMAN
        languageMap[GREEK] = GREEK
        languageMap[GUJARATI] = GUJARATI
        languageMap[HAITIAN_CREOLE] = HAITIAN_CREOLE
        languageMap[HEBREW] = HEBREW
        languageMap[HINDI] = HINDI
        languageMap[HUNGARIAN] = HUNGARIAN
        languageMap[ICELANDIC] = ICELANDIC
        languageMap[INDONESIAN] = INDONESIAN
        languageMap[IRISH] = IRISH
        languageMap[ITALIAN] = ITALIAN
        languageMap[JAPANESE] = JAPANESE
        languageMap[KANNADA] = KANNADA
        languageMap[KOREAN] = KOREAN
        languageMap[LATIN] = LATIN
        languageMap[LATVIAN] = LATVIAN
        languageMap[LITHUANIAN] = LITHUANIAN
        languageMap[MACEDONIAN] = MACEDONIAN
        languageMap[MALAY] = MALAY
        languageMap[MALTESE] = MALTESE
        languageMap[NORWEGIAN] = NORWEGIAN
        languageMap[PERSIAN] = PERSIAN
        languageMap[POLISH] = POLISH
        languageMap[PORTUGUESE] = PORTUGUESE
        languageMap[ROMANIAN] = ROMANIAN
        languageMap[RUSSIAN] = RUSSIAN
        languageMap[SERBIAN] = SERBIAN
        languageMap[SLOVAK] = SLOVAK
        languageMap[SLOVENIAN] = SLOVENIAN
        languageMap[SPANISH] = SPANISH
        languageMap[SWAHILI] = SWAHILI
        languageMap[SWEDISH] = SWEDISH
        languageMap[TAMIL] = TAMIL
        languageMap[TELUGU] = TELUGU
        languageMap[THAI] = THAI
        languageMap[TURKISH] = TURKISH
        languageMap[UKRAINIAN] = UKRAINIAN
        languageMap[URDU] = URDU
        languageMap[VIETNAMESE] = VIETNAMESE
        languageMap[WELSH] = WELSH
        languageMap[YIDDISH] = YIDDISH
        languageMap[CHINESE_SIMPLIFIED] = CHINESE_SIMPLIFIED
        languageMap[CHINESE_TRADITIONAL] = CHINESE_TRADITIONAL

    }

    companion object {
        private val languageMap= HashMap<String, String>()

        fun getLanguage ( languageSign : String ) : String{
            var language = languageMap[languageSign]
            return language!!
        }

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
       private val HAITIAN_CREOLE_VALUE = "Haitian_creole"
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

    }

}