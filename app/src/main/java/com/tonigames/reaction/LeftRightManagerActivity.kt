package com.tonigames.reaction

import android.animation.Animator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.jeevandeshmukh.glidetoastlib.GlideToast
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.HIGH_SCORE_LEFT_RIGHT
import com.tonigames.reaction.leftorright.LeftOrRightFragment
import com.tonigames.reaction.leftorright.ResultListener
import com.tonigames.reaction.leftorright.ViewOutState

class LeftRightManagerActivity : AbstractManagerActivity(), ResultListener {

    private var mCurrFragment: LeftOrRightFragment? = null

    private var mLastState: ViewOutState = ViewOutState.Invalid
    private var mLastImg: Int = Int.MIN_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCurrFragment = LeftOrRightFragment.newInstance(mRoundCnt.toString(), mLastImg.toString())
            .also {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
    }

    override fun handleContinueClicked() {
        super.handleContinueClicked()

        mLastImg = Int.MIN_VALUE
        mLastState = ViewOutState.Invalid

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
                override fun onAdClosed() = ads.loadAd(AdRequest.Builder().build())
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

    override fun onResult(lastImg: Int, state: ViewOutState) {

        if ((lastImg == mLastImg && state == mLastState) ||
            (lastImg != mLastImg && state != mLastState)
        ) {
            mLastState = state
            mLastImg = lastImg

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
            findViewById<TextView>(R.id.highScoreGameOver).text =
                getHighScore(HIGH_SCORE_LEFT_RIGHT).toString()

            findViewById<Button>(R.id.btnGoHome).setOnClickListener { theButton ->
                YoYo.with(Techniques.Pulse).duration(200).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            soundBtnClick?.start()

                            Intent(
                                this@LeftRightManagerActivity,
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

        saveHighScore(mRoundCnt, HIGH_SCORE_LEFT_RIGHT)
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
