package com.tonigames.reaction

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.Secure
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.tonigames.reaction.ISettingChange.Companion.translatedMenuText
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.GAME_TYPE
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.TAP_COLOR
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.FIND_PAIR
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.LEFT_RIGHT
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.ROCK_PAPER
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.HIGH_SCORE_TAP_COLOR
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.HIGH_SCORE_FIND_PAIR
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.HIGH_SCORE_LEFT_RIGHT
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.HIGH_SCORE_ROCK_PAPER
import com.tonigames.reaction.cloud.FireBaseAccess
import com.tonigames.reaction.popups.BoomMenuHandler
import com.tonigames.reaction.popups.LanguageSettingFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainMenuActivity : AppCompatActivity(), ISettingChange {
    private var interstitialAd: InterstitialAd? = null
    private var soundBtnClick: MediaPlayer? = null
    private var rewardedAd: RewardedAd? = null

    private var bmbMenuHandler: BoomMenuHandler? = null
    private lateinit var fireBaseAccess: FireBaseAccess

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        soundBtnClick = MediaPlayer.create(this, R.raw.button_click)
        bmbMenuHandler = BoomMenuHandler(
            bmb,
            gameTitle,
            this,
            soundBtnClick,
            rewardedAd,
            gameTypeSelectCallback = { refreshHighScore() }
        ).also { it.onCreate() }

        imageBtnLogo?.setOnClickListener {
            YoYo.with(Techniques.RubberBand).duration(800).playOn(it)
        }

        onLanguageChanged()
        initSettingButton()

        bindEventHandlerStartButton()

        rewardedAd = RewardedAd(this, resources.getString(R.string.ads_interstitial_unit_id))

        fireBaseAccess = Secure.getString(contentResolver, Secure.ANDROID_ID).run {
            FireBaseAccess(this, textViewRank)
        }
    }

    private fun initSettingButton() {
        tapBarMenu.setOnClickListener {
            tapBarMenu.toggle()
        }

        listOf(barMenuItemLanguage, barMenuItemLike).forEach { it ->
            it.setOnClickListener {
                soundBtnClick?.start()

                YoYo.with(Techniques.Pulse).duration(120).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            tapBarMenu.close()
                            when (it) {
                                barMenuItemLanguage -> showLanguageSettingPP()
                                barMenuItemLanguage -> showLeaderBoardPP()
                                barMenuItemLike -> openRatingLink()
                                /*barMenuItemAds -> showLanguageSettingPP()*/
                            }
                        }
                    }).playOn(it)
            }
        }
    }

    private fun bindEventHandlerStartButton() {
        btnStart?.setOnClickListener {
            YoYo.with(Techniques.Pulse).duration(120).withListener(
                object : DefaultAnimatorListener() {
                    override fun onAnimationStart(animation: Animator?) {
                        soundBtnClick?.start()
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        gotoNextActivity()
                    }
                }).playOn(it)
        }
    }

    private fun gotoNextActivity() {
        val targetActivity = when (getSharedPreferences(GAME_TYPE, Context.MODE_PRIVATE).getInt(
            GAME_TYPE,
            TAP_COLOR
        )) {
            TAP_COLOR -> TapColorManagerActivity::class.java
            FIND_PAIR -> FindPairManagerActivity::class.java
            LEFT_RIGHT -> LeftRightManagerActivity::class.java
            ROCK_PAPER -> RockPaperManagerActivity::class.java
            else -> TapColorManagerActivity::class.java
        }

        interstitialAd?.adListener = object : AdListener() {
            override fun onAdClosed() {
                interstitialAd?.loadAd(AdRequest.Builder().build())
                startActivity(Intent(this@MainMenuActivity, targetActivity))
            }
        }

        val isAdLoaded = interstitialAd?.isLoaded ?: false
        if (isAdLoaded) {
            interstitialAd?.show()
        } else {
            startActivity(Intent(this@MainMenuActivity, targetActivity))
        }
    }

    override fun onResume() {
        super.onResume()
        initSounds()

        refreshHighScore()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun showLanguageSettingPP() =
        LanguageSettingFragment(this).show(supportFragmentManager, "test11")


    private fun showLeaderBoardPP() {
    }

    private fun openRatingLink() {
        val uri1 = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        val uri2 = Uri.parse("market://details?id=$packageName")

        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri1))
        } catch (ex: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, uri2))
        }
    }

    private fun getHighScore(gameType: String = "") = ISettingChange.getHighScore(this, gameType)

    class Constants {
        companion object {
            const val GAME_TYPE: String = "GameType"

            const val HIGH_SCORE_TAP_COLOR: String = "HighScoreTapColor"
            const val HIGH_SCORE_FIND_PAIR: String = "HighScoreFindPair"
            const val HIGH_SCORE_LEFT_RIGHT: String = "HighScoreLeftRight"
            const val HIGH_SCORE_ROCK_PAPER: String = "HighScoreRockPaper"

            const val LOCKED_TAP_COLOR: String = "LockedTapColor"
            const val LOCKED_FIND_PAIR: String = "LockedFindPair"
            const val LOCKED_LEFT_RIGHT: String = "LockedLeftRight"
            const val LOCKED_ROCK_PAPER: String = "LockedRockPaper"

            const val TAP_COLOR: Int = 0
            const val FIND_PAIR: Int = 1
            const val LEFT_RIGHT: Int = 2
            const val ROCK_PAPER: Int = 3

            const val SELECTED_LANGUAGE = "SelectedLanguage"
        }
    }

    override fun onStop() {
        super.onStop()
        releaseSounds()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseSounds()
    }

    private fun releaseSounds() = soundBtnClick?.release()

    private fun initSounds() = MediaPlayer.create(this, R.raw.button_click).also {
        soundBtnClick = it
    }

    private fun refreshHighScore() {
        getSharedPreferences(GAME_TYPE, Context.MODE_PRIVATE).getInt(GAME_TYPE, TAP_COLOR).run {
            val gameType = when (this) {
                TAP_COLOR -> HIGH_SCORE_TAP_COLOR
                FIND_PAIR -> HIGH_SCORE_FIND_PAIR
                LEFT_RIGHT -> HIGH_SCORE_LEFT_RIGHT
                ROCK_PAPER -> HIGH_SCORE_ROCK_PAPER
                else -> HIGH_SCORE_TAP_COLOR
            }

            score.text = getHighScore(gameType).toString()
            fireBaseAccess.updateScore(this, getHighScore(gameType))
        }
    }

    override fun onLanguageChanged() {
        val currLanguage = ISettingChange.currentLanguage(this)
        val highScore = translatedMenuText(resources, currLanguage, MainMenuCataEnum.HighScore)
        val ranking = translatedMenuText(resources, currLanguage, MainMenuCataEnum.Ranking)

        textViewHighScore.text = highScore
        textViewRanking.text = ranking

        bmbMenuHandler?.onLanguageChanged()
    }
}
