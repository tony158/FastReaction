package com.tonigames.reaction.anagram

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import android.widget.ToggleButton
import androidx.core.animation.doOnEnd
import com.tonigames.reaction.findpair.AnswerSelectListener
import com.tonigames.reaction.findpair.IImageFragment

abstract class AbstractAnagramFragment(contentLayoutId: Int) : Fragment(contentLayoutId), IImageFragment {

    protected val roundArgument: String = "Round"
    protected val extraArgument: String = "Extra"

    var paramRound: Int = 0
    var paramExtra: String? = null

    abstract var seekBarAnimator: Animator?
    abstract var gameOverListener: AnswerSelectListener?

    abstract var quizImageBtnList: List<ImageButton>
    abstract var ansToggleToImgMap: Map<ToggleButton, Set<ImageButton>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramRound = it.getString(roundArgument)?.toInt() ?: 0
            paramExtra = it.getString(extraArgument)
        }
    }

    override fun onResume() {
        super.onResume()

        ansToggleToImgMap.keys.forEach { it.isChecked = false }   //uncheck all

        initImages()
        bindToggleListeners()
    }

    abstract fun initImages()

    private fun bindToggleListeners() {
        ansToggleToImgMap.keys.forEach { toggle ->
            toggle.setOnCheckedChangeListener { selected, _ ->
                seekBarAnimator?.pause()

                val selectedImgTags = ansToggleToImgMap.getOrDefault((selected as ToggleButton), setOf()).map { it.tag }.toSet()
                val quizImgTags = quizImageBtnList.map { it.tag }.toSet()
                val diff = selectedImgTags subtract quizImgTags

                ansToggleToImgMap.keys.forEach { if (it != selected) it.isChecked = false } // uncheck the others

                if (diff.isNullOrEmpty()) {
                    gameOverListener?.onCorrectPairSelected()
                } else {
                    gameOverListener?.onFailedToSolve("Wrong selection!")
                }
            }
        }
    }

    fun clearAllToggles() = ansToggleToImgMap.keys.forEach { it.isChecked = false }

    fun initSeekBarAnimator(
        animationTime: Long,
        progressBar: SeekBar? = null,
        onFinishListener: AnswerSelectListener? = null
    ): Animator {
        progressBar?.max = 100
        progressBar?.progress = 0

        return ValueAnimator.ofInt(0, 100).apply {
            duration = animationTime
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                (animation.animatedValue as Int).also { progressBar?.progress = it }
            }

            doOnEnd {
                progressBar?.progress = 100
                try {
                    onFinishListener?.onFailedToSolve("Time's up")
                } catch (e: Exception) {
                    Log.d("AbstractAnagramFragment", e.message ?: "exception")
                }
            }
        }
    }
}