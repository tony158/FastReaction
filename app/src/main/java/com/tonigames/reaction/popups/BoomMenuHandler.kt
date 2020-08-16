package com.tonigames.reaction.popups

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.Rect
import android.media.MediaPlayer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.nightonke.boommenu.BoomButtons.BoomButton
import com.nightonke.boommenu.BoomButtons.HamButton
import com.nightonke.boommenu.BoomMenuButton
import com.nightonke.boommenu.OnBoomListenerAdapter
import com.tonigames.reaction.Constants.Companion.FIND_PAIR
import com.tonigames.reaction.Constants.Companion.HIGH_SCORE_FIND_PAIR
import com.tonigames.reaction.Constants.Companion.HIGH_SCORE_LEFT_RIGHT
import com.tonigames.reaction.Constants.Companion.HIGH_SCORE_TAP_COLOR
import com.tonigames.reaction.Constants.Companion.LEFT_RIGHT
import com.tonigames.reaction.Constants.Companion.ROCK_PAPER
import com.tonigames.reaction.Constants.Companion.SELECTED_GAME_TYPE
import com.tonigames.reaction.Constants.Companion.TAP_COLOR
import com.tonigames.reaction.IGameSettings
import com.tonigames.reaction.MainMenuCataEnum
import com.tonigames.reaction.R
import es.dmoral.toasty.Toasty

private const val UNLOCK_GAME_SCORE_THRESHOLD = 50

class BoomMenuHandler(
    private val boomMenu: BoomMenuButton,
    private val gameTitle: TextView,
    private val context: ContextWrapper,
    private val soundBtnClick: MediaPlayer?,
    private val rewardedAd: RewardedVideoAd?,
    private val gameTypeSelectCallback: () -> Unit
) {
    fun onCreate() {
        refreshLockState()
        buildHamMenu()
    }

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
                val isGameLocked = IGameSettings.isGameLocked(context, gameType)

                if (!isGameLocked) {
                    context.getSharedPreferences(SELECTED_GAME_TYPE, Context.MODE_PRIVATE).edit()
                        .putInt(SELECTED_GAME_TYPE, gameType).commit()

                    refreshGameTitle()
                    gameTypeSelectCallback.invoke()
                } else {
                    // game is locked, show popup of ads
                    MaterialDialog(context).customView(R.layout.ask_watch_ads_popup).show {
                        cornerRadius(10f)
                        val dialog = this
                        findViewById<Button>(R.id.btnAcceptAds).setOnClickListener {
                            soundBtnClick?.start()
                            dialog.dismiss()

                            loadRewardAds(gameType)
                        }

                        findViewById<Button>(R.id.btnRefuseAds).setOnClickListener {
                            soundBtnClick?.start()
                            dialog.dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun addBuilderBMB(title: String, subTitle: String, gameType: Int = TAP_COLOR) {
        val drawableIcon = when {
            IGameSettings.isGameLocked(context, gameType) -> R.drawable.menu_locked
            else -> R.drawable.menu_unlocked
        }

        @Suppress("DEPRECATION")
        HamButton.Builder()
            .imagePadding(Rect(20, 30, 20, 30))
            .normalImageDrawable(ContextCompat.getDrawable(context, drawableIcon))
            .normalText(title)
            //.normalColor(Color.GRAY)        // color of button can be set here
            .subNormalText(subTitle)
            .shadowColor(Color.BLACK)
            .pieceColor(Color.BLACK).apply { boomMenu.addBuilder(this) }
            .listener { soundBtnClick?.start() }
    }

    fun refreshBoomMenu() {
        val tapColorTitle = getTranslatedText(MainMenuCataEnum.TapColor)
        val findPairTitle = getTranslatedText(MainMenuCataEnum.FindPair)
        val leftRightTitle = getTranslatedText(MainMenuCataEnum.LeftOrRight)
        val rockPaperTitle = getTranslatedText(MainMenuCataEnum.RockPaper)

        val tapColorSubtitle = getTranslatedText(MainMenuCataEnum.TapColorSubtitle)
        val findPairSubtitle = getTranslatedText(MainMenuCataEnum.FindPairSubtitle)
        val leftRightSubtitle = getTranslatedText(MainMenuCataEnum.LeftOrRightSubtitle)
        val rockPaperSubtitle = getTranslatedText(MainMenuCataEnum.RockPaperSubtitle)

        val tapColorLocked = IGameSettings.isGameLocked(context, TAP_COLOR)
        val findPairLocked = IGameSettings.isGameLocked(context, FIND_PAIR)
        val leftRightLocked = IGameSettings.isGameLocked(context, LEFT_RIGHT)
        val rockPaperLocked = IGameSettings.isGameLocked(context, ROCK_PAPER)

        val drawableTapColor = when {
            tapColorLocked -> R.drawable.menu_locked
            else -> R.drawable.menu_unlocked
        }
        with(boomMenu.getBuilder(0) as HamButton.Builder) {
            this.normalText(tapColorTitle)
            this.subNormalText(tapColorSubtitle)
            this.normalImageDrawable(ContextCompat.getDrawable(context, drawableTapColor))
        }

        val drawableFindPair = when {
            findPairLocked -> R.drawable.menu_locked
            else -> R.drawable.menu_unlocked
        }
        with(boomMenu.getBuilder(1) as HamButton.Builder) {
            this.normalText(findPairTitle)
            this.subNormalText(findPairSubtitle)
            this.normalImageDrawable(ContextCompat.getDrawable(context, drawableFindPair))
        }

        val drawableLeftRight = when {
            leftRightLocked -> R.drawable.menu_locked
            else -> R.drawable.menu_unlocked
        }
        with(boomMenu.getBuilder(2) as HamButton.Builder) {
            this.normalText(leftRightTitle)
            this.subNormalText(leftRightSubtitle)
            this.normalImageDrawable(ContextCompat.getDrawable(context, drawableLeftRight))
        }

        val drawableRockPaper = when {
            rockPaperLocked -> R.drawable.menu_locked
            else -> R.drawable.menu_unlocked
        }
        with(boomMenu.getBuilder(3) as HamButton.Builder) {
            this.normalText(rockPaperTitle)
            this.subNormalText(rockPaperSubtitle)
            this.normalImageDrawable(ContextCompat.getDrawable(context, drawableRockPaper))
        }

        refreshGameTitle()
    }

    private fun getTranslatedText(cateEnum: MainMenuCataEnum) = IGameSettings.translatedMenuText(
        context.resources,
        IGameSettings.currentLanguage(context),
        cateEnum
    )

    private fun refreshGameTitle() =
        context.getSharedPreferences(SELECTED_GAME_TYPE, Context.MODE_PRIVATE)
            .getInt(SELECTED_GAME_TYPE, TAP_COLOR)
            .run {
                when (this) {
                    TAP_COLOR -> gameTitle.text = getTranslatedText(MainMenuCataEnum.TapColor)
                    FIND_PAIR -> gameTitle.text = getTranslatedText(MainMenuCataEnum.FindPair)
                    LEFT_RIGHT -> gameTitle.text = getTranslatedText(MainMenuCataEnum.LeftOrRight)
                    ROCK_PAPER -> gameTitle.text = getTranslatedText(MainMenuCataEnum.RockPaper)
                    else -> gameTitle.text = "???"
                }
            }

    private fun loadRewardAds(gameType: Int) {
        showWaitingInfoToast()

        rewardedAd?.loadAd(
            context.resources.getString(R.string.ads_reward_unit_id),
            AdRequest.Builder().build()
        )

        rewardedAd?.rewardedVideoAdListener = createRewardAdsCallback(gameType)
    }

    private fun createRewardAdsCallback(gameType: Int): RewardedVideoAdListener {
        return object : RewardedVideoAdListener {
            override fun onRewardedVideoAdClosed() {
                val isGameLocked = IGameSettings.isGameLocked(context, gameType)
                if (isGameLocked) {
                    showFailureToast("Video is not completed yet!")
                }
            }

            override fun onRewardedVideoAdLeftApplication() {
                Log.i("BoomMenuHandler", "onRewardedVideoAdLeftApplication")
            }

            override fun onRewardedVideoAdLoaded() {
                rewardedAd?.takeIf { it.isLoaded }?.show()
            }

            override fun onRewardedVideoAdOpened() {
                Log.i("BoomMenuHandler", "onRewardedVideoAdOpened")
            }

            override fun onRewardedVideoCompleted() {
                showSuccessToast("The game will be unlocked!")

                IGameSettings.unlockGame(context, gameType)
                refreshBoomMenu()
            }

            override fun onRewarded(p0: RewardItem?) {
                showSuccessToast("The game is unlocked now, have fun!")
            }

            override fun onRewardedVideoStarted() {
                Log.i("BoomMenuHandler", "onRewardedVideoStarted!")
            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {
                showFailureToast("Failed to load video, please try again after a minute!")
            }
        }
    }

    private fun showSuccessToast(msg: String = "") =
        Toasty.success(context, msg, Toast.LENGTH_LONG, true).show();

    private fun showFailureToast(msg: String = "") =
        Toasty.warning(context, msg, Toast.LENGTH_LONG, true).show()

    private fun showWaitingInfoToast() = Toasty.normal(
        context,
        R.string.wait_loading_video_english,
        Toast.LENGTH_LONG,
        ContextCompat.getDrawable(context, R.drawable.menu_clock),
        true
    ).show()

    fun refreshLockState() {
        val findPairLocked = IGameSettings.isGameLocked(context, FIND_PAIR)
        val leftRightLocked = IGameSettings.isGameLocked(context, LEFT_RIGHT)
        val rockPaperLocked = IGameSettings.isGameLocked(context, ROCK_PAPER)

        val tapColorHighScore = IGameSettings.getHighScore(context, HIGH_SCORE_TAP_COLOR)
        val findPairHighScore = IGameSettings.getHighScore(context, HIGH_SCORE_FIND_PAIR)
        val leftRightHighScore = IGameSettings.getHighScore(context, HIGH_SCORE_LEFT_RIGHT)

        var hasGameUnlocked = false
        if (findPairLocked && tapColorHighScore >= UNLOCK_GAME_SCORE_THRESHOLD) {
            IGameSettings.unlockGame(context, FIND_PAIR)
            hasGameUnlocked = true
        }
        if (leftRightLocked && findPairHighScore >= UNLOCK_GAME_SCORE_THRESHOLD) {
            IGameSettings.unlockGame(context, LEFT_RIGHT)
            hasGameUnlocked = true
        }
        if (rockPaperLocked && leftRightHighScore >= UNLOCK_GAME_SCORE_THRESHOLD) {
            IGameSettings.unlockGame(context, ROCK_PAPER)
            hasGameUnlocked = true
        }

        if (hasGameUnlocked) refreshBoomMenu()
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