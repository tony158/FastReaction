package com.tonigames.reaction.tapcolor

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.tonigames.reaction.R
import com.tonigames.reaction.common.GameFinishListener
import kotlinx.android.synthetic.main.fragment_tap_color_three.*
import kotlinx.android.synthetic.main.fragment_tap_color_three.btnColor1
import kotlinx.android.synthetic.main.fragment_tap_color_three.btnColor2
import kotlinx.android.synthetic.main.fragment_tap_color_three.progressBar
import kotlinx.android.synthetic.main.fragment_tap_color_three.textViewColor

private const val DURATION = 1550L

class TapColorFragmentThree : AbstractTapColorFragment(R.layout.fragment_tap_color_three) {

    override var seekBarAnimator: Animator? = null
    override var gameOverListener: GameFinishListener? = null
    override var correctColorButton: Pair<IColorFragment.Color?, Button>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tap_color_three, container, false)
    }

    override fun onResume() {
        super.onResume()
        tvRoundCnt?.run { text = paramRound.toString() }

        setColorsToButtons(mutableListOf(btnColor1, btnColor2, btnColor3))
            .run {
                textViewColor.text =
                    IColorFragment.Color.translatedName(
                        context!!.resources,
                        currentLanguage(),
                        this.first
                    )

                correctColorButton = this
            }

        bindButtonListeners(listOf<Button>(btnColor1, btnColor2, btnColor3))

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
            TapColorFragmentThree().apply {
                arguments = Bundle().apply {
                    putString(roundArgument, param1)
                    putString(extraArgument, param2)
                }
            }
    }
}
