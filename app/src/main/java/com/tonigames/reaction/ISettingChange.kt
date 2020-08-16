package com.tonigames.reaction

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.FIND_PAIR
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.LEFT_RIGHT
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.LOCKED_FIND_PAIR
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.LOCKED_LEFT_RIGHT
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.LOCKED_ROCK_PAPER
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.LOCKED_TAP_COLOR
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.ROCK_PAPER
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.TAP_COLOR
import com.tonigames.reaction.popups.MyLanguageEnum

interface ISettingChange {

    companion object {
        private val englishMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_english,
            MainMenuCataEnum.FindPair to R.string.find_pair_english,
            MainMenuCataEnum.LeftOrRight to R.string.left_or_right_english,
            MainMenuCataEnum.RockPaper to R.string.rock_paper_english,
            MainMenuCataEnum.TapColorSubtitle to R.string.tap_color_description_english,
            MainMenuCataEnum.FindPairSubtitle to R.string.find_pair_description_english,
            MainMenuCataEnum.LeftOrRightSubtitle to R.string.left_or_right_description_english,
            MainMenuCataEnum.RockPaperSubtitle to R.string.rock_paper_description_english,
            MainMenuCataEnum.HighScore to R.string.high_score_english,
            MainMenuCataEnum.Ranking to R.string.score_rank_english,
            MainMenuCataEnum.Coin to R.string.score_coin_english
        )

        private val germanMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_german,
            MainMenuCataEnum.FindPair to R.string.find_pair_german,
            MainMenuCataEnum.LeftOrRight to R.string.left_or_right_german,
            MainMenuCataEnum.RockPaper to R.string.rock_paper_right_german,
            MainMenuCataEnum.TapColorSubtitle to R.string.tap_color_description_german,
            MainMenuCataEnum.FindPairSubtitle to R.string.find_pair_description_german,
            MainMenuCataEnum.LeftOrRightSubtitle to R.string.left_or_right_description_german,
            MainMenuCataEnum.RockPaperSubtitle to R.string.rock_paper_description_german,
            MainMenuCataEnum.HighScore to R.string.high_score_german,
            MainMenuCataEnum.Ranking to R.string.score_rank_german,
            MainMenuCataEnum.Coin to R.string.score_coin_german
        )

        private val frenchMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_french,
            MainMenuCataEnum.FindPair to R.string.find_pair_french,
            MainMenuCataEnum.LeftOrRight to R.string.left_or_right_french,
            MainMenuCataEnum.RockPaper to R.string.rock_paper_english,
            MainMenuCataEnum.TapColorSubtitle to R.string.tap_color_description_french,
            MainMenuCataEnum.FindPairSubtitle to R.string.find_pair_description_french,
            MainMenuCataEnum.LeftOrRightSubtitle to R.string.left_or_right_description_french,
            MainMenuCataEnum.RockPaperSubtitle to R.string.rock_paper_description_french,
            MainMenuCataEnum.HighScore to R.string.high_score_french,
            MainMenuCataEnum.Ranking to R.string.score_rank_french,
            MainMenuCataEnum.Coin to R.string.score_coin_french
        )

        private val chineseMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_chinese,
            MainMenuCataEnum.FindPair to R.string.find_pair_chinese,
            MainMenuCataEnum.LeftOrRight to R.string.left_or_right_chinese,
            MainMenuCataEnum.RockPaper to R.string.rock_paper_chinese,
            MainMenuCataEnum.TapColorSubtitle to R.string.tap_color_description_chinese,
            MainMenuCataEnum.FindPairSubtitle to R.string.find_pair_description_chinese,
            MainMenuCataEnum.LeftOrRightSubtitle to R.string.left_or_right_description_chinese,
            MainMenuCataEnum.RockPaperSubtitle to R.string.rock_paper_description_chinese,
            MainMenuCataEnum.HighScore to R.string.high_score_chinese,
            MainMenuCataEnum.Ranking to R.string.score_rank_chinese,
            MainMenuCataEnum.Coin to R.string.score_coin_chinese
        )

        private val spanishMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_spanish,
            MainMenuCataEnum.FindPair to R.string.find_pair_spanish,
            MainMenuCataEnum.LeftOrRight to R.string.left_or_right_spanish,
            MainMenuCataEnum.RockPaper to R.string.rock_paper_english,
            MainMenuCataEnum.TapColorSubtitle to R.string.tap_color_description_spanish,
            MainMenuCataEnum.FindPairSubtitle to R.string.find_pair_description_spanish,
            MainMenuCataEnum.LeftOrRightSubtitle to R.string.left_or_right_description_spanish,
            MainMenuCataEnum.RockPaperSubtitle to R.string.rock_paper_description_english,
            MainMenuCataEnum.HighScore to R.string.high_score_spanish,
            MainMenuCataEnum.Ranking to R.string.score_rank_spanish,
            MainMenuCataEnum.Coin to R.string.score_coin_spanish
        )

        private val portuMap = mapOf(
            MainMenuCataEnum.TapColor to R.string.tap_color_portu,
            MainMenuCataEnum.FindPair to R.string.find_pair_portu,
            MainMenuCataEnum.LeftOrRight to R.string.left_or_right_portu,
            MainMenuCataEnum.RockPaper to R.string.rock_paper_english,
            MainMenuCataEnum.TapColorSubtitle to R.string.tap_color_description_portu,
            MainMenuCataEnum.FindPairSubtitle to R.string.find_pair_description_portu,
            MainMenuCataEnum.LeftOrRightSubtitle to R.string.left_or_right_description_portu,
            MainMenuCataEnum.RockPaperSubtitle to R.string.rock_paper_description_english,
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
            MyLanguageEnum.Portuguese to portuMap
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

        fun getHighScore(context: Context, gameType: String = "") =
            context.getSharedPreferences(gameType, Context.MODE_PRIVATE).run {
                getInt(gameType, 0)
            }

        fun saveHighScore(context: Context, score: Int, highScoreType: String) {
            getHighScore(context, highScoreType).takeIf { score > it }?.run {
                context.getSharedPreferences(highScoreType, Context.MODE_PRIVATE).edit()
                    .putInt(highScoreType, score)
                    .commit()
            }
        }

        private val gameTypeToLockTypeMap = mapOf<Int, String>(
            TAP_COLOR to LOCKED_TAP_COLOR,
            FIND_PAIR to LOCKED_FIND_PAIR,
            LEFT_RIGHT to LOCKED_LEFT_RIGHT,
            ROCK_PAPER to LOCKED_ROCK_PAPER
        )

        fun unlockGame(context: Context, gameType: Int) {
            val lockType = gameTypeToLockTypeMap.getOrDefault(gameType, LOCKED_TAP_COLOR)

            context.getSharedPreferences(lockType, Context.MODE_PRIVATE)
                .edit().putBoolean(lockType, false).commit()
        }

        fun isGameLocked(context: Context, gameType: Int): Boolean {
            return when (val lockedType =
                gameTypeToLockTypeMap.getOrDefault(gameType, LOCKED_TAP_COLOR)) //
            {
                LOCKED_TAP_COLOR -> false
                else -> context.getSharedPreferences(
                    lockedType,
                    Context.MODE_PRIVATE
                ).getBoolean(lockedType, true)
            }
        }

        /** get the current language from preference */
        fun currentLanguage(context: ContextWrapper): MyLanguageEnum {
            val languageIndex = context.getSharedPreferences(
                MainMenuActivity.Constants.SELECTED_LANGUAGE,
                Context.MODE_PRIVATE
            ).getInt(MainMenuActivity.Constants.SELECTED_LANGUAGE, 0)

            return MyLanguageEnum.fromIndex(languageIndex)
        }
    }

    fun onLanguageChanged()
}

enum class MainMenuCataEnum {
    TapColor,
    FindPair,
    LeftOrRight,
    RockPaper,

    TapColorSubtitle,
    FindPairSubtitle,
    LeftOrRightSubtitle,
    RockPaperSubtitle,

    HighScore,
    Ranking,
    Coin
}
