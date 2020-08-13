package com.tonigames.reaction.rockpaper

import com.tonigames.reaction.R
import java.util.*

interface ResultListener {
    fun onCorrectSelection()

    // wrong answer or time's up
    fun onFailedToSolve(msg: String = "")
}

interface IRockPaper {
    fun <E> List<E>.random(): E? = if (size > 0) get(Random().nextInt(size)) else null

    fun randomImage(): Int {
        return allDrawables.random() ?: allDrawables[0]
    }

    fun reduceDuration(duration: Long, roundCnt: Int?): Long {
        return (roundCnt ?: 0).run {
            when {
                this > 20 -> (duration * 0.9).toLong()
                this > 40 -> (duration * 0.85).toLong()
                else -> duration
            }
        }
    }

    companion object {
        val allDrawables: List<Int> = listOf(
            R.drawable.menu_hand_rock_l,
            R.drawable.menu_hand_scissor_l,
            R.drawable.menu_hand_paper_l
        )

        val answerCheckMap = mapOf(
            R.drawable.menu_hand_rock_l to R.drawable.menu_hand_scissor_l,
            R.drawable.menu_hand_scissor_l to R.drawable.menu_hand_paper_l,
            R.drawable.menu_hand_paper_l to R.drawable.menu_hand_rock_l
        )
    }
}