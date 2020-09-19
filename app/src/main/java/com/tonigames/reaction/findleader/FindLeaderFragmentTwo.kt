package com.tonigames.reaction.findleader

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import com.tonigames.reaction.R
import com.tonigames.reaction.common.GameFinishListener
import kotlinx.android.synthetic.main.fragment_find_leader_two.*

private const val DURATION = 3000L

class FindLeaderFragmentTwo : AbstractFindLeaderFragment(R.layout.fragment_find_leader_two) {

    override var seekBarAnimator: Animator? = null
    override var gameOverListener: GameFinishListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_leader_two, container, false)

        return view
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
        @JvmStatic
        fun newInstance(param1: String, param2: String) = FindLeaderFragmentTwo()
            .apply {
                arguments = Bundle().apply {
                    putString(roundArgument, param1)
                    putString(extraArgument, param2)
                }
            }
    }
}