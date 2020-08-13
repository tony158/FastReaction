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
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.jeevandeshmukh.glidetoastlib.GlideToast
import com.tonigames.reaction.ISettingChange.Companion.getHighScore
import com.tonigames.reaction.rockpaper.ResultListener
import com.tonigames.reaction.rockpaper.RockPaperFragment

class RockPaperManagerActivity : AppCompatActivity(), ResultListener {

    private var mRoundCnt: Int = 0
    private var mCurrFragment: Fragment? = null
    private var mDialogPopup: MaterialDialog? = null

    private var soundBtnClick: MediaPlayer? = null
    private var soundNegative: MediaPlayer? = null
    private var soundPositive: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_rock_paper_manager)

        initMedia()

        mCurrFragment = RockPaperFragment.newInstance(mRoundCnt.toString(), "0")
            .also {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
    }

    private fun initMedia() {
        soundBtnClick = MediaPlayer.create(this, R.raw.button_click)
        soundPositive = MediaPlayer.create(this, R.raw.correct_beep)
        soundNegative = MediaPlayer.create(this, R.raw.negative_beeps)
    }

    override fun onCorrectSelection() {
        mDialogPopup?.takeIf { it.isShowing }?.run { return@onCorrectSelection }

        GlideToast.makeToast(
            this,
            "Correct!!",
            GlideToast.LENGTHSHORT,
            GlideToast.SUCCESSTOAST,
            GlideToast.BOTTOM
        ).show()

        soundPositive?.takeIf { it.isPlaying }?.stop()
        soundPositive?.start()

        ++mRoundCnt
        mCurrFragment = RockPaperFragment.newInstance(mRoundCnt.toString(), "0")
            .also {
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

        Log.wtf("", "test")
    }

    override fun onFailedToSolve(msg: String) {
        mDialogPopup?.takeIf { it.isShowing }?.run { return@onFailedToSolve }

        soundNegative?.start()
        vibrate()

        mDialogPopup = MaterialDialog(this).customView(R.layout.game_over_popup).show {
            cancelable(false)
            cancelOnTouchOutside(false)
            cornerRadius(8f)
            findViewById<TextView>(R.id.title).text = msg
            findViewById<TextView>(R.id.scoreGameOver).text = mRoundCnt.toString()
            findViewById<TextView>(R.id.highScoreGameOver).text =
                getHighScore(MainMenuActivity.Constants.HIGH_SCORE_ROCK_PAPER).toString()

            findViewById<Button>(R.id.btnGoHome).setOnClickListener { theButton ->
                YoYo.with(Techniques.Pulse).duration(200).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            soundBtnClick?.start()

                            Intent(
                                this@RockPaperManagerActivity,
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

    private fun handleContinueClicked() {
        mRoundCnt = 0
//        mCurrFragment?.clearAllToggles()
        mDialogPopup?.dismiss()
    }

    //when score is higher than the current highest score, then save it
    private fun saveHighScore(score: Int) {
        getHighScore(this).takeIf { score > it }?.run {
            getSharedPreferences(
                MainMenuActivity.Constants.HIGH_SCORE_ROCK_PAPER,
                Context.MODE_PRIVATE
            ).edit()
                .putInt(MainMenuActivity.Constants.HIGH_SCORE_ROCK_PAPER, score)
                .commit()
        }
    }

    private fun getHighScore(gameType: String = "") = ISettingChange.getHighScore(this, gameType)

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