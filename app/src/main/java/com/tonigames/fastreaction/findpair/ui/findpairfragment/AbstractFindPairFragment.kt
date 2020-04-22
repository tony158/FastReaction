package com.tonigames.fastreaction.findpair.ui.findpairfragment

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.fastreaction.DefaultAnimatorListener
import com.tonigames.fastreaction.findpair.ui.findpairfragment.IFindPairFragment.Companion.allDrawables

abstract class AbstractFindPairFragment(contentLayoutId: Int) : Fragment(contentLayoutId),
    IFindPairFragment {

    protected val ARG_ROUND: String = "Round"
    protected val ARG_EXTRA: String = "Extra"

    var paramRound: Int = 0
    var paramExtra: String? = null

    abstract var seekBarAnimator: Animator?
    abstract var buttonLayoutMap: Map<Int, Pair<RelativeLayout, ImageButton>>

    private var allImageButtons: List<ImageButton> = listOf()
    private val checkedToggles: MutableList<ToggleButton> = mutableListOf()

    abstract var gameOverListener: FindPairInteractionListener?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramRound = it.getString(ARG_ROUND)?.toInt() ?: 0
            paramExtra = it.getString(ARG_EXTRA)
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

        initImages()
    }

    private fun initImages() {
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
                    gameOverListener?.onCorrectPairSelected()
                } else {
                    gameOverListener?.onFailedToSolve("Wrong selection")
                }
            } else if (checkedToggles.size > 2) {
                seekBarAnimator?.cancel()
                gameOverListener?.onFailedToSolve("Wrong selection!")
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

        if (context is FindPairInteractionListener) {
            gameOverListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        gameOverListener = null
    }

    fun initSeekBarAnimator(
        animationTime: Long,
        progressBar: SeekBar? = null,
        onFinishListener: FindPairInteractionListener? = null
    ): Animator {

        progressBar?.max = 100
        progressBar?.progress = 0

        return ValueAnimator.ofInt(0, 100).apply {
            duration = animationTime
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                (animation.animatedValue as Int).also { progressBar?.progress = it }
            }

            doOnEnd {
                progressBar?.progress = 100
                try {
                    onFinishListener?.onFailedToSolve("Time's up")
                } catch (e: Exception) {
                    Log.d("FindPairFragment", "onFinishListener?.onFailedToSolve exception")
                }
            }
        }
    }
}