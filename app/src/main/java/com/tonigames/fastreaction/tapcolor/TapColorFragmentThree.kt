package com.tonigames.fastreaction.tapcolor

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.tonigames.fastreaction.R
import kotlinx.android.synthetic.main.fragment_tap_color_three.*
import kotlinx.android.synthetic.main.fragment_tap_color_three.btnColor1
import kotlinx.android.synthetic.main.fragment_tap_color_three.btnColor2
import kotlinx.android.synthetic.main.fragment_tap_color_three.progressBar
import kotlinx.android.synthetic.main.fragment_tap_color_three.textViewColor

class TapColorFragmentThree : AbstractTapColorFragment(R.layout.fragment_tap_color_three) {

    override var mCountDownTimer: CountDownTimer? = null
    override var listener: FragmentInteractionListener? = null
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

        with(setColorsToButtons(mutableListOf(btnColor1, btnColor2, btnColor3))) {
            textViewColor.text =
                IColorFragment.Color.translatedName(
                    context!!.resources,
                    currentLanguage(),
                    this.first
                )

            correctColorButton = this
        }

        bindButtonListeners(listOf<Button>(btnColor1, btnColor2, btnColor3))

        //init ProgressBar with CountDownTimer
        with(
            initCountDownTimer(
                reduceDuration(1500L, paramRound),
                50L,
                activity,
                progressBar,
                listener
            )
        ) {
            mCountDownTimer = this
            start()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TapColorFragmentThree().apply {
                arguments = Bundle().apply {
                    putString(ARG_ROUND, param1)
                    putString(ARG_EXTRA, param2)
                }
            }
    }
}
