package com.tonigames.reaction.findleader

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tonigames.reaction.R
import com.tonigames.reaction.common.GameFinishListener
import kotlinx.android.synthetic.main.fragment_find_leader_two.*

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
        return inflater.inflate(R.layout.fragment_find_leader_two, container, false)
    }

    override fun initImageButtons() {
        val twoImages: List<Int> = allDrawables.shuffled().takeLast(2)
        val selectedImages = mutableListOf<Int>()
        for (i in 1..4) selectedImages.add(twoImages[0])
        for (i in 1..5) selectedImages.add(twoImages[1])
        selectedImages.shuffle()

        toggleToImgMap.values.forEachIndexed { idx, imgBtn ->
            imgBtn.setImageResource(selectedImages[idx])
            imgBtn.tag = selectedImages[idx]
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