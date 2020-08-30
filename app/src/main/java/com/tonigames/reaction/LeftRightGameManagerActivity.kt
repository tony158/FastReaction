package com.tonigames.reaction

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.jeevandeshmukh.glidetoastlib.GlideToast
import com.tonigames.reaction.Constants.Companion.HIGH_SCORE_LEFT_RIGHT
import com.tonigames.reaction.leftorright.LeftOrRightFragment
import com.tonigames.reaction.leftorright.ResultListener
import com.tonigames.reaction.leftorright.ViewOutState

class LeftRightGameManagerActivity : AbstractGameManagerActivity(), ResultListener {

    private var mCurrFragment: LeftOrRightFragment? = null

    private var mLastState: ViewOutState = ViewOutState.Invalid
    private var mLastImg: Int = Int.MIN_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mRoundCnt = -1
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

        mRoundCnt = -1
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

    override fun onResume() {
        super.onResume()

        mRoundCnt = -1
    }

    override fun onStop() {
        super.onStop()

        mRoundCnt = -1
        mLastImg = Int.MIN_VALUE
        mLastState = ViewOutState.Invalid
    }

    override fun onDestroy() {
        super.onDestroy()

        mRoundCnt = -1
        mLastImg = Int.MIN_VALUE
        mLastState = ViewOutState.Invalid
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

    override fun onCorrectAnswer() {
        // nothing to do here, "onResult()" handle result check
    }

    override fun onFailedToSolve(msg: String) {
        onFailure("Time's up!")
        mLastState = ViewOutState.Invalid
    }

    private fun onFailure(msg: String = "") {
        mDialogPopup?.takeIf { it.isShowing }?.run { return@onFailure }

        soundNegative?.start()
        vibrate()

        mDialogPopup = MaterialDialog(this).customView(R.layout.game_over_popup).show {
            configGameOverDialog(this, msg, mRoundCnt, getHighScore(HIGH_SCORE_LEFT_RIGHT))

            findViewById<Button>(R.id.btnGoHome).setOnClickListener { theButton ->
                YoYo.with(Techniques.Pulse).duration(200).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            soundBtnClick?.start()

                            Intent(
                                this@LeftRightGameManagerActivity,
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
            showSuccessToast()

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
