package com.tonigames.reaction

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.media.MediaPlayer
import android.widget.TextView
import com.nightonke.boommenu.BoomButtons.BoomButton
import com.nightonke.boommenu.BoomButtons.HamButton
import com.nightonke.boommenu.BoomMenuButton
import com.nightonke.boommenu.OnBoomListenerAdapter
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.FIND_PAIR
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.GAME_TYPE
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.ROCK_PAPER
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.TAP_COLOR
import com.tonigames.reaction.popups.MyLanguageEnum

class BoomMenuHandler(
    private val boomMenu: BoomMenuButton,
    private val gameTitle: TextView,
    private val context: ContextWrapper,
    private val soundBtnClick: MediaPlayer?,
    private val updateCallback: () -> Unit
) {
    fun onCreate() = buildHamMenu()

    private fun buildHamMenu() {

        val tapColorTitle = getTranslatedText(MainMenuCataEnum.TapColor)
        val findPairTitle = getTranslatedText(MainMenuCataEnum.FindPair)
        val leftRightTitle = getTranslatedText(MainMenuCataEnum.LeftOrRight)
        val rockPaperTitle = getTranslatedText(MainMenuCataEnum.RockPaper)

        val tapColorSubtitle = getTranslatedText(MainMenuCataEnum.TapColorSubtitle)
        val findPairSubtitle = getTranslatedText(MainMenuCataEnum.FindPairSubtitle)
        val leftRightSubtitle = getTranslatedText(MainMenuCataEnum.LeftOrRightSubtitle)
        val rockPaperSubtitle = getTranslatedText(MainMenuCataEnum.RockPaperSubtitle)

        addBuilderBMB(leftRightTitle, leftRightSubtitle)
        addBuilderBMB(findPairTitle, findPairSubtitle)
        addBuilderBMB(tapColorTitle, tapColorSubtitle)
        addBuilderBMB(rockPaperTitle, rockPaperSubtitle)

        boomMenu.onBoomListener = object : OnBoomListenerAdapter() {
            override fun onClicked(index: Int, boomButton: BoomButton?) {
                super.onClicked(index, boomButton)

                val gameType = when (index) {
                    0 -> MainMenuActivity.Constants.Left_Right
                    1 -> MainMenuActivity.Constants.FIND_PAIR
                    2 -> MainMenuActivity.Constants.TAP_COLOR
                    else -> MainMenuActivity.Constants.ROCK_PAPER
                }

                context.getSharedPreferences(GAME_TYPE, Context.MODE_PRIVATE).apply {
                    edit().putInt(GAME_TYPE, gameType).commit()
                }

                when (index) {
                    0 -> gameTitle.text = getTranslatedText(MainMenuCataEnum.LeftOrRight)
                    1 -> gameTitle.text = getTranslatedText(MainMenuCataEnum.FindPair)
                    2 -> gameTitle.text = getTranslatedText(MainMenuCataEnum.TapColor)
                    else -> gameTitle.text = getTranslatedText(MainMenuCataEnum.RockPaper)
                }

                updateCallback()
            }
        }
    }

    /** get the current language setting*/
    private fun currentLanguage(): MyLanguageEnum {
        val languageIndex = context.getSharedPreferences(
            MainMenuActivity.Constants.SELECTED_LANGUAGE,
            Context.MODE_PRIVATE
        ).getInt(MainMenuActivity.Constants.SELECTED_LANGUAGE, 0)

        return MyLanguageEnum.fromIndex(languageIndex)
    }

    private fun addBuilderBMB(text: String, subText: String) {
        @Suppress("DEPRECATION")
        HamButton.Builder()
            .normalImageDrawable(context.resources.getDrawable(R.drawable.ic_play_to_stop))
            .normalText(text)
            .subNormalText(subText)
            .pieceColor(Color.BLACK).apply {
                boomMenu.addBuilder(this)
            }
            .listener {
                soundBtnClick?.start()
            }
    }

    fun onLanguageChanged() {
        val leftRightTitle = getTranslatedText(MainMenuCataEnum.LeftOrRight)
        val findPairTitle = getTranslatedText(MainMenuCataEnum.FindPair)
        val tapColorTitle = getTranslatedText(MainMenuCataEnum.TapColor)

        val leftRightSubtitle = getTranslatedText(MainMenuCataEnum.LeftOrRightSubtitle)
        val findPairSubtitle = getTranslatedText(MainMenuCataEnum.FindPairSubtitle)
        val tapColorSubtitle = getTranslatedText(MainMenuCataEnum.TapColorSubtitle)

        with(boomMenu.getBuilder(0) as HamButton.Builder) {
            this.normalText(leftRightTitle)
            this.subNormalText(leftRightSubtitle)
        }

        with(boomMenu.getBuilder(1) as HamButton.Builder) {
            this.normalText(findPairTitle)
            this.subNormalText(findPairSubtitle)
        }

        with(boomMenu.getBuilder(2) as HamButton.Builder) {
            this.normalText(tapColorTitle)
            this.subNormalText(tapColorSubtitle)
        }

        refreshGameTitle()
    }

    private fun getTranslatedText(cateEnum: MainMenuCataEnum) =
        ISettingChange.translatedMenuText(
            context.resources,
            currentLanguage(),
            cateEnum
        )


    private fun refreshGameTitle() =
        context.getSharedPreferences(GAME_TYPE, Context.MODE_PRIVATE).getInt(GAME_TYPE, TAP_COLOR)
            .run {
                when (this) {
                    TAP_COLOR -> gameTitle.text = getTranslatedText(MainMenuCataEnum.TapColor)
                    FIND_PAIR -> gameTitle.text = getTranslatedText(MainMenuCataEnum.FindPair)
                    ROCK_PAPER -> gameTitle.text = getTranslatedText(MainMenuCataEnum.RockPaper)
                    else -> gameTitle.text = getTranslatedText(MainMenuCataEnum.LeftOrRight)
                }
            }

}