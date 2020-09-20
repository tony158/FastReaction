package com.tonigames.reaction.findleader

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tonigames.reaction.R
import com.tonigames.reaction.common.GameFinishListener
import kotlinx.android.synthetic.main.fragment_find_leader_two.*
import kotlin.random.Random

private const val DURATION = 2600L

class FindLeaderFragmentTwo : AbstractFindLeaderFragment(R.layout.fragment_find_leader_two) {

    override var seekBarAnimator: Animator? = null
    override var gameOverListener: GameFinishListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_find_leader_two, container, false)
    }

    override fun initImageButtons() {
        val selectedImages = mutableListOf<Int>()
        val randomInt = Random.nextInt(1, 100)
        when (randomInt % 3) {
            0 -> {
                val twoImages: List<Int> = allDrawables.shuffled().takeLast(2)
                for (i in 1..4) selectedImages.add(twoImages[0])
                for (i in 1..5) selectedImages.add(twoImages[1])
            }
            1 -> {
                val threeImages: List<Int> = allDrawables.shuffled().takeLast(3)
                for (i in 1..2) selectedImages.add(threeImages[0])
                for (i in 1..3) selectedImages.add(threeImages[1])
                for (i in 1..4) selectedImages.add(threeImages[2])
            }
            else -> {
                val fourImages: List<Int> = allDrawables.shuffled().takeLast(4)
                for (i in 1..2) selectedImages.add(fourImages[0])
                for (i in 1..2) selectedImages.add(fourImages[1])
                for (i in 1..2) selectedImages.add(fourImages[2])
                for (i in 1..3) selectedImages.add(fourImages[3])
            }
        }
        selectedImages.shuffle()

        toggleToImgMap.values.forEachIndexed { idx, imgBtn ->
            imgBtn.setImageResource(selectedImages[idx])
            imgBtn.tag = selectedImages[idx]
        }
    }

    override fun onResume() {
        super.onResume()

        val modifiedDuration =
            when (toggleToImgMap.values.groupingBy { it.tag.toString() }.eachCount().count()) {
                2 -> DURATION - 800     // 2 kinds of images
                3 -> DURATION - 400     // 3 kinds of images
                4 -> DURATION           // 4 kinds of images
                else -> DURATION
            }

        seekBarAnimator = initSeekBarAnimator(
            reduceDuration(modifiedDuration, paramRound),
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