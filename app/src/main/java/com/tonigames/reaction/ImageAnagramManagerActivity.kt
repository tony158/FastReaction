package com.tonigames.reaction

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.reaction.anagram.AbstractAnagramFragment
import com.tonigames.reaction.anagram.AnagramFragmentThree
import com.tonigames.reaction.anagram.AnagramFragmentTwo
import com.tonigames.reaction.common.GameFinishListener
import kotlin.random.Random

class ImageAnagramManagerActivity : AbstractGameManagerActivity(), GameFinishListener {
    private var mCurrFragment: AbstractAnagramFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCurrFragment = when (Random.nextInt(0, 100) % 2) {
            0 -> AnagramFragmentThree.newInstance(mRoundCnt.toString(), "Three")
            else -> AnagramFragmentTwo.newInstance(mRoundCnt.toString(), "Two")
        }.also {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, it)
                .commit()
        }
    }

    override fun onCorrectAnswer() {
        mDialogPopup?.takeIf { it.isShowing }?.run { return@onCorrectAnswer }

        showSuccessToast()

        soundPositive?.takeIf { it.isPlaying }?.stop()
        soundPositive?.start()

        ++mRoundCnt

        mCurrFragment = when (Random.nextInt(0, 100) % 2) {
            0 -> AnagramFragmentThree.newInstance(mRoundCnt.toString(), "Three")
            else -> AnagramFragmentTwo.newInstance(mRoundCnt.toString(), "Two")
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
            configGameOverDialog(this, msg, mRoundCnt, getHighScore(Constants.HIGH_SCORE_IMAGE_ANAGRAM))

            findViewById<Button>(R.id.btnGoHome).setOnClickListener { theButton ->
                YoYo.with(Techniques.Pulse).duration(200).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            soundBtnClick?.start()

                            Intent(
                                this@ImageAnagramManagerActivity,
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

        saveHighScore(mRoundCnt, Constants.HIGH_SCORE_IMAGE_ANAGRAM)
    }

    override fun handleContinueClicked() {
        mCurrFragment?.clearAllToggles()

        super.handleContinueClicked()
    }
}