package com.tonigames.reaction.tapcolor

import android.animation.Animator
import android.content.Context
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.reaction.Constants.Companion.SELECTED_LANGUAGE
import com.tonigames.reaction.DefaultAnimatorListener
import com.tonigames.reaction.common.AnswerSelectListener
import com.tonigames.reaction.common.ISeekBar
import com.tonigames.reaction.findpair.WRONG_SELECTION_MSG
import com.tonigames.reaction.popups.MyLanguageEnum

abstract class AbstractTapColorFragment(contentLayoutId: Int) : Fragment(contentLayoutId),
    IColorFragment, ISeekBar {

    protected val roundArgument = "Round"
    protected val extraArgument = "Extra"

    var paramRound: Int = 0
    var paramExtra: String? = null

    abstract var seekBarAnimator: Animator?
    abstract var gameOverListener: AnswerSelectListener?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramRound = it.getString(roundArgument)?.toInt() ?: 0
            paramExtra = it.getString(extraArgument)
        }
    }

    fun bindButtonListeners(buttons: List<Button>) {
        buttons.forEach(fun(button: Button) {
            button.setOnClickListener(fun(theButton: View) {
                YoYo.with(Techniques.Pulse).duration(100).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            try {
                                seekBarAnimator?.pause()

                                if (theButton == correctColorButton?.second) {
                                    gameOverListener?.onCorrectAnswer()
                                } else {
                                    gameOverListener?.onFailedToSolve(WRONG_SELECTION_MSG)
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

        val colorList = generateColor(buttonList.size).toMutableList().also { it.shuffle() }
        val selectedColor: IColorFragment.Color = colorList.removeAt(colorList.size - 1)
        val selectedButton =
            buttonList.removeAt(buttonList.size - 1).apply { setBackgroundColor(selectedColor.cc) }

        for ((index, button) in buttonList.withIndex()) {
            button.setBackgroundColor(colorList[index].cc)
        }

        return Pair(selectedColor, selectedButton)
    }

    /** get the current language setting*/
    fun currentLanguage(): MyLanguageEnum {
        val languageIndex = context?.getSharedPreferences(
            SELECTED_LANGUAGE,
            Context.MODE_PRIVATE
        )?.getInt(SELECTED_LANGUAGE, 0) ?: MyLanguageEnum.English.ordinal

        return MyLanguageEnum.fromIndex(languageIndex)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is AnswerSelectListener) {
            gameOverListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        gameOverListener = null
    }
}