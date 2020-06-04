package com.tonigames.reaction.leftorright

import com.tonigames.reaction.R

interface ILeftOrRight {

    fun randomImage(): Int {
        return allDrawables.shuffled().take(1)[0]
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