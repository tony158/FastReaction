package com.tonigames.reaction

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.jeevandeshmukh.glidetoastlib.GlideToast.*
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.HIGH_SCORE_FIND_PAIR
import com.tonigames.reaction.findpair.*

class FindPairGameManagerActivity : AbstractGameManagerActivity(), FindPairInteractionListener {

    private var mCurrFragment: AbstractFindPairFragment? = null

    companion object {
        val RoundImgRowCount: Map<Int, Int> =
            mapOf(1 to 2, 2 to 2, 3 to 2, 4 to 2, 5 to 3, 6 to 3, 7 to 3, 8 to 4, 9 to 4, 10 to 4)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imgRowCnt = RoundImgRowCount.getOrDefault(
            if (mRoundCnt % RoundImgRowCount.size == 0) 1 else mRoundCnt % RoundImgRowCount.size,
            2
        )

        mCurrFragment = when (imgRowCnt) {
            4 -> FindPairFragmentFour.newInstance(mRoundCnt.toString(), "")
            3 -> FindPairFragmentThree.newInstance(mRoundCnt.toString(), "")
            else -> FindPairFragmentTwo.newInstance(mRoundCnt.toString(), "")
        }.also {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, it)
                .commit()
        }
    }

    override fun onCorrectPairSelected() {
        mDialogPopup?.takeIf { it.isShowing }?.run { return@onCorrectPairSelected }

        makeToast(this, "Correct!!", LENGTHMEDIUM, SUCCESSTOAST, BOTTOM).show()

        soundPositive?.takeIf { it.isPlaying }?.stop()
        soundPositive?.start()

        ++mRoundCnt

        val imgRowCnt = RoundImgRowCount.getOrDefault(
            if (mRoundCnt % RoundImgRowCount.size == 0) 1 else mRoundCnt % RoundImgRowCount.size,
            2
        )

        mCurrFragment = when (imgRowCnt) {
            4 -> FindPairFragmentFour.newInstance(mRoundCnt.toString(), "")
            3 -> FindPairFragmentThree.newInstance(mRoundCnt.toString(), "")
            else -> FindPairFragmentTwo.newInstance(mRoundCnt.toString(), "")
        }.also {
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
            cancelable(false)
            cancelOnTouchOutside(false)
            cornerRadius(8f)
            findViewById<TextView>(R.id.title).text = msg
            findViewById<TextView>(R.id.scoreGameOver).text = mRoundCnt.toString()
            findViewById<TextView>(R.id.highScoreGameOver).text =
                getHighScore(HIGH_SCORE_FIND_PAIR).toString()

            findViewById<Button>(R.id.btnGoHome).setOnClickListener { theButton ->
                YoYo.with(Techniques.Pulse).duration(200).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            soundBtnClick?.start()

                            Intent(this@FindPairGameManagerActivity, MainMenuActivity::class.java).run {
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

        saveHighScore(mRoundCnt, HIGH_SCORE_FIND_PAIR)
    }

    override fun handleContinueClicked() {
        mCurrFragment?.clearAllToggles()

        super.handleContinueClicked()
    }
}
