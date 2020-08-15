package com.tonigames.reaction.popups

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.Rect
import android.media.MediaPlayer
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.nightonke.boommenu.BoomButtons.BoomButton
import com.nightonke.boommenu.BoomButtons.HamButton
import com.nightonke.boommenu.BoomMenuButton
import com.nightonke.boommenu.OnBoomListenerAdapter
import com.tonigames.reaction.ISettingChange
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.FIND_PAIR
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.GAME_TYPE
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.LEFT_RIGHT
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.ROCK_PAPER
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.TAP_COLOR
import com.tonigames.reaction.MainMenuCataEnum
import com.tonigames.reaction.R

class BoomMenuHandler(
    private val boomMenu: BoomMenuButton,
    private val gameTitle: TextView,
    private val context: ContextWrapper,
    private val soundBtnClick: MediaPlayer?,
    private val rewardedAd: RewardedAd?,
    private val gameTypeSelectCallback: () -> Unit
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

        addBuilderBMB(tapColorTitle, tapColorSubtitle, TAP_COLOR)       // 0
        addBuilderBMB(findPairTitle, findPairSubtitle, FIND_PAIR)       // 1
        addBuilderBMB(leftRightTitle, leftRightSubtitle, LEFT_RIGHT)    // 2
        addBuilderBMB(rockPaperTitle, rockPaperSubtitle, ROCK_PAPER)    // 3

        boomMenu.onBoomListener = object : OnBoomListenerAdapter() {
            override fun onClicked(index: Int, boomButton: BoomButton?) {
                super.onClicked(index, boomButton)

                val gameType = indexToGameTypeMap.getOrDefault(index, TAP_COLOR)
                val isGameLocked = ISettingChange.isGameLocked(context, gameType)

//                if (!isGameLocked) {
                context.getSharedPreferences(GAME_TYPE, Context.MODE_PRIVATE).apply {
                    edit().putInt(GAME_TYPE, gameType).commit()
                }
                refreshGameTitle()
                gameTypeSelectCallback.invoke()
//                } else {
//                    // game is locked, show popup of ads
//
//                }
            }
        }
    }

    private fun addBuilderBMB(text: String, subText: String, gameType: Int = TAP_COLOR) {
        val drawableIcon = when {
            ISettingChange.isGameLocked(context, gameType) -> R.drawable.menu_video_ads
            else -> R.drawable.menu_unlock
        }

        @Suppress("DEPRECATION")
        HamButton.Builder()
            .imagePadding(Rect(20, 40, 20, 40))
            .normalImageDrawable(ContextCompat.getDrawable(context, drawableIcon))
            .normalText(text)
            //.normalColor(Color.LTGRAY)        // color of button can be set here
            .shadowColor(Color.BLACK)
            .subNormalText(subText)
            .pieceColor(Color.WHITE).apply {
                boomMenu.addBuilder(this)
            }
            .listener {
                soundBtnClick?.start()
            }
    }

    fun onLanguageChanged() {
        val tapColorTitle = getTranslatedText(MainMenuCataEnum.TapColor)
        val findPairTitle = getTranslatedText(MainMenuCataEnum.FindPair)
        val leftRightTitle = getTranslatedText(MainMenuCataEnum.LeftOrRight)
        val rockPaperTitle = getTranslatedText(MainMenuCataEnum.RockPaper)

        val tapColorSubtitle = getTranslatedText(MainMenuCataEnum.TapColorSubtitle)
        val findPairSubtitle = getTranslatedText(MainMenuCataEnum.FindPairSubtitle)
        val leftRightSubtitle = getTranslatedText(MainMenuCataEnum.LeftOrRightSubtitle)
        val rockPaperSubtitle = getTranslatedText(MainMenuCataEnum.RockPaperSubtitle)

        with(boomMenu.getBuilder(0) as HamButton.Builder) {
            this.normalText(tapColorTitle)
            this.subNormalText(tapColorSubtitle)
        }

        with(boomMenu.getBuilder(1) as HamButton.Builder) {
            this.normalText(findPairTitle)
            this.subNormalText(findPairSubtitle)
        }

        with(boomMenu.getBuilder(2) as HamButton.Builder) {
            this.normalText(leftRightTitle)
            this.subNormalText(leftRightSubtitle)
        }

        with(boomMenu.getBuilder(3) as HamButton.Builder) {
            this.normalText(rockPaperTitle)
            this.subNormalText(rockPaperSubtitle)
        }

        refreshGameTitle()
    }

    private fun getTranslatedText(cateEnum: MainMenuCataEnum) = ISettingChange.translatedMenuText(
        context.resources,
        ISettingChange.currentLanguage(context),
        cateEnum
    )

    private fun refreshGameTitle() =
        context.getSharedPreferences(GAME_TYPE, Context.MODE_PRIVATE).getInt(GAME_TYPE, TAP_COLOR)
            .run {
                when (this) {
                    TAP_COLOR -> gameTitle.text = getTranslatedText(MainMenuCataEnum.TapColor)
                    FIND_PAIR -> gameTitle.text = getTranslatedText(MainMenuCataEnum.FindPair)
                    LEFT_RIGHT -> gameTitle.text = getTranslatedText(MainMenuCataEnum.LeftOrRight)
                    ROCK_PAPER -> gameTitle.text = getTranslatedText(MainMenuCataEnum.RockPaper)
                    else -> gameTitle.text = "???"
                }
            }

    private fun loadRewardAds() {
        val adLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            override fun onRewardedAdFailedToLoad(error: Int) {
                // Ad failed to load.
            }
        }
        rewardedAd?.loadAd(AdRequest.Builder().build(), adLoadCallback)
    }

    private fun showRewardAds() {
        val adCallback = object : RewardedAdCallback() {
            override fun onRewardedAdOpened() {
                // Ad opened.
            }

            override fun onRewardedAdClosed() {
                // Ad closed.
            }

            override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                // User earned reward.
            }

            override fun onRewardedAdFailedToShow(msg: Int) {
                // Ad failed to display.
            }
        }
        rewardedAd?.show(context as Activity, adCallback)   // ??????? convert
    }

    companion object {
        private val indexToGameTypeMap =
            mapOf(
                0 to TAP_COLOR,
                1 to FIND_PAIR,
                2 to LEFT_RIGHT,
                3 to ROCK_PAPER
            )
    }
}