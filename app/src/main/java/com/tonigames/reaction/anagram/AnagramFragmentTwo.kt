package com.tonigames.reaction.anagram

import android.animation.Animator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ToggleButton
import com.tonigames.reaction.R
import com.tonigames.reaction.findpair.AnswerSelectListener
import com.tonigames.reaction.findpair.IImageFragment
import kotlinx.android.synthetic.main.fragment_anagram_two.*

private const val DURATION = 2200L

class AnagramFragmentTwo() : AbstractAnagramFragment(R.layout.fragment_anagram_two) {

    override var seekBarAnimator: Animator? = null
    override var gameOverListener: AnswerSelectListener? = null

    override var quizImageBtnList: List<ImageButton> = listOf()
    override var ansToggleToImgMap: Map<ToggleButton, Set<ImageButton>> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_anagram_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizImageBtnList = listOf(quizImgBtn1, quizImgBtn2)

        ansToggleToImgMap = mapOf(
            toggleBtn1 to setOf(imgAns11, imgAns12),
            toggleBtn2 to setOf(imgAns21, imgAns22),
            toggleBtn3 to setOf(imgAns31, imgAns32),
            toggleBtn4 to setOf(imgAns41, imgAns42)
        )
    }

    override fun initImages() {
        val drawables: List<Int> = IImageFragment.allDrawables.shuffled()
        val quizImages = drawables.subList(0, quizImageBtnList.size)
        val restOfImages = drawables.subList(quizImages.size, drawables.size)

        quizImageBtnList.forEachIndexed { idx, imgBtn ->
            imgBtn.setImageResource(quizImages[idx])
            imgBtn.tag = quizImages[idx]
        }

        quizImages.shuffled()
        val correctAnsToggle = ansToggleToImgMap.keys.toList().random()!!

        ansToggleToImgMap.getValue(correctAnsToggle).shuffled().forEachIndexed { idx, imageBtn ->
            imageBtn.setImageResource(quizImages[idx])
            imageBtn.tag = quizImages[idx]
        }

        initWrongAnsRows(correctAnsToggle, quizImages, restOfImages)
    }

    private fun initWrongAnsRows(correctAnsToggle: ToggleButton, quizImages: List<Int>, restOfImages: List<Int>) {
        val restAnsToggles = (ansToggleToImgMap.keys subtract setOf(correctAnsToggle)).toList().shuffled()

        val wrongAnsRow1 = ansToggleToImgMap.getValue(restAnsToggles[0]).toList()
        val wrongAnsRow2 = ansToggleToImgMap.getValue(restAnsToggles[1]).toList()
        val wrongAnsRow3 = ansToggleToImgMap.getValue(restAnsToggles[2]).toList()

        wrongAnsRow1[0].setImageResource(quizImages[0])
        wrongAnsRow1[1].setImageResource(restOfImages.random()!!)

        wrongAnsRow2[0].setImageResource(quizImages[1])
        wrongAnsRow2[1].setImageResource(restOfImages.random()!!)

        wrongAnsRow3[0].setImageResource(restOfImages.random()!!)
        wrongAnsRow3[1].setImageResource(restOfImages.random()!!)
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