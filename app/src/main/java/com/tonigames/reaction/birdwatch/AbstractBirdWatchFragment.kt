package com.tonigames.reaction.birdwatch

import android.animation.Animator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tonigames.reaction.R
import com.tonigames.reaction.common.GameFinishListener
import com.tonigames.reaction.common.ISeekBar

abstract class AbstractBirdWatchFragment(contentLayoutId: Int) : Fragment(contentLayoutId), ISeekBar {
    protected val roundArgument: String = "Round"
    protected val extraArgument: String = "Extra"

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_abstract, container, false)
    }
}