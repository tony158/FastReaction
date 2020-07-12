package com.tonigames.reaction

import android.content.res.Resources
import com.tonigames.reaction.popups.MyLanguageEnum

interface ISettingChange {

    companion object {
        private val englishMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_english,
            MainMenuCataEnum.FindPair to R.string.find_pair_english,
            MainMenuCataEnum.LeftOrRight to R.string.left_or_right_english,
            MainMenuCataEnum.HighScore to R.string.high_score_english,
            MainMenuCataEnum.Ranking to R.string.score_rank_english,
            MainMenuCataEnum.Coin to R.string.score_coin_english
        )

        private val germanMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_german,
            MainMenuCataEnum.FindPair to R.string.find_pair_german,
            MainMenuCataEnum.LeftOrRight to R.string.left_or_right_german,
            MainMenuCataEnum.HighScore to R.string.high_score_german,
            MainMenuCataEnum.Ranking to R.string.score_rank_german,
            MainMenuCataEnum.Coin to R.string.score_coin_german
        )

        private val frenchMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_french,
            MainMenuCataEnum.FindPair to R.string.find_pair_french,
            MainMenuCataEnum.LeftOrRight to R.string.left_or_right_french,
            MainMenuCataEnum.HighScore to R.string.high_score_french,
            MainMenuCataEnum.Ranking to R.string.score_rank_french,
            MainMenuCataEnum.Coin to R.string.score_coin_french
        )

        private val chineseMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_chinese,
            MainMenuCataEnum.FindPair to R.string.find_pair_chinese,
            MainMenuCataEnum.LeftOrRight to R.string.left_or_right_chinese,
            MainMenuCataEnum.HighScore to R.string.high_score_chinese,
            MainMenuCataEnum.Ranking to R.string.score_rank_chinese,
            MainMenuCataEnum.Coin to R.string.score_coin_chinese
        )

        private val spanishMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_spanish,
            MainMenuCataEnum.FindPair to R.string.find_pair_spanish,
            MainMenuCataEnum.LeftOrRight to R.string.left_or_right_spanish,
            MainMenuCataEnum.HighScore to R.string.high_score_spanish,
            MainMenuCataEnum.Ranking to R.string.score_rank_spanish,
            MainMenuCataEnum.Coin to R.string.score_coin_spanish
        )

        private val koreanMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_portu,
            MainMenuCataEnum.FindPair to R.string.find_pair_portu,
            MainMenuCataEnum.LeftOrRight to R.string.left_or_right_portu,
            MainMenuCataEnum.HighScore to R.string.high_score_portu,
            MainMenuCataEnum.Ranking to R.string.score_rank_portu,
            MainMenuCataEnum.Coin to R.string.score_coin_portu
        )

        private val theMap = mapOf(
            MyLanguageEnum.English to englishMap,
            MyLanguageEnum.German to germanMap,
            MyLanguageEnum.French to frenchMap,
            MyLanguageEnum.Chinese to chineseMap,
            MyLanguageEnum.Spanish to spanishMap,
            MyLanguageEnum.Portuguese to koreanMap
        )

        fun translatedMenuText(
            res: Resources,
            language: MyLanguageEnum = MyLanguageEnum.English,
            cata: MainMenuCataEnum
        ): String {
            return theMap.getOrDefault(language, englishMap).getOrDefault(cata, 0)
                .run {
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
    LeftOrRight,
    HighScore,
    Ranking,
    Coin
}
