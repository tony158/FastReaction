package com.tonigames.reaction.leftorright

import com.tonigames.reaction.R
import com.tonigames.reaction.common.GameFinishListener
import com.tonigames.reaction.common.ISeekBar
import java.util.*

enum class ViewOutState(val state: Int) {
    Invalid(0),
    LeftOut(1),
    Inside(2),
    RightOut(3)
}

interface ResultListener : GameFinishListener {
    fun onResult(lastImg: Int, state: ViewOutState)
}

interface ILeftOrRight : ISeekBar {
    fun <E> List<E>.random(): E? = if (size > 0) get(Random().nextInt(size)) else null

    fun randomImage(): Int {
        return allDrawables.random() ?: allDrawables[0]
    }

    fun reduceDuration(duration: Long, roundCnt: Int?): Long {
        return (roundCnt ?: 0).run {
            when {
                this > 16 -> (duration * 0.92).toLong()
                this > 32 -> (duration * 0.84).toLong()
                else -> duration
            }
        }
    }

    companion object {
        val allDrawables: List<Int> = listOf(
            R.drawable.leftright_circle,
            R.drawable.leftright_diamond,
            R.drawable.leftright_heart,
            R.drawable.leftright_star,
            R.drawable.leftright_triangle,
            R.drawable.leftright_square
        )
    }
}