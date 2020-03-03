package com.tonigames.fastreaction

import android.content.res.Resources
import com.tonigames.fastreaction.popups.MyLanguageEnum

interface ISettingChange {

    companion object {
        val englishMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_english,
            MainMenuCataEnum.FindPair to R.string.find_pair_english,
            MainMenuCataEnum.HighScore to R.string.high_score_english,
            MainMenuCataEnum.Ranking to R.string.score_rank_english
        )

        val germanMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_german,
            MainMenuCataEnum.FindPair to R.string.find_pair_german,
            MainMenuCataEnum.HighScore to R.string.high_score_german,
            MainMenuCataEnum.Ranking to R.string.score_rank_german
        )

        val frenchMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_french,
            MainMenuCataEnum.FindPair to R.string.find_pair_french,
            MainMenuCataEnum.HighScore to R.string.high_score_french,
            MainMenuCataEnum.Ranking to R.string.score_rank_french
        )

        val chineseMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_chinese,
            MainMenuCataEnum.FindPair to R.string.find_pair_chinese,
            MainMenuCataEnum.HighScore to R.string.high_score_chinese,
            MainMenuCataEnum.Ranking to R.string.score_rank_chinese
        )

        val japaneseMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_japanese,
            MainMenuCataEnum.FindPair to R.string.find_pair_japanese,
            MainMenuCataEnum.HighScore to R.string.high_score_japanese,
            MainMenuCataEnum.Ranking to R.string.score_rank_japanese
        )

        val koreanMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_korean,
            MainMenuCataEnum.FindPair to R.string.find_pair_korean,
            MainMenuCataEnum.HighScore to R.string.high_score_korean,
            MainMenuCataEnum.Ranking to R.string.score_rank_korean
        )

        val theMap = mapOf(
            MyLanguageEnum.English to englishMap,
            MyLanguageEnum.German to germanMap,
            MyLanguageEnum.French to frenchMap,
            MyLanguageEnum.Chinese to chineseMap,
            MyLanguageEnum.Japanese to japaneseMap,
            MyLanguageEnum.Korean to koreanMap
        )

        fun translatedMenuText(
            res: Resources,
            language: MyLanguageEnum = MyLanguageEnum.English,
            cata: MainMenuCataEnum
        ): String {
            return with(
                theMap.getOrDefault(language, englishMap)
                    .getOrDefault(cata, 0)
            ) {
                res.getString(this)
            }
        }
    }

    fun onLanguageChanged()

    fun onBgImageChanged()
}

enum class MainMenuCataEnum {
    TapColor,
    FindPair,
    HighScore,
    Ranking
}
