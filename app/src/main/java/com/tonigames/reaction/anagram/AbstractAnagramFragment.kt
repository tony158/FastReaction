package com.tonigames.reaction.anagram

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
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
    private val checkedToggles: MutableList<ToggleButton> = mutableListOf()
    abstract var gameOverListener: AnswerSelectListener?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramRound = it.getString(roundArgument)?.toInt() ?: 0
            paramExtra = it.getString(extraArgument)
        }
    }

    override fun onResume() {
        super.onResume()

        checkedToggles.forEach { if (it.isChecked) it.isChecked = false }   //uncheck all
        checkedToggles.clear()
    }

    fun clearAllToggles() = checkedToggles.run {
        this.forEach { it.isChecked = false }
        this.clear()
    }

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