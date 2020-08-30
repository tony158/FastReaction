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
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.tonigames.reaction.Constants.Companion.FIND_PAIR
import com.tonigames.reaction.Constants.Companion.HIGH_SCORE_TAP_COLOR
import com.tonigames.reaction.Constants.Companion.IMAGE_ANAGRAM
import com.tonigames.reaction.Constants.Companion.LEFT_RIGHT
import com.tonigames.reaction.Constants.Companion.ROCK_PAPER
import com.tonigames.reaction.Constants.Companion.SELECTED_GAME_TYPE
import com.tonigames.reaction.Constants.Companion.TAP_COLOR
import com.tonigames.reaction.IGameSettings.Companion.gameTypeToHighScoreTypeMap
import com.tonigames.reaction.IGameSettings.Companion.translatedMenuText
import com.tonigames.reaction.cloud.FireBaseAccess
import com.tonigames.reaction.popups.BoomMenuHandler
import com.tonigames.reaction.popups.LanguageSettingFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainMenuActivity : AppCompatActivity(), IGameSettings {
    private var interstitialAd: InterstitialAd? = null
    private var soundBtnClick: MediaPlayer? = null
    private var rewardedAd: RewardedVideoAd? = null

    private var bmbMenuHandler: BoomMenuHandler? = null
    private lateinit var fireBaseAccess: FireBaseAccess

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this, resources.getString(R.string.ads_reward_unit_id))
        rewardedAd = MobileAds.getRewardedVideoAdInstance(this)
        interstitialAd = InterstitialAd(this).apply {
            adUnitId = resources.getString(R.string.ads_interstitial_unit_id)

            try {
                loadAd(AdRequest.Builder().build())
            } catch (e: Exception) {
                Log.wtf("InterstitialAd", "excpetion occurred when loading interstitialAd!")
            }
        }

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
        val targetActivity =
            when (getSharedPreferences(SELECTED_GAME_TYPE, Context.MODE_PRIVATE).getInt(
                SELECTED_GAME_TYPE,
                TAP_COLOR
            )) {
                TAP_COLOR -> TapColorGameManagerActivity::class.java
                FIND_PAIR -> FindPairGameManagerActivity::class.java
                LEFT_RIGHT -> LeftRightGameManagerActivity::class.java
                ROCK_PAPER -> RockPaperGameManagerActivity::class.java
                IMAGE_ANAGRAM -> ImageAnagramManagerActivity::class.java
                else -> TapColorGameManagerActivity::class.java
            }

        interstitialAd?.adListener = object : AdListener() {
            override fun onAdClosed() {
                try {
                    interstitialAd?.loadAd(AdRequest.Builder().build())
                } catch (e: Exception) {
                    Log.wtf("InterstitialAd", "excpetion occurred when loading interstitialAd!")
                }
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
        bmbMenuHandler?.refreshLockState()
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

    private fun getHighScore(highScoreType: String = "") =
        IGameSettings.getHighScore(this, highScoreType)

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
        getSharedPreferences(SELECTED_GAME_TYPE, Context.MODE_PRIVATE).getInt(
            SELECTED_GAME_TYPE,
            TAP_COLOR
        ).run {
            val highScoreType = gameTypeToHighScoreTypeMap.getOrDefault(this, HIGH_SCORE_TAP_COLOR)

            val highScore = getHighScore(highScoreType)
            score.text = highScore.toString()
            fireBaseAccess.updateScore(this, highScore)
        }
    }

    private fun showWatchAdsPopup() {

    }

    override fun onLanguageChanged() {
        val currLanguage = IGameSettings.currentLanguage(this)
        val highScore = translatedMenuText(resources, currLanguage, MainMenuCataEnum.HighScore)
        val ranking = translatedMenuText(resources, currLanguage, MainMenuCataEnum.Ranking)

        textViewHighScore.text = highScore
        textViewRanking.text = ranking

        bmbMenuHandler?.refreshBoomMenu()
    }
}
