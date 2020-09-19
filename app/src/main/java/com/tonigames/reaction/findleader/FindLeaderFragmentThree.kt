package com.tonigames.reaction.findleader

import android.animation.Animator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tonigames.reaction.R
import com.tonigames.reaction.common.GameFinishListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FindLeaderFragmentThree : AbstractFindLeaderFragment(R.layout.fragment_find_leader_three) {
    private var param1: String? = null
    private var param2: String? = null

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_leader_three, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FindLeaderFragmentThree().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}