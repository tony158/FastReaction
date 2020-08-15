package com.tonigames.reaction

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd

abstract class AbstractGameManagerActivity : AppCompatActivity() {

    var interstitialAd: InterstitialAd? = null

    var mRoundCnt: Int = 0
    var mDialogPopup: MaterialDialog? = null
    var soundBtnClick: MediaPlayer? = null
    var soundNegative: MediaPlayer? = null
    var soundPositive: MediaPlayer? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_find_pair_manager)

        interstitialAd = InterstitialAd(this).apply {
            adUnitId = resources.getString(R.string.ads_interstitial_unit_id)
            loadAd(AdRequest.Builder().build())
        }

        initMedia()
    }

    // get the high score from Persistence
    fun getHighScore(highScoreType: String): Int {
        return getSharedPreferences(highScoreType, Context.MODE_PRIVATE).run {
            getInt(highScoreType, 0)
        }
    }

    //when score is higher than the current highest score, then save it
    fun saveHighScore(score: Int, highScoreType: String) {
        getHighScore(highScoreType).takeIf { score > it }?.run {
            getSharedPreferences(highScoreType, Context.MODE_PRIVATE).edit()
                .putInt(highScoreType, score)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()

        mRoundCnt = 0
        initMedia()
    }

    override fun onStop() {
        super.onStop()

        mRoundCnt = 0
        releaseMedia()
        mDialogPopup?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()

        releaseMedia()
        mDialogPopup?.dismiss()
    }

    private fun releaseMedia() =
        listOf(soundPositive, soundNegative, soundBtnClick).forEach { it?.release() }


    private fun initMedia() {
        soundBtnClick = MediaPlayer.create(this, R.raw.button_click)
        soundPositive = MediaPlayer.create(this, R.raw.correct_beep)
        soundNegative = MediaPlayer.create(this, R.raw.negative_beeps)
    }

    @Suppress("DEPRECATION")
    fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(120)
        }
    }

    open fun handleContinueClicked() {
        mRoundCnt = 0
        mDialogPopup?.dismiss()

        interstitialAd?.let { ads ->
            ads.adListener = object : AdListener() {
                override fun onAdClosed() = ads.loadAd(AdRequest.Builder().build())
            }

            ads.takeIf { it.isLoaded }?.show()
        }
    }
}