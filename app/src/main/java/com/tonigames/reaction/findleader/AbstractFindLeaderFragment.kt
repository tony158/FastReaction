package com.tonigames.reaction.findleader

import android.animation.Animator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tonigames.reaction.R
import com.tonigames.reaction.common.GameFinishListener
import com.tonigames.reaction.common.ISeekBar

abstract class AbstractFindLeaderFragment(contentLayoutId: Int) : Fragment(contentLayoutId), ISeekBar {
    val roundArgument: String = "Round"
    val extraArgument: String = "Extra"

    var paramRound: Int = 0
    var paramExtra: String? = null

    abstract var seekBarAnimator: Animator?
    abstract var gameOverListener: GameFinishListener?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            paramRound = it.getString(roundArgument)?.toInt() ?: 0
            paramExtra = it.getString(extraArgument)
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