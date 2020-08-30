package com.tonigames.reaction.common

interface AnswerSelectListener {
    fun onCorrectAnswer()

    // wrong answer or time's up
    fun onFailedToSolve(msg: String = "")
}
