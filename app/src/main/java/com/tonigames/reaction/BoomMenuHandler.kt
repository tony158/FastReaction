package com.tonigames.reaction

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.widget.TextView
import com.nightonke.boommenu.BoomButtons.BoomButton
import com.nightonke.boommenu.BoomButtons.HamButton
import com.nightonke.boommenu.BoomMenuButton
import com.nightonke.boommenu.OnBoomListenerAdapter
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.GAME_TYPE
import com.tonigames.reaction.popups.MyLanguageEnum

class BoomMenuHandler(
    private val bmb: BoomMenuButton,
    private val titleText: TextView,
    private val context: ContextWrapper
) {

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

        bmb.onBoomListener = object : OnBoomListenerAdapter() {
            override fun onClicked(index: Int, boomButton: BoomButton?) {
                super.onClicked(index, boomButton)

                val valueToPut = when (index) {
                    0 -> MainMenuActivity.Constants.TAP_COLOR
                    1 -> MainMenuActivity.Constants.FIND_PAIR
                    else -> MainMenuActivity.Constants.Left_Right
                }

                context.getSharedPreferences(GAME_TYPE, Context.MODE_PRIVATE).apply {
                    edit().putInt(GAME_TYPE, valueToPut).commit()
                }

                when (index) {
                    0 -> titleText.text = leftRightTitle
                    1 -> titleText.text = findPairTitle
                    2 -> titleText.text = tapColorTitle
                }
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

        //        bmb.onButtonClick(1,null)
        //        titleText?.text = ""    //TODO
    }
}