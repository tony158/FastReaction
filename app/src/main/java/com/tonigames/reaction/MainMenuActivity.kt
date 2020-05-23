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
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.tonigames.reaction.ISettingChange.Companion.translatedMenuText
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.HIGH_SCORE_FIND_PAIR
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.HIGH_SCORE_LEFT_RIGHT
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.HIGH_SCORE_TAP_COLOR
import com.tonigames.reaction.cloud.FireBaseAccess
import com.tonigames.reaction.popups.LanguageSettingFragment
import com.tonigames.reaction.popups.MyLanguageEnum
import kotlinx.android.synthetic.main.activity_main.*
import nl.bryanderidder.themedtogglebuttongroup.SelectAnimation


class MainMenuActivity : AppCompatActivity(), ISettingChange {
    private var soundBtnClick: MediaPlayer? = null
    private var interstitialAd: InterstitialAd? = null

    private lateinit var fireBaseAccess: FireBaseAccess

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        radio_grp.selectAnimation = SelectAnimation.HORIZONTAL_WINDOW

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        soundBtnClick = MediaPlayer.create(this, R.raw.button_click)

        imageBtnLogo?.setOnClickListener {
            YoYo.with(Techniques.RubberBand).duration(800).playOn(it)
        }

        // test submit
        onLanguageChanged()
        initSettingButton()
        refreshRadioButtonState()
//        bindEventHandlerRadioButtons()
        bindEventHandlerStartButton()

        interstitialAd = InterstitialAd(this).apply {
            adUnitId = resources.getString(R.string.ads_interstitial_unit_id)
            loadAd(AdRequest.Builder().build())
        }

        fireBaseAccess =
            FireBaseAccess(Secure.getString(contentResolver, Secure.ANDROID_ID), textViewRank)
    }

    private fun initSettingButton() {
        tapBarMenu.setOnClickListener {
            soundBtnClick?.start()
            tapBarMenu.toggle()
        }

        listOf(barMenuItemLanguage, barMenuItemRanking, barMenuItemLike).forEach { it ->
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
            when (getSharedPreferences(Constants.GAME_TYPE, Context.MODE_PRIVATE)
                .getInt(Constants.GAME_TYPE, Constants.TAP_COLOR)) {
                Constants.TAP_COLOR -> TapColorManagerActivity::class.java
                Constants.FIND_PAIR -> FindPairManagerActivity::class.java
                else -> LeftOrRightActivity::class.java
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

    private fun bindEventHandlerRadioButtons() {
        listOf(radio_tapcolor, radio_findpair, radio_leftright)
            .forEach {
                it.setOnClickListener(fun(it: View) {
                    YoYo.with(Techniques.Pulse).duration(300).withListener(
                        object : DefaultAnimatorListener() {
                            override fun onAnimationStart(animation: Animator?) {
                                soundBtnClick?.start()

                                val valueToPut = when (it) {
                                    radio_tapcolor -> Constants.TAP_COLOR
                                    radio_findpair -> Constants.FIND_PAIR
                                    else -> Constants.Left_Right
                                }

                                getSharedPreferences(
                                    Constants.GAME_TYPE,
                                    Context.MODE_PRIVATE
                                ).apply {
                                    edit().putInt(Constants.GAME_TYPE, valueToPut).commit()
                                }

                                when (valueToPut) {
                                    Constants.TAP_COLOR -> score.text =
                                        getHighScore(HIGH_SCORE_TAP_COLOR).toString()
                                    Constants.FIND_PAIR -> score.text =
                                        getHighScore(HIGH_SCORE_FIND_PAIR).toString()
                                    else -> score.text =
                                        getHighScore(HIGH_SCORE_LEFT_RIGHT).toString()
                                }

                                refreshHighestScore()
                            }
                        }
                    ).playOn(it)
                })
            }
    }

    private fun refreshRadioButtonState() {
        getSharedPreferences(Constants.GAME_TYPE, Context.MODE_PRIVATE)
            .getInt(Constants.GAME_TYPE, Constants.TAP_COLOR).run {
                val toCheck = when (this) {
                    Constants.TAP_COLOR -> R.id.radio_tapcolor
                    Constants.FIND_PAIR -> R.id.radio_findpair
                    else -> R.id.radio_leftright
                }
                radio_grp?.selectButton(toCheck)
            }
    }

    override fun onResume() {
        super.onResume()
        initSounds()
        refreshRadioButtonState()

        getSharedPreferences(Constants.GAME_TYPE, Context.MODE_PRIVATE)
            .getInt(Constants.GAME_TYPE, Constants.TAP_COLOR).run {
                val gameType =
                    if (this == Constants.TAP_COLOR) HIGH_SCORE_TAP_COLOR else HIGH_SCORE_FIND_PAIR
                score.text = getHighScore(gameType).toString()
            }

        refreshHighestScore()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun showLanguageSettingPP() {
        LanguageSettingFragment(this).show(supportFragmentManager, "test11")
    }

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

    private fun getHighScore(gameType: String = ""): Int {
        return getSharedPreferences(gameType, Context.MODE_PRIVATE).run {
            getInt(gameType, 0)
        }
    }

    class Constants {
        companion object {
            const val GAME_TYPE: String = "GameType"
            const val HIGH_SCORE_TAP_COLOR: String = "HighScoreTapColor"
            const val HIGH_SCORE_FIND_PAIR: String = "HighScoreFindPair"
            const val HIGH_SCORE_LEFT_RIGHT: String = "HighScoreLeftRight"

            const val TAP_COLOR: Int = 0
            const val FIND_PAIR: Int = 1
            const val Left_Right: Int = 2

            const val SELECTED_LANGUAGE = "SelectedLanguage"
            const val SELECTED_BG_IMAGE = "SelectedBgImage"
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

    private fun releaseSounds() {
        soundBtnClick?.release()
    }

    private fun initSounds() {
        soundBtnClick = MediaPlayer.create(this, R.raw.button_click)
    }

    /** get the current language setting*/
    private fun currentLanguage(): MyLanguageEnum {
        val languageIndex = getSharedPreferences(
            Constants.SELECTED_LANGUAGE,
            Context.MODE_PRIVATE
        ).getInt(Constants.SELECTED_LANGUAGE, 0)

        return MyLanguageEnum.fromIndex(languageIndex)
    }

    private fun refreshHighestScore() {

        getSharedPreferences(Constants.GAME_TYPE, Context.MODE_PRIVATE)
            .getInt(Constants.GAME_TYPE, Constants.TAP_COLOR)
            .run {
                val gameType =
                    if (this == Constants.TAP_COLOR) HIGH_SCORE_TAP_COLOR else HIGH_SCORE_FIND_PAIR

                fireBaseAccess.updateScore(this, getHighScore(gameType))
            }
    }

    override fun onLanguageChanged() {
        val currLanguage: MyLanguageEnum = currentLanguage()

        val tapColor = translatedMenuText(resources, currLanguage, MainMenuCataEnum.TapColor)
        val findPair = translatedMenuText(resources, currLanguage, MainMenuCataEnum.FindPair)
        val leftOrRight = translatedMenuText(resources, currLanguage, MainMenuCataEnum.LeftOrRight)

        val highScore = translatedMenuText(resources, currLanguage, MainMenuCataEnum.HighScore)
        val ranking = translatedMenuText(resources, currLanguage, MainMenuCataEnum.Ranking)

        radio_tapcolor.text = tapColor
        radio_findpair.text = findPair
        radio_leftright.text = leftOrRight

        textViewHighScore.text = highScore
        textViewRanking.text = ranking
    }

    override fun onBgImageChanged() {
        //to implement later
    }
}
