package com.tonigames.reaction.anagram

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.ToggleButton
import com.tonigames.reaction.R
import com.tonigames.reaction.common.GameFinishListener
import kotlinx.android.synthetic.main.fragment_anagram_three.*

private const val DURATION = 3100L

class AnagramFragmentThree : AbstractAnagramFragment(R.layout.fragment_anagram_three) {

    override var seekBarAnimator: Animator? = null
    override var gameOverListener: GameFinishListener? = null

    override var quizImageBtnList: List<ImageButton> = listOf()
    override var ansToggleToImgMap: Map<ToggleButton, Pair<RelativeLayout, Set<ImageButton>>> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_anagram_three, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizImageBtnList = listOf(quizImgBtn1, quizImgBtn2, quizImgBtn3)

        ansToggleToImgMap = mapOf(
            toggleBtn1 to Pair(layoutRow1, setOf(imgAns11, imgAns12, imgAns13)),
            toggleBtn2 to Pair(layoutRow2, setOf(imgAns21, imgAns22, imgAns23)),
            toggleBtn3 to Pair(layoutRow3, setOf(imgAns31, imgAns32, imgAns33))
        )
    }

    override fun initWrongAnsRows(correctAnsToggle: ToggleButton, quizImages: List<Int>, restOfImages: List<Int>) {

        val restAnsToggles = (ansToggleToImgMap.keys subtract setOf(correctAnsToggle)).toList().shuffled()

        val wrongAnsRow1 = ansToggleToImgMap.getValue(restAnsToggles[0]).second.toList()
        val wrongAnsRow2 = ansToggleToImgMap.getValue(restAnsToggles[1]).second.toList()

        wrongAnsRow1[0].setImageResource(quizImages[0])
        wrongAnsRow1[1].setImageResource(restOfImages.random()!!)
        wrongAnsRow1[2].setImageResource(restOfImages.random()!!)

        wrongAnsRow2[0].setImageResource(quizImages[0])
        wrongAnsRow2[1].setImageResource(quizImages[1])
        wrongAnsRow2[2].setImageResource(restOfImages.random()!!)
    }

    override fun onResume() {
        super.onResume()
        tvRoundCnt?.run { text = paramRound.toString() }

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
        fun newInstance(param1: String, param2: String) = AnagramFragmentThree()
            .apply {
                arguments = Bundle().apply {
                    putString(roundArgument, param1)
                    putString(extraArgument, param2)
                }
            }
    }
}