package com.tonigames.reaction.anagram

import android.animation.Animator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tonigames.reaction.R
import com.tonigames.reaction.findpair.AnswerSelectListener
import kotlinx.android.synthetic.main.fragment_find_pair_two.*

private const val DURATION = 2200L

class AnagramFragmentTwo() : AbstractAnagramFragment(R.layout.fragment_anagram_two) {

    override var seekBarAnimator: Animator? = null
    override var gameOverListener: AnswerSelectListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_anagram_two, container, false)

        return view
    }

    override fun onResume() {
        super.onResume()
        tvRoundCnt?.run {
            setTextColor(Color.parseColor("#FFA07A"))
            text = paramRound.toString()
        }

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
        fun newInstance(param1: String, param2: String) = AnagramFragmentTwo()
            .apply {
                arguments = Bundle().apply {
                    putString(roundArgument, param1)
                    putString(extraArgument, param2)
                }
            }
    }
}