package com.tonigames.reaction.findpair

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.reaction.DefaultAnimatorListener
import com.tonigames.reaction.common.GameFinishListener
import com.tonigames.reaction.common.ISeekBar
import com.tonigames.reaction.findpair.IImageFragment.Companion.allDrawables

abstract class AbstractFindPairFragment(contentLayoutId: Int) : Fragment(contentLayoutId),
    IImageFragment, ISeekBar {

    val roundArgument: String = "Round"
    val extraArgument: String = "Extra"

    var paramRound: Int = 0
    var paramExtra: String? = null

    abstract var seekBarAnimator: Animator?
    abstract var buttonLayoutMap: Map<Int, Pair<RelativeLayout, ImageButton>>

    private var allImageButtons: List<ImageButton> = listOf()
    private val checkedToggles: MutableList<ToggleButton> = mutableListOf()

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

        checkedToggles.forEach { if (it.isChecked) it.isChecked = false }   //uncheck all
        checkedToggles.clear()

        allImageButtons
            .takeIf { it.isNullOrEmpty() }
            ?.run {
                allImageButtons = buttonLayoutMap.values.map { pair -> pair.second }
            }

        initImageButtons()
    }

    private fun initImageButtons() {
        if (allImageButtons.isNullOrEmpty() || allDrawables.isNullOrEmpty()) return

        val drawables: List<Int> = allDrawables.shuffled()

        allImageButtons
            .shuffled()
            .let { imgButtons ->
                val selectedDrawables: List<Int> = drawables.takeLast(imgButtons.size - 1)

                selectedDrawables.forEachIndexed { index, drawable ->
                    imgButtons[index].setImageResource(drawable)
                    imgButtons[index].tag = drawable
                }

                with(selectedDrawables.random()) {
                    imgButtons.last().setImageResource(this ?: 0)
                    imgButtons.last().tag = this ?: 0
                }
            }
    }

    fun bindButtonListeners(toggleButtons: List<ToggleButton>) {

        toggleButtons.forEach(fun(button: ToggleButton) {
            button.setOnCheckedChangeListener { toggleView, isChecked ->
                buttonLayoutMap.let {
                    YoYo.with(Techniques.Tada).duration(100).withListener(
                        object : DefaultAnimatorListener() {
                            override fun onAnimationEnd(animation: Animator?) {
                                with(toggleView as ToggleButton) {
                                    handleToggleChange(this, isChecked)
                                }
                            }
                        }).playOn(it[toggleView.id]?.first)
                }
            }
        })
    }

    private fun handleToggleChange(theToggleButton: ToggleButton, isChecked: Boolean) {

        if (isChecked) {
            checkedToggles.add(theToggleButton)

            if (checkedToggles.size == 2) {
                seekBarAnimator?.pause()

                val img1 = buttonLayoutMap[checkedToggles[0].id]?.second?.tag
                val img2 = buttonLayoutMap[checkedToggles[1].id]?.second?.tag

                if ((img1 != null && img2 != null) && (img1 == img2)) {
                    gameOverListener?.onCorrectAnswer()
                } else {
                    gameOverListener?.onFailedToSolve(WRONG_SELECTION_MSG)
                }
            } else if (checkedToggles.size > 2) {
                seekBarAnimator?.cancel()
                gameOverListener?.onFailedToSolve(WRONG_SELECTION_MSG)
            }
        } else {
            checkedToggles.remove(theToggleButton)
        }
    }

    fun clearAllToggles() = checkedToggles.run {
        this.forEach { it.isChecked = false }
        this.clear()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is GameFinishListener) {
            gameOverListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        gameOverListener = null
    }
}