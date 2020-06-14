package com.tonigames.reaction

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.tonigames.reaction.leftorright.LeftOrRightFragment
import kotlinx.android.synthetic.main.activity_left_or_right.*

class LeftOrRightManagerActivity : AppCompatActivity() {
    private var interstitialAd: InterstitialAd? = null

    private var roundCnt: Int = 0
    private var currFragment: LeftOrRightFragment? = null
    private var dialogPopup: MaterialDialog? = null

    private var soundBtnClick: MediaPlayer? = null
    private var soundNegative: MediaPlayer? = null
    private var soundPositive: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_left_or_right)

        initMedia()

        currFragment = LeftOrRightFragment.newInstance(roundCnt.toString(), "").also {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, it)
                .commit()
        }

//        MobileAds.initialize(this) { adView.loadAd(AdRequest.Builder().build()) }

//        interstitialAd = InterstitialAd(this).apply {
//            adUnitId = resources.getString(R.string.ads_interstitial_unit_id)
//            loadAd(AdRequest.Builder().build())
//        }
    }

    // get the high score from Persistence
    private fun getHighScore(): Int {
        return getSharedPreferences(
            MainMenuActivity.Constants.HIGH_SCORE_LEFT_RIGHT,
            Context.MODE_PRIVATE
        ).run {
            getInt(MainMenuActivity.Constants.HIGH_SCORE_LEFT_RIGHT, 0)
        }
    }

    //when score is higher than the current highest score, then save it
    private fun saveHighScore(score: Int) {
        getHighScore().takeIf { score > it }?.run {
            getSharedPreferences(
                MainMenuActivity.Constants.HIGH_SCORE_LEFT_RIGHT,
                Context.MODE_PRIVATE
            ).edit()
                .putInt(MainMenuActivity.Constants.HIGH_SCORE_LEFT_RIGHT, score)
                .commit()
        }
    }

    private fun handleContinueClicked() {
        roundCnt = 0
        dialogPopup?.dismiss()

//        interstitialAd?.let { ads ->
//            ads.adListener = object : AdListener() {
//                override fun onAdClosed() {
//                    ads.loadAd(AdRequest.Builder().build())
//                }
//            }
//
//            ads.takeIf { it.isLoaded }?.show()
//        }
    }

    override fun onResume() {
        super.onResume()

        roundCnt = 0
        initMedia()
    }

    override fun onStop() {
        super.onStop()

        roundCnt = 0
        releaseMedia()
        dialogPopup?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()

        releaseMedia()
        dialogPopup?.dismiss()
    }

    private fun releaseMedia() {
        listOf(soundPositive, soundNegative, soundBtnClick).forEach { it?.release() }
    }

    private fun initMedia() {
        soundBtnClick = MediaPlayer.create(this, R.raw.button_click)
        soundPositive = MediaPlayer.create(this, R.raw.correct_beep)
        soundNegative = MediaPlayer.create(this, R.raw.negative_beeps)
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(120)
        }
    }
}
