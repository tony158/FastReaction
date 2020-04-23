package com.tonigames.reaction.popups

data class LanguageSettingModel(
    var languageName: String = "",
    var countryFlag: Int = 0,   // resource id, e.g. R.drawable.xxx
    var checkedImage: Int = 0   // resource id, e.g. R.drawable.xxx
)

enum class MyLanguageEnum {
    English,
    German,
    French,
    Chinese,
    Japanese,
    Korean;

    companion object {
        private val map = values().map { it.ordinal to it }.toMap()

        fun fromIndex(languageIndex: Int): MyLanguageEnum = map.getOrDefault(languageIndex, English)
    }
}