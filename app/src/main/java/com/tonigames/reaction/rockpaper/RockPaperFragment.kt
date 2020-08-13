package com.tonigames.reaction.rockpaper

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.*
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.reaction.DefaultAnimatorListener
import com.tonigames.reaction.R
import com.tonigames.reaction.rockpaper.IRockPaper.Companion.answerCheckMap
import kotlinx.android.synthetic.main.fragment_rock_paper.*
import kotlinx.android.synthetic.main.fragment_rock_paper.progressBar
import kotlinx.android.synthetic.main.fragment_rock_paper.tvRoundCnt

private const val DURATION = 3300L

class RockPaperFragment : Fragment(R.layout.fragment_rock_paper), IRockPaper {

    private var seekBarAnimator: Animator? = null
    private var mRoundCnt: Int = Int.MIN_VALUE
    private var mLastImg: Int = Int.MIN_VALUE
    private var buttonLayoutMap: Map<Int, ImageButton> = mapOf()
    private var gameOverListener: ResultListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rock_paper, container, false)

        val quiz = view.findViewById(R.id.imageBtnAnsQuestion) as ImageButton
        val answer1 = view.findViewById(R.id.imageBtnAns1) as ImageButton
        val answer2 = view.findViewById(R.id.imageBtnAns2) as ImageButton
        val answer3 = view.findViewById(R.id.imageBtnAns3) as ImageButton

        val randomImg = IRockPaper.allDrawables.random()
        quiz.setImageResource(randomImg!!)
        quiz.tag = randomImg

        val shuffles = IRockPaper.allDrawables.shuffled()
        answer1.setImageResource(shuffles[0])
        answer1.tag = shuffles[0]
        answer2.setImageResource(shuffles[1])
        answer2.tag = shuffles[1]
        answer3.setImageResource(shuffles[2])
        answer3.tag = shuffles[2]

        if (buttonLayoutMap.isNullOrEmpty()) {
            buttonLayoutMap = mapOf(
                R.id.toggleBtnAns1 to answer1,
                R.id.toggleBtnAns2 to answer2,
                R.id.toggleBtnAns3 to answer3
            )
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        tvRoundCnt?.run { text = mRoundCnt.toString() }

        bindButtonListeners(listOf<ToggleButton>(toggleBtnAns1, toggleBtnAns2, toggleBtnAns3))

        seekBarAnimator = initSeekBarAnimator(
            reduceDuration(DURATION, mRoundCnt), progressBar, gameOverListener
        ).also {
            it.start()
        }
    }

    private fun bindButtonListeners(buttons: List<Button>) {
        buttons.forEach(fun(button: Button) {
            button.setOnClickListener(fun(theButton: View) {
                YoYo.with(Techniques.Pulse).duration(100).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            try {
                                seekBarAnimator?.pause()

                                val selectedImg = buttonLayoutMap[theButton.id]?.tag ?: ""

                                if (imageBtnAnsQuestion.tag == answerCheckMap[selectedImg]) {
                                    gameOverListener?.onCorrectSelection()
                                } else {
                                    gameOverListener?.onFailedToSolve("Wrong answer")
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

    private fun initSeekBarAnimator(
        animationTime: Long,
        progressBar: SeekBar? = null,
        resultListener: ResultListener? = null
    ): Animator {
        progressBar?.max = 100
        progressBar?.progress = 0

        return ValueAnimator.ofInt(0, 100).apply {
            duration = animationTime
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                (animation.animatedValue as Int).also { progressBar?.progress = it }
            }

            addListener(object : DefaultAnimatorListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    try {
                        progressBar?.progress = 100
                        resultListener?.onFailedToSolve("Time's up!")
                    } catch (e: Exception) {
                        Log.wtf("RockPaperFragment", e.message ?: "exception")
                    }
                }
            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ResultListener) {
            gameOverListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        gameOverListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RockPaperFragment().apply {
                arguments = Bundle().apply {
                    mRoundCnt = param1.toInt()
                    mLastImg = param2.toInt()
                }
            }
    }
}