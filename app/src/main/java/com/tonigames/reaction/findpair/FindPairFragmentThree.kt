package com.tonigames.reaction.findpair

import android.animation.Animator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.ToggleButton
import com.tonigames.reaction.R
import kotlinx.android.synthetic.main.fragment_find_pair_three.*

private const val DURATION = 2500L

class FindPairFragmentThree : AbstractFindPairFragment(R.layout.fragment_find_pair_three) {

    override var seekBarAnimator: Animator? = null
    override var buttonLayoutMap: Map<Int, Pair<RelativeLayout, ImageButton>> = mapOf()
    override var gameOverListener: FindPairInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_pair_three, container, false)

        val ib1 = view.findViewById(R.id.imageBtn1) as ImageButton
        val ib2 = view.findViewById(R.id.imageBtn2) as ImageButton
        val ib3 = view.findViewById(R.id.imageBtn3) as ImageButton
        val ib4 = view.findViewById(R.id.imageBtn4) as ImageButton
        val ib5 = view.findViewById(R.id.imageBtn5) as ImageButton
        val ib6 = view.findViewById(R.id.imageBtn6) as ImageButton

        val layout1 = view.findViewById(R.id.relativeLayout1) as RelativeLayout
        val layout2 = view.findViewById(R.id.relativeLayout2) as RelativeLayout
        val layout3 = view.findViewById(R.id.relativeLayout3) as RelativeLayout
        val layout4 = view.findViewById(R.id.relativeLayout4) as RelativeLayout
        val layout5 = view.findViewById(R.id.relativeLayout5) as RelativeLayout
        val layout6 = view.findViewById(R.id.relativeLayout6) as RelativeLayout

        if (buttonLayoutMap.isNullOrEmpty()) {
            buttonLayoutMap = mapOf(
                R.id.toggleBtn1 to Pair(layout1, ib1),
                R.id.toggleBtn2 to Pair(layout2, ib2),
                R.id.toggleBtn3 to Pair(layout3, ib3),
                R.id.toggleBtn4 to Pair(layout4, ib4),
                R.id.toggleBtn5 to Pair(layout5, ib5),
                R.id.toggleBtn6 to Pair(layout6, ib6)
            )
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        tvRoundCnt?.run {
            setTextColor(Color.parseColor("#FFA07A"))
            text = paramRound.toString()
        }

        bindButtonListeners(
            listOf<ToggleButton>(
                toggleBtn1, toggleBtn2, toggleBtn3, toggleBtn4, toggleBtn5, toggleBtn6
            )
        )

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
            FindPairFragmentThree().apply {
                arguments = Bundle().apply {
                    putString(roundArgument, param1)
                    putString(extraArgument, param2)
                }
            }
    }
}
