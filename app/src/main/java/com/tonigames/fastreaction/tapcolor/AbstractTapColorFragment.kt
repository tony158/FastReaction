package com.tonigames.fastreaction.tapcolor

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.fastreaction.DefaultAnimatorListener
import com.tonigames.fastreaction.MainMenuActivity
import com.tonigames.fastreaction.popups.MyLanguageEnum


abstract class AbstractTapColorFragment(contentLayoutId: Int) : Fragment(contentLayoutId),
    IColorFragment {

    protected val ARG_ROUND = "Round"
    protected val ARG_EXTRA = "Extra"

    var paramRound: Int = 0
    var paramExtra: String? = null

    abstract var mCountDownTimer: CountDownTimer?
    abstract var gameOverListener: FragmentInteractionListener?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramRound = it.getString(ARG_ROUND)?.toInt() ?: 0
            paramExtra = it.getString(ARG_EXTRA)
        }
    }

    fun bindButtonListeners(buttons: List<Button>) {
        buttons.forEach(fun(button: Button) {
            button.setOnClickListener(fun(theButton: View) {
                YoYo.with(Techniques.Pulse).duration(100).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            try {
                                activity?.let {
                                    synchronized(it) {
                                        mCountDownTimer?.cancel()

                                        if (theButton == correctColorButton?.second) {
                                            gameOverListener?.onCorrectColorSelected()
                                        } else {
                                            gameOverListener?.onFailedToSolve("Wrong answer")
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.d("onAnimationEnd", e.toString())
                            }
                        }
                    }
                ).playOn(theButton)
            })
        })
    }

    fun setColorsToButtons(buttonList: MutableList<Button>): Pair<IColorFragment.Color, Button> {
        buttonList.shuffle()

        val colorList = generateColor(buttonList.size).toMutableList()
        colorList.shuffle()

        val selectedColor: IColorFragment.Color = colorList.removeAt(colorList.size - 1)

        val selectedButton = buttonList.removeAt(buttonList.size - 1)
        selectedButton.setBackgroundColor(selectedColor.cc)

        for ((index, button) in buttonList.withIndex()) {
            button.setBackgroundColor(colorList[index].cc)
        }

        return Pair(selectedColor, selectedButton)
    }

    fun initCountDownTimer(
        duration: Long,
        interval: Long,
        syncLocker: Activity?,
        progressBar: SeekBar? = null,
        onFinishListener: FragmentInteractionListener? = null
    ): CountDownTimer {

        progressBar?.max = 100
        progressBar?.progress = 0

        return object : CountDownTimer(duration, interval) {
            var i = 0

            override fun onTick(millisUntilFinished: Long) {
                syncLocker?.let {
                    synchronized(it) {
                        i++
                        progressBar?.progress = i * 100 / (duration.toInt() / interval.toInt())
                    }
                }
            }

            override fun onFinish() {
                syncLocker?.let {
                    synchronized(it) {
                        progressBar?.progress = 100

                        try {
                            onFinishListener?.onFailedToSolve("Time's up")
                        } catch (e: Exception) {
                            Log.d(
                                "AbstractFindPairFragment",
                                "onFinishListener?.onFailedToSolve exception"
                            )
                        }
                    }
                }
            }
        }
    }

    /** get the current language setting*/
    fun currentLanguage(): MyLanguageEnum {
        val languageIndex = context?.getSharedPreferences(
            MainMenuActivity.Constants.SELECTED_LANGUAGE,
            Context.MODE_PRIVATE
        )?.getInt(MainMenuActivity.Constants.SELECTED_LANGUAGE, 0) ?: MyLanguageEnum.English.ordinal

        return MyLanguageEnum.fromIndex(languageIndex)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is FragmentInteractionListener) {
            gameOverListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        mCountDownTimer?.cancel()
        gameOverListener = null
    }
}