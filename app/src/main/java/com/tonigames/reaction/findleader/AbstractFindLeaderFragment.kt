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
import kotlinx.android.synthetic.main.fragment_find_leader_two.*

private const val DURATION = 3000L

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

    override fun onResume() {
        super.onResume()

        seekBarAnimator = initSeekBarAnimator(
            reduceDuration(DURATION, paramRound),
            progressBar,
            gameOverListener
        ).also {
            it.start()
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