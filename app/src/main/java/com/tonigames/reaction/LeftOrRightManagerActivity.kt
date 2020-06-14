package com.tonigames.reaction

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.jeevandeshmukh.glidetoastlib.GlideToast
import com.tonigames.reaction.leftorright.LeftOrRightFragment
import com.tonigames.reaction.leftorright.LeftRightResultListener
import com.tonigames.reaction.leftorright.ViewOutState
import kotlinx.android.synthetic.main.activity_left_or_right.*

class LeftOrRightManagerActivity : AppCompatActivity(), LeftRightResultListener {
    private var interstitialAd: InterstitialAd? = null

    private var roundCnt: Int = -1
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

        currFragment = LeftOrRightFragment.newInstance(
            roundCnt.toString(),
            Int.MIN_VALUE.toString(),
            ViewOutState.Invalid.toString()
        ).also {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, it)
                .commit()
        }

        MobileAds.initialize(this) { adView.loadAd(AdRequest.Builder().build()) }

        interstitialAd = InterstitialAd(this).apply {
            adUnitId = resources.getString(R.string.ads_interstitial_unit_id)
            loadAd(AdRequest.Builder().build())
        }
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
        roundCnt = -1
        dialogPopup?.dismiss()

        currFragment = LeftOrRightFragment.newInstance(
            roundCnt.toString(),
            Int.MIN_VALUE.toString(),
            ViewOutState.Invalid.toString()
        ).also {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, it)
                .commit()
        }
    }

    private fun tryLoadAds() {
        interstitialAd?.let { ads ->
            ads.adListener = object : AdListener() {
                override fun onAdClosed() {
                    ads.loadAd(AdRequest.Builder().build())
                }
            }
            ads.takeIf { it.isLoaded }?.show()
        }
    }

    override fun onResume() {
        super.onResume()

        roundCnt = -1
        initMedia()
    }

    override fun onStop() {
        super.onStop()

        roundCnt = -1
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

    private var mLastState: ViewOutState? = null

    override fun onResult(lastImg: Int, state: ViewOutState) {

        if (mLastState == null || mLastState == state) {
            onSuccess(lastImg, state)
            mLastState = state

        } else if (mLastState != state) {
            onFailure("Wrong direction!")
            mLastState = null
        }
    }

    override fun onTimeUp() {
        onFailure("Time's up!")
        mLastState = null
    }

    private fun onFailure(msg: String = "") {
        dialogPopup?.takeIf { it.isShowing }?.run { return@onFailure }

        soundNegative?.start()
        vibrate()

        dialogPopup = MaterialDialog(this).customView(R.layout.game_over_popup).show {
            cancelable(false)
            cancelOnTouchOutside(false)
            cornerRadius(8f)
            findViewById<TextView>(R.id.title).text = msg
            findViewById<TextView>(R.id.scoreGameOver).text = roundCnt.toString()
            findViewById<TextView>(R.id.highScoreGameOver).text = getHighScore().toString()

            findViewById<Button>(R.id.btnGoHome).setOnClickListener { theButton ->
                YoYo.with(Techniques.Pulse).duration(200).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            soundBtnClick?.start()

                            Intent(
                                this@LeftOrRightManagerActivity,
                                MainMenuActivity::class.java
                            ).run {
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(this)
                            }
                        }
                    }).playOn(theButton)
            }

            findViewById<Button>(R.id.btnContinue).setOnClickListener { theButton ->
                YoYo.with(Techniques.Pulse).duration(200).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            soundBtnClick?.start()

                            handleContinueClicked()
                        }
                    }).playOn(theButton)
            }
        }

        saveHighScore(roundCnt)
    }

    private fun onSuccess(lastImage: Int, lastState: ViewOutState) {
        dialogPopup?.takeIf { it.isShowing }?.run { return@onSuccess }

        GlideToast.makeToast(
            this,
            "Correct!!",
            GlideToast.LENGTHMEDIUM,
            GlideToast.SUCCESSTOAST,
            GlideToast.BOTTOM
        ).show()

        soundPositive?.takeIf { it.isPlaying }?.stop()
        soundPositive?.start()

        ++roundCnt

        currFragment = LeftOrRightFragment.newInstance(
            roundCnt.toString(),
            lastImage.toString(),
            lastState.toString()
        ).also {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, it)
                .commit()
        }
    }
}
