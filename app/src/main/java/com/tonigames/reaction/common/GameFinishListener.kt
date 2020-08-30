package com.tonigames.reaction.common

interface GameFinishListener {
    fun onCorrectAnswer()

    // wrong answer or time's up
    fun onFailedToSolve(msg: String = "")
}
