package com.tonigames.reaction.findleader

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageButton
import android.widget.ToggleButton
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.reaction.DefaultAnimatorListener
import com.tonigames.reaction.R
import com.tonigames.reaction.common.GameFinishListener
import com.tonigames.reaction.common.ISeekBar
import com.tonigames.reaction.findpair.WRONG_SELECTION_MSG
import kotlinx.android.synthetic.main.fragment_find_leader_two.*

private const val DURATION = 2200L

abstract class AbstractFindLeaderFragment(contentLayoutId: Int) : Fragment(contentLayoutId), ISeekBar {
    val roundArgument: String = "Round"
    val extraArgument: String = "Extra"

    var paramRound: Int = 0
    var paramExtra: String? = null

    abstract var seekBarAnimator: Animator?
    abstract var gameOverListener: GameFinishListener?

    protected lateinit var toggleToImgMap: Map<ToggleButton, ImageButton>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            paramRound = it.getString(roundArgument)?.toInt() ?: 0
            paramExtra = it.getString(extraArgument)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleToImgMap = mapOf(
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

    abstract fun initImageButtons()

    fun bindToggleListeners() {
        toggleToImgMap.keys.forEach { toggle ->
            toggle.setOnCheckedChangeListener { selected, _ ->
                YoYo.with(Techniques.Tada).duration(80).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            if (!selected.isChecked) return // handle only when checked is true

                            seekBarAnimator?.pause()

                            val selectedImgBtn = toggleToImgMap.getValue((selected as ToggleButton))

                            if (checkAnswer(selectedImgBtn, toggleToImgMap)) {
                                gameOverListener?.onCorrectAnswer()
                            } else {
                                gameOverListener?.onFailedToSolve(WRONG_SELECTION_MSG)
                            }
                        }
                    }).playOn(toggleToImgMap.getValue(toggle))
            }
        }
    }

    private fun checkAnswer(selectedImgBtn: ImageButton, toggleToImgMap: Map<ToggleButton, ImageButton>): Boolean {
        val countMap = toggleToImgMap.values.groupingBy { it.tag.toString() }.eachCount()
        val maxOne = countMap.toList().maxBy { (_, value) -> value } ?: Pair("", 0)

        return maxOne.first == selectedImgBtn.tag.toString()
    }

    override fun onResume() {
        super.onResume()

        toggleToImgMap.keys.forEach { it.isChecked = false }   //uncheck all

        initImageButtons()
        bindToggleListeners()

        val imageKindCount = toggleToImgMap.values.groupingBy { it.tag.toString() }.eachCount().count()
        val modifiedDuration = if (imageKindCount == 2) DURATION - 300 else DURATION

        seekBarAnimator = initSeekBarAnimator(
            reduceDuration(modifiedDuration, paramRound),
            progressBar,
            gameOverListener
        ).also {
            it.start()
        }
    }

    fun clearAllToggles() {
        toggleToImgMap.keys.forEach { it.isChecked = false }   //uncheck all
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is GameFinishListener) {
            gameOverListener = context
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