package com.tonigames.reaction

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import com.nightonke.boommenu.BoomButtons.HamButton
import com.nightonke.boommenu.BoomMenuButton
import com.tonigames.reaction.popups.MyLanguageEnum

class BoomMenuHandler(private val bmb: BoomMenuButton, private val context: ContextWrapper) {

    fun onCreate() {
        buildHamMenu()
    }

    private fun buildHamMenu() {

        val currLanguage: MyLanguageEnum = currentLanguage()
        val tapColorTitle =
            ISettingChange.translatedMenuText(
                context.resources,
                currLanguage,
                MainMenuCataEnum.TapColor
            )
        val findPairTitle =
            ISettingChange.translatedMenuText(
                context.resources,
                currLanguage,
                MainMenuCataEnum.FindPair
            )
        val leftRightTitle =
            ISettingChange.translatedMenuText(
                context.resources,
                currLanguage,
                MainMenuCataEnum.LeftOrRight
            )

        val tapColorSubtitle =
            ISettingChange.translatedMenuText(
                context.resources,
                currLanguage,
                MainMenuCataEnum.TapColorSubtitle
            )
        val findPairSubtitle =
            ISettingChange.translatedMenuText(
                context.resources,
                currLanguage,
                MainMenuCataEnum.FindPairSubtitle
            )

        val leftRightSubtitle =
            ISettingChange.translatedMenuText(
                context.resources,
                currLanguage,
                MainMenuCataEnum.LeftOrRightSubtitle
            )

        addBuilderBMB(leftRightTitle, leftRightSubtitle)
        addBuilderBMB(findPairTitle, findPairSubtitle)
        addBuilderBMB(tapColorTitle, tapColorSubtitle)
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
        bmb.addBuilder(
            HamButton.Builder()
                .normalText(text)
                .subNormalText(subText)
                .pieceColor(Color.BLACK)
        )
    }

    fun onLanguageChanged() {
        val currLanguage: MyLanguageEnum = currentLanguage()
        val tapColorTitle =
            ISettingChange.translatedMenuText(
                context.resources,
                currLanguage,
                MainMenuCataEnum.TapColor
            )
        val findPairTitle =
            ISettingChange.translatedMenuText(
                context.resources,
                currLanguage,
                MainMenuCataEnum.FindPair
            )
        val leftRightTitle =
            ISettingChange.translatedMenuText(
                context.resources,
                currLanguage,
                MainMenuCataEnum.LeftOrRight
            )

        val tapColorSubtitle =
            ISettingChange.translatedMenuText(
                context.resources,
                currLanguage,
                MainMenuCataEnum.TapColorSubtitle
            )
        val findPairSubtitle =
            ISettingChange.translatedMenuText(
                context.resources,
                currLanguage,
                MainMenuCataEnum.FindPairSubtitle
            )
        val leftRightSubtitle =
            ISettingChange.translatedMenuText(
                context.resources,
                currLanguage,
                MainMenuCataEnum.LeftOrRightSubtitle
            )

        bmb.getBoomButton(0)?.textView?.text = leftRightTitle
        bmb.getBoomButton(0)?.subTextView?.text = leftRightSubtitle

        bmb.getBoomButton(1)?.textView?.text = findPairTitle
        bmb.getBoomButton(1)?.subTextView?.text = findPairSubtitle

        bmb.getBoomButton(2)?.textView?.text = tapColorTitle
        bmb.getBoomButton(2)?.subTextView?.text = tapColorSubtitle
    }
}