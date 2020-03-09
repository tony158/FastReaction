package com.tonigames.fastreaction

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.jeevandeshmukh.glidetoastlib.GlideToast.*
import com.tonigames.fastreaction.tapcolor.FragmentInteractionListener
import com.tonigames.fastreaction.tapcolor.TapColorFragmentFour
import com.tonigames.fastreaction.tapcolor.TapColorFragmentThree
import com.tonigames.fastreaction.tapcolor.TapColorFragmentTwo
import kotlinx.android.synthetic.main.activity_tap_color_manager.*
import kotlin.math.max


class TapColorManagerActivity : AppCompatActivity(), FragmentInteractionListener {

    private var roundCnt: Int = 0
    private var currFragment: Fragment? = null
    private var dialogPopup: MaterialDialog? = null

    private var soundBtnClick: MediaPlayer? = null
    private var soundNegative: MediaPlayer? = null
    private var soundPositive: MediaPlayer? = null

    companion object {
        val RoundColorCount: Map<Int, Int> =
            mapOf(1 to 2, 2 to 2, 3 to 2, 4 to 2, 5 to 3, 6 to 3, 7 to 3, 8 to 4, 9 to 4, 10 to 4)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_tap_color_manager)

        initMedia()

        val colorCnt = RoundColorCount.getOrDefault(
            if (roundCnt % RoundColorCount.size == 0) 1 else roundCnt % RoundColorCount.size,
            2
        )

        with(
            when (colorCnt) {
                4 -> TapColorFragmentThree.newInstance(roundCnt.toString(), "test4")
                3 -> TapColorFragmentThree.newInstance(roundCnt.toString(), "test3")
                else -> TapColorFragmentTwo.newInstance(roundCnt.toString(), "test2")
            }
        ) {
            currFragment = this

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, this)
                .commit()
        }

        MobileAds.initialize(this) { adView.loadAd(AdRequest.Builder().build()) }
    }

    override fun onCorrectColorSelected() {
        dialogPopup?.let {
            if (it.isShowing) {
                Log.w("TapColorManagerActivity", "dialog is shown, do not show next Fragment!")
                return
            }
        }

        makeToast(this, "Correct!!", LENGTHSHORT, SUCCESSTOAST, TOP).show()

        soundPositive?.let {
            if (it.isPlaying) soundPositive?.stop()
            it.start()
        }

        ++roundCnt

        //determine how many color buttons the next fragment has
        val colorCnt = RoundColorCount.getOrDefault(
            if (roundCnt % RoundColorCount.size == 0) 1 else roundCnt % RoundColorCount.size,
            2
        )

        with(
            when (colorCnt) {
                4 -> TapColorFragmentFour.newInstance(roundCnt.toString(), "test4")
                3 -> TapColorFragmentThree.newInstance(roundCnt.toString(), "test3")
                else -> TapColorFragmentTwo.newInstance(roundCnt.toString(), "test2")
            }
        ) {
            currFragment = this
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.fragment_container, this)
                .commit()
        }
    }

    private fun getHighScore(): Int {
        return with(
            getSharedPreferences(
                MainMenuActivity.Constants.HIGH_SCORE_TAP_COLOR,
                Context.MODE_PRIVATE
            )
        ) {
            getInt(MainMenuActivity.Constants.HIGH_SCORE_TAP_COLOR, 0)
        }
    }

    private fun saveHighScore(roundCnt: Int) {
        with(getHighScore()) {
            if (roundCnt > this) {
                getSharedPreferences(
                    MainMenuActivity.Constants.HIGH_SCORE_TAP_COLOR, Context.MODE_PRIVATE
                ).edit()
                    .putInt(
                        MainMenuActivity.Constants.HIGH_SCORE_TAP_COLOR,
                        max(this, max(roundCnt, 0))
                    ).commit()
            }
        }
    }

    override fun onFailedToSolve(msg: String) {
        dialogPopup?.let {
            if (it.isShowing) {
                Log.w("TapColorManagerActivity", "dialog is already shown!!!")
                return
            }
        }

        soundNegative?.start()
        vibrate()

        dialogPopup = MaterialDialog(this).customView(R.layout.game_over_popup).show {
            cancelable(false)
            cancelOnTouchOutside(false)
            cornerRadius(8f)

            findViewById<TextView>(R.id.title).text = msg
            findViewById<TextView>(R.id.scoreGameOver).text = roundCnt.toString()
            findViewById<TextView>(R.id.highScoreGameOver).text = getHighScore().toString()

            findViewById<Button>(R.id.btnGoHome).setOnClickListener {
                soundBtnClick?.start()

                with(Intent(this@TapColorManagerActivity, MainMenuActivity::class.java)) {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(this)
                }
            }
            findViewById<Button>(R.id.btnContinue).setOnClickListener {
                soundBtnClick?.start()

                roundCnt = 0
                onCorrectColorSelected()
                this.dismiss()
            }
        }

        saveHighScore(max(roundCnt, getHighScore()))
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
        dialogPopup?.let { it.dismiss() }
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
