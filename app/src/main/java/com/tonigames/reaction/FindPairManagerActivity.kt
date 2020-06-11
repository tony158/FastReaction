package com.tonigames.reaction

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.jeevandeshmukh.glidetoastlib.GlideToast.*
import com.tonigames.reaction.findpair.*
import kotlinx.android.synthetic.main.activity_tap_color_manager.*

class FindPairManagerActivity : AppCompatActivity(), FindPairInteractionListener {

    private var interstitialAd: InterstitialAd? = null

    private var roundCnt: Int = 0
    private var currFragment: AbstractFindPairFragment? = null
    private var dialogPopup: MaterialDialog? = null

    private var soundBtnClick: MediaPlayer? = null
    private var soundNegative: MediaPlayer? = null
    private var soundPositive: MediaPlayer? = null

    companion object {
        val RoundImgRowCount: Map<Int, Int> =
            mapOf(1 to 2, 2 to 2, 3 to 2, 4 to 2, 5 to 3, 6 to 3, 7 to 3, 8 to 4, 9 to 4, 10 to 4)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_find_pair_manager)

        initMedia()

        val imgRowCnt = RoundImgRowCount.getOrDefault(
            if (roundCnt % RoundImgRowCount.size == 0) 1 else roundCnt % RoundImgRowCount.size,
            2
        )

        currFragment = when (imgRowCnt) {
            4 -> FindPairFragmentFour.newInstance(roundCnt.toString(), "")
            3 -> FindPairFragmentThree.newInstance(roundCnt.toString(), "")
            else -> FindPairFragmentTwo.newInstance(roundCnt.toString(), "")
        }.also {
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
            MainMenuActivity.Constants.HIGH_SCORE_FIND_PAIR,
            Context.MODE_PRIVATE
        ).run {
            getInt(MainMenuActivity.Constants.HIGH_SCORE_FIND_PAIR, 0)
        }
    }

    //when score is higher than the current highest score, then save it
    private fun saveHighScore(score: Int) {
        getHighScore().takeIf { score > it }?.run {
            getSharedPreferences(
                MainMenuActivity.Constants.HIGH_SCORE_FIND_PAIR,
                Context.MODE_PRIVATE
            ).edit()
                .putInt(MainMenuActivity.Constants.HIGH_SCORE_FIND_PAIR, score)
                .commit()
        }
    }

    override fun onCorrectPairSelected() {
        dialogPopup?.takeIf { it.isShowing }?.run { return@onCorrectPairSelected }

        makeToast(this, "Correct!!", LENGTHMEDIUM, SUCCESSTOAST, BOTTOM).show()

        soundPositive?.takeIf { it.isPlaying }?.stop()
        soundPositive?.start()

        ++roundCnt

        val imgRowCnt = RoundImgRowCount.getOrDefault(
            if (roundCnt % RoundImgRowCount.size == 0) 1 else roundCnt % RoundImgRowCount.size,
            2
        )

        currFragment = when (imgRowCnt) {
            4 -> FindPairFragmentFour.newInstance(roundCnt.toString(), "")
            3 -> FindPairFragmentThree.newInstance(roundCnt.toString(), "")
            else -> FindPairFragmentTwo.newInstance(roundCnt.toString(), "")
        }.also {
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.fragment_container, it)
                .commit()
        }
    }

    override fun onFailedToSolve(msg: String) {
        dialogPopup?.takeIf { it.isShowing }?.run { return@onFailedToSolve }

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

                            Intent(this@FindPairManagerActivity, MainMenuActivity::class.java).run {
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


    private fun handleContinueClicked() {
        roundCnt = 0
        currFragment?.clearAllToggles()
        dialogPopup?.dismiss()

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
