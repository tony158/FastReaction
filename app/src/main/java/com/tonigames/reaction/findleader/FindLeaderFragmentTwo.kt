package com.tonigames.reaction.findleader

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.ToggleButton
import com.tonigames.reaction.R
import com.tonigames.reaction.common.GameFinishListener
import com.tonigames.reaction.findpair.IImageFragment
import kotlinx.android.synthetic.main.fragment_find_leader_two.*

class FindLeaderFragmentTwo : AbstractFindLeaderFragment(R.layout.fragment_find_leader_two) {

    private lateinit var ansToggleToImgMap: Map<ToggleButton, ImageButton>

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
        return inflater.inflate(R.layout.fragment_find_leader_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ansToggleToImgMap = mapOf(
            toggleBtn11 to imageBtn11,
            toggleBtn12 to imageBtn12,
            toggleBtn13 to imageBtn13,

            toggleBtn21 to imageBtn21,
            toggleBtn22 to imageBtn22,
            toggleBtn23 to imageBtn23,

            toggleBtn31 to imageBtn31,
            toggleBtn32 to imageBtn32,
            toggleBtn33 to imageBtn33
        )
    }

    override fun onResume() {
        super.onResume()


        ansToggleToImgMap.keys.forEach { it.isChecked = false }   //uncheck all

        initImageButtons()
        bindToggleListeners()
    }

    private fun initImageButtons() {
        val drawables: List<Int> = allDrawables.shuffled()


    }

    private fun bindToggleListeners() {

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