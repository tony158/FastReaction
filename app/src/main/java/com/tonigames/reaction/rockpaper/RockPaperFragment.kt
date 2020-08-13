package com.tonigames.reaction.rockpaper

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.ToggleButton
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.reaction.DefaultAnimatorListener
import com.tonigames.reaction.R
import com.tonigames.reaction.leftorright.ResultListener
import kotlinx.android.synthetic.main.fragment_find_pair_two.*
import kotlinx.android.synthetic.main.fragment_rock_paper.*
import kotlinx.android.synthetic.main.fragment_tap_color_two.*
import kotlinx.android.synthetic.main.fragment_tap_color_two.progressBar
import kotlinx.android.synthetic.main.fragment_tap_color_two.tvRoundCnt

private const val DURATION = 1300L

class RockPaperFragment : Fragment(R.layout.fragment_rock_paper), IRockPaper {

    private var seekBarAnimator: Animator? = null
    private var mRoundCnt: Int = Int.MIN_VALUE
    private var mLastImg: Int = Int.MIN_VALUE

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
        quiz.setImageResource(randomImage())
        quiz.tag = randomImg

        val shuffles = IRockPaper.allDrawables.shuffled()
        answer1.setImageResource(shuffles[0])
        answer1.tag = shuffles[0]
        answer2.setImageResource(shuffles[1])
        answer2.tag = shuffles[1]
        answer3.setImageResource(shuffles[2])
        answer3.tag = shuffles[2]

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

                                //  gameOverListener?.onCorrectColorSelected()
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
                        resultListener?.onTimeUp()
                    } catch (e: Exception) {
                        Log.wtf("RockPaperFragment", e.message ?: "exception")
                    }
                }
            })
        }
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