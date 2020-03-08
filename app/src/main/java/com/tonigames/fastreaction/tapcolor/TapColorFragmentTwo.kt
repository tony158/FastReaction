package com.tonigames.fastreaction.tapcolor

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.tonigames.fastreaction.R
import kotlinx.android.synthetic.main.fragment_tap_color_two.*

class TapColorFragmentTwo : AbstractTapColorFragment(R.layout.fragment_tap_color_two) {

    override var mCountDownTimer: CountDownTimer? = null
    override var listener: FragmentInteractionListener? = null
    override var correctColorButton: Pair<IColorFragment.Color?, Button>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tap_color_two, container, false)
    }

    override fun onResume() {
        super.onResume()

        with(setColorsToButtons(mutableListOf(btnColor1, btnColor2))) {
            textViewColor.text =
                IColorFragment.Color.translatedName(
                    context!!.resources,
                    currentLanguage(),
                    this.first
                )

            correctColorButton = this
        }

        bindButtonListeners(listOf<Button>(btnColor1, btnColor2))

        //init ProgressBar with CountDownTimer
        with(
            initCountDownTimer(
                reduceDuration(1400L, paramRound),
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
            TapColorFragmentTwo().apply {
                arguments = Bundle().apply {
                    putString(ARG_ROUND, param1)
                    putString(ARG_EXTRA, param2)
                }
            }
    }
}
