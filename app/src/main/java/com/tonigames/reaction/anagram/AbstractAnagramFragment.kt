package com.tonigames.reaction.anagram

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import android.widget.ToggleButton
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.reaction.DefaultAnimatorListener
import com.tonigames.reaction.common.GameFinishListener
import com.tonigames.reaction.common.ISeekBar
import com.tonigames.reaction.findpair.IImageFragment
import com.tonigames.reaction.findpair.WRONG_SELECTION_MSG

abstract class AbstractAnagramFragment(contentLayoutId: Int) : Fragment(contentLayoutId), IImageFragment, ISeekBar {

    protected val roundArgument: String = "Round"
    protected val extraArgument: String = "Extra"

    var paramRound: Int = 0
    var paramExtra: String? = null

    abstract var seekBarAnimator: Animator?
    abstract var gameOverListener: GameFinishListener?

    abstract var quizImageBtnList: List<ImageButton>
    abstract var ansToggleToImgMap: Map<ToggleButton, Pair<RelativeLayout, Set<ImageButton>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramRound = it.getString(roundArgument)?.toInt() ?: 0
            paramExtra = it.getString(extraArgument)
        }
    }

    override fun onResume() {
        super.onResume()

        ansToggleToImgMap.keys.forEach { it.isChecked = false }   //uncheck all

        initImageButtons()
        bindToggleListeners()
    }

    private fun initImageButtons() {
        //region init the quiz Images
        val drawables: List<Int> = IImageFragment.allDrawables.shuffled()
        val quizImages = drawables.subList(0, quizImageBtnList.size)
        val restOfImages = drawables.subList(quizImages.size, drawables.size)

        quizImageBtnList.forEachIndexed { idx, imgBtn ->
            imgBtn.setImageResource(quizImages[idx])
            imgBtn.tag = quizImages[idx]
        }
        //endregion

        //region init correct Answer Images
        quizImages.shuffled()
        val correctAnsToggle = ansToggleToImgMap.keys.toList().random()!!

        ansToggleToImgMap.getValue(correctAnsToggle).second.shuffled().forEachIndexed { idx, imageBtn ->
            imageBtn.setImageResource(quizImages[idx])
            imageBtn.tag = quizImages[idx]
        }
        //endregion

        // region init wrong Answer images
        initWrongAnsRows(correctAnsToggle, quizImages, restOfImages)
        //endregion
    }

    abstract fun initWrongAnsRows(correctAnsToggle: ToggleButton, quizImages: List<Int>, restOfImages: List<Int>)

    private fun bindToggleListeners() {
        ansToggleToImgMap.keys.forEach { toggle ->
            toggle.setOnCheckedChangeListener { selected, _ ->

                YoYo.with(Techniques.Tada).duration(100).withListener(
                    object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            if (!selected.isChecked) return // handle only when checked is true

                            seekBarAnimator?.pause()

                            val selectedImgTags = ansToggleToImgMap.getValue((selected as ToggleButton)).second.map { it.tag }.toSet()
                            val quizImgTags = quizImageBtnList.map { it.tag }.toSet()
                            val diff = selectedImgTags subtract quizImgTags

                            ansToggleToImgMap.keys.forEach { if (it != selected) it.isChecked = false } // uncheck the others

                            if (diff.isNullOrEmpty()) {
                                gameOverListener?.onCorrectAnswer()
                            } else {
                                gameOverListener?.onFailedToSolve(WRONG_SELECTION_MSG)
                            }
                        }
                    }).playOn(ansToggleToImgMap.getValue(toggle).first)
            }
        }
    }

    fun clearAllToggles() = ansToggleToImgMap.keys.forEach { it.isChecked = false }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is GameFinishListener) {
            gameOverListener = context
        }
    }
}