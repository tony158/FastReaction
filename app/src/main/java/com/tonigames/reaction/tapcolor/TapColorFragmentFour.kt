package com.tonigames.reaction.tapcolor

import android.animation.Animator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.tonigames.reaction.R
import com.tonigames.reaction.common.GameFinishListener
import kotlinx.android.synthetic.main.fragment_tap_color_four.*
import kotlinx.android.synthetic.main.fragment_tap_color_four.btnColor1
import kotlinx.android.synthetic.main.fragment_tap_color_four.btnColor2
import kotlinx.android.synthetic.main.fragment_tap_color_four.progressBar
import kotlinx.android.synthetic.main.fragment_tap_color_four.textViewColor

private const val DURATION = 1650L

class TapColorFragmentFour : AbstractTapColorFragment(R.layout.fragment_tap_color_four) {

    override var seekBarAnimator: Animator? = null
    override var gameOverListener: GameFinishListener? = null
    override var correctColorButton: Pair<IColorFragment.Color?, Button>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tap_color_four, container, false)
    }

    override fun onResume() {
        super.onResume()
        tvRoundCnt?.run {
            setTextColor(Color.parseColor("#00574B"))
            text = paramRound.toString()
        }

        setColorsToButtons(mutableListOf(btnColor1, btnColor2, btnColor3, btnColor4))
            .run {
                textViewColor.text =
                    IColorFragment.Color.translatedName(
                        context!!.resources,
                        currentLanguage(),
                        this.first
                    )

                correctColorButton = this
            }

        bindButtonListeners(listOf<Button>(btnColor1, btnColor2, btnColor3, btnColor4))

        seekBarAnimator = initSeekBarAnimator(
            reduceDuration(DURATION, paramRound),
            progressBar,
            gameOverListener
        ).also {
            it.start()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TapColorFragmentFour().apply {
                arguments = Bundle().apply {
                    putString(roundArgument, param1)
                    putString(extraArgument, param2)
                }
            }
    }
}
