package com.tonigames.reaction.common

import android.animation.Animator
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.core.animation.doOnEnd

interface ISeekBar {
    fun reduceDuration(duration: Long, roundCnt: Int): Long {
        return when {
            roundCnt > 16 -> (duration * 0.92).toLong()
            roundCnt > 32 -> (duration * 0.84).toLong()
            else -> duration
        }
    }

    fun initSeekBarAnimator(
        animationTime: Long,
        progressBar: SeekBar? = null,
        answerSelectListener: AnswerSelectListener? = null
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
                    answerSelectListener?.onFailedToSolve("Time's up")
                } catch (e: Exception) {
                    Log.d("ISeekBar", e.message ?: "exception")
                }
            }
        }
    }
}