package com.tonigames.reaction.leftorright

import com.tonigames.reaction.R
import java.util.*

enum class ViewOutState(val state: Int) {
    Invalid(0),
    LeftOut(1),
    Inside(2),
    RightOut(3)
}

interface LeftRightResultListener {
    fun onResult(lastImg: Int, state: ViewOutState)

    fun onTimeUp()
}

interface ILeftOrRight {
    fun <E> List<E>.random(): E? = if (size > 0) get(Random().nextInt(size)) else null

    fun randomImage(): Int {
        return allDrawables.random() ?: allDrawables[0]
    }

    companion object {
        val allDrawables: List<Int> = listOf(
            R.drawable.acorn,
            R.drawable.aircraft,
            R.drawable.allterrain,
            R.drawable.amazon,
            R.drawable.ameracan_football,
            R.drawable.american_football,
            R.drawable.android,
            R.drawable.ant
        )
    }
}