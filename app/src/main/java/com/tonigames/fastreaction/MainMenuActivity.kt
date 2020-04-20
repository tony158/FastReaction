package com.tonigames.fastreaction

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
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.firebase.database.*
import com.tonigames.fastreaction.ISettingChange.Companion.translatedMenuText
import com.tonigames.fastreaction.MainMenuActivity.Constants.Companion.HIGH_SCORE_FIND_PAIR
import com.tonigames.fastreaction.MainMenuActivity.Constants.Companion.HIGH_SCORE_TAP_COLOR
import com.tonigames.fastreaction.cloud.FireBaseAccess
import com.tonigames.fastreaction.popups.LanguageSettingFragment
import com.tonigames.fastreaction.popups.MyLanguageEnum
import kotlinx.android.synthetic.main.activity_main.*


class MainMenuActivity : AppCompatActivity(), ISettingChange {
    private var soundBtnClick: MediaPlayer? = null
    private var interstitialAd: InterstitialAd? = null

    private lateinit var fireBaseAccess: FireBaseAccess

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        soundBtnClick = MediaPlayer.create(this, R.raw.button_click)

        imageBtnLogo?.setOnClickListener {
            YoYo.with(Techniques.RubberBand).duration(800).playOn(it)
        }

        onLanguageChanged()
        initSettingButton()
        refreshRadioButtonState()
        bindEventHandlerRadioButtons()
        bindEventHandlerStartButton()

        interstitialAd = InterstitialAd(this).apply {
            adUnitId = resources.getString(R.string.ads_interstitial_unit_id)
            loadAd(AdRequest.Builder().build())
        }

        fireBaseAccess = FireBaseAccess(
            FirebaseDatabase.getInstance(),
            Secure.getString(contentResolver, Secure.ANDROID_ID),
            textViewRank
        )
    }

    private fun initSettingButton() {
        tapBarMenu.setOnClickListener {
            soundBtnClick?.start()
            tapBarMenu.toggle()
        }

        listOf(barMenuItemLanguage, barMenuItemLike).forEach { it ->
            it.setOnClickListener {
                soundBtnClick?.start()

                YoYo.with(Techniques.Pulse).duration(120).withListener(object :
                    DefaultAnimatorListener() {
                    override fun onAnimationEnd(animation: Animator?) {
                        tapBarMenu.close()
                        when (it) {
                            barMenuItemLanguage -> showLanguageSettingPP()
                            /*barMenuItemTheme -> showBgImgSettingPP()*/
                            barMenuItemLike -> openRatingLink()
                            /*barMenuItemAds -> showLanguageSettingPP()*/
                        }
                    }
                }).playOn(it)
            }
        }
    }

    private fun bindEventHandlerStartButton() {
        btnStart?.setOnClickListener(View.OnClickListener {
            YoYo.with(Techniques.Pulse).duration(120).withListener(object :
                DefaultAnimatorListener() {

                override fun onAnimationStart(animation: Animator?) {
                    soundBtnClick?.start()
                }

                override fun onAnimationEnd(animation: Animator?) {
                    gotoNextActivity()
                }
            }).playOn(it)
        })
    }

    private fun gotoNextActivity() {
        val targetActivity =
            if (getSharedPreferences(Constants.GAME_TYPE, Context.MODE_PRIVATE)
                    .getInt(
                        Constants.GAME_TYPE,
                        Constants.TAP_COLOR
                    ) == Constants.TAP_COLOR
            ) {
                TapColorManagerActivity::class.java
            } else {
                FindPairManagerActivity::class.java
            }

        interstitialAd?.adListener = object : AdListener() {
            override fun onAdClosed() {
                interstitialAd?.loadAd(AdRequest.Builder().build())

                val intent = Intent(this@MainMenuActivity, targetActivity)
                startActivity(intent)
            }
        }

        val isAdLoaded = interstitialAd?.isLoaded ?: false
        if (isAdLoaded) {
            interstitialAd?.show()
        } else {
            val intent = Intent(this@MainMenuActivity, targetActivity)
            startActivity(intent)
        }
    }

    private fun bindEventHandlerRadioButtons() {
        listOf<RadioButton>(radio_tapcolor, radio_findpair)
            .forEach {
                it.setOnClickListener(fun(it: View) {
                    YoYo.with(Techniques.Pulse).duration(300).withListener(
                        object : DefaultAnimatorListener() {
                            override fun onAnimationStart(animation: Animator?) {
                                soundBtnClick?.start()

                                val valueToPut =
                                    if (it == radio_tapcolor) Constants.TAP_COLOR else Constants.FIND_PAIR

                                getSharedPreferences(
                                    Constants.GAME_TYPE,
                                    Context.MODE_PRIVATE
                                ).edit().putInt(Constants.GAME_TYPE, valueToPut).commit()

                                with(if (valueToPut == 0) HIGH_SCORE_TAP_COLOR else HIGH_SCORE_FIND_PAIR) {
                                    score.text = getHighScore(this).toString()
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
                radio_grp?.check(if (this == Constants.TAP_COLOR) R.id.radio_tapcolor else R.id.radio_findpair)
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

    private fun showBgImgSettingPP() {
        //BgImageSettingFragment(this).show(supportFragmentManager, "test11")
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

            const val TAP_COLOR: Int = 0
            const val FIND_PAIR: Int = 1

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
        val highScore = translatedMenuText(resources, currLanguage, MainMenuCataEnum.HighScore)

        radio_tapcolor.text = tapColor
        radio_findpair.text = findPair
        textViewHighScore.text = highScore
    }

    override fun onBgImageChanged() {
        //to implement later
    }
}
