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
import com.jeevandeshmukh.glidetoastlib.GlideToast
import com.tonigames.reaction.leftorright.LeftOrRightFragment
import com.tonigames.reaction.leftorright.ResultListener
import com.tonigames.reaction.leftorright.ViewOutState

class ManagerActivity : AppCompatActivity(), ResultListener {
    private var interstitialAd: InterstitialAd? = null

    private var soundBtnClick: MediaPlayer? = null
    private var soundNegative: MediaPlayer? = null
    private var soundPositive: MediaPlayer? = null

    private var mCurrFragment: LeftOrRightFragment? = null
    private var mDialogPopup: MaterialDialog? = null

    private var mRoundCnt: Int = -1
    private var mLastState: ViewOutState = ViewOutState.Invalid
    private var mLastImg: Int = Int.MIN_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_left_or_right_manager)

        initMedia()

        mCurrFragment = LeftOrRightFragment.newInstance(mRoundCnt.toString(), mLastImg.toString())
            .also {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }

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
        mRoundCnt = -1
        mLastImg = Int.MIN_VALUE
        mLastState = ViewOutState.Invalid

        mDialogPopup?.dismiss()

        mCurrFragment = LeftOrRightFragment.newInstance(mRoundCnt.toString(), mLastImg.toString())
            .also {
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

        mRoundCnt = -1
        initMedia()
    }

    override fun onStop() {
        super.onStop()

        mRoundCnt = -1
        mLastImg = Int.MIN_VALUE
        mLastState = ViewOutState.Invalid

        releaseMedia()
        mDialogPopup?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()

        mRoundCnt = -1
        mLastImg = Int.MIN_VALUE
        mLastState = ViewOutState.Invalid

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
    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(120)
        }
    }

    override fun onResult(currImg: Int, currState: ViewOutState) {

        if ((currImg == mLastImg && currState == mLastState) ||
            (currImg != mLastImg && currState != mLastState)
        ) {
            mLastState = currState
            mLastImg = currImg

            onSuccess()
        } else {
            mLastState = ViewOutState.Invalid

            onFailure("Wrong direction!")
        }
    }

    override fun onTimeUp() {
        onFailure("Time's up!")
        mLastState = ViewOutState.Invalid
    }

    private fun onFailure(msg: String = "") {
        mDialogPopup?.takeIf { it.isShowing }?.run { return@onFailure }

        soundNegative?.start()
        vibrate()

        mDialogPopup = MaterialDialog(this).customView(R.layout.game_over_popup).show {
            cancelable(false)
            cancelOnTouchOutside(false)
            cornerRadius(8f)
            findViewById<TextView>(R.id.title).text = msg
            findViewById<TextView>(R.id.scoreGameOver).text = mRoundCnt.toString()
            findViewById<TextView>(R.id.highScoreGameOver).text = getHighScore().toString()

            findViewById<Button>(R.id.btnGoHome).setOnClickListener { theButton ->
                YoYo.with(Techniques.Pulse).duration(200).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            soundBtnClick?.start()

                            Intent(
                                this@ManagerActivity,
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

        saveHighScore(mRoundCnt)
    }

    private fun onSuccess() {
        mDialogPopup?.takeIf { it.isShowing }?.run { return@onSuccess }


        if (mRoundCnt >= 0) {
            GlideToast.makeToast(
                this,
                "Right!!",
                GlideToast.LENGTHSHORT,
                GlideToast.SUCCESSTOAST,
                GlideToast.BOTTOM
            ).show()

            soundPositive?.takeIf { it.isPlaying }?.stop()
            soundPositive?.start()
        }

        ++mRoundCnt

        mCurrFragment = LeftOrRightFragment.newInstance(mRoundCnt.toString(), mLastImg.toString())
            .also {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
    }
}
