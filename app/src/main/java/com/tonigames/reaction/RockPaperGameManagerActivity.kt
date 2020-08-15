package com.tonigames.reaction

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.jeevandeshmukh.glidetoastlib.GlideToast
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.HIGH_SCORE_ROCK_PAPER
import com.tonigames.reaction.rockpaper.ResultListener
import com.tonigames.reaction.rockpaper.RockPaperFragment

class RockPaperGameManagerActivity : AbstractGameManagerActivity(), ResultListener {

    private var mCurrFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCurrFragment = RockPaperFragment.newInstance(mRoundCnt.toString(), "0")
            .also {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
    }

    override fun onCorrectSelection() {
        mDialogPopup?.takeIf { it.isShowing }?.run { return@onCorrectSelection }

        showSuccessToast(GlideToast.LENGTHSHORT)

        soundPositive?.takeIf { it.isPlaying }?.stop()
        soundPositive?.start()

        ++mRoundCnt
        mCurrFragment = RockPaperFragment.newInstance(mRoundCnt.toString(), "0")
            .also {
                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
    }

    override fun onFailedToSolve(msg: String) {
        mDialogPopup?.takeIf { it.isShowing }?.run { return@onFailedToSolve }

        soundNegative?.start()
        vibrate()

        mDialogPopup = MaterialDialog(this).customView(R.layout.game_over_popup).show {
            configDialog( this, msg, mRoundCnt, getHighScore(HIGH_SCORE_ROCK_PAPER))

            findViewById<Button>(R.id.btnGoHome).setOnClickListener { theButton ->
                YoYo.with(Techniques.Pulse).duration(200).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            soundBtnClick?.start()

                            Intent(
                                this@RockPaperGameManagerActivity,
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

        saveHighScore(mRoundCnt, HIGH_SCORE_ROCK_PAPER)
    }
}