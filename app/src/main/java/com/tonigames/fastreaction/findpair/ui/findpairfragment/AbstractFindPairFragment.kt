package com.tonigames.fastreaction.findpair.ui.findpairfragment

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.*
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.fastreaction.DefaultAnimatorListener
import com.tonigames.fastreaction.findpair.ui.findpairfragment.IFindPairFragment.Companion.allDrawables
import java.util.stream.Collectors.toList

abstract class AbstractFindPairFragment(contentLayoutId: Int) : Fragment(contentLayoutId),
    IFindPairFragment {

    protected val ARG_ROUND: String = "Round"
    protected val ARG_EXTRA: String = "Extra"

    var paramRound: Int = 0
    var paramExtra: String? = null

    abstract var mCountDownTimer: CountDownTimer?
    abstract var buttonLayoutMap: Map<Int, Pair<RelativeLayout, ImageButton>>

    private var allImageButtons: List<ImageButton> = listOf()
    private val checkedToggles: MutableList<ToggleButton> = mutableListOf()

    abstract var listener: FindPairInteractionListener?

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
                mCountDownTimer?.cancel()

                val img1 = buttonLayoutMap[checkedToggles[0].id]?.second?.tag
                val img2 = buttonLayoutMap[checkedToggles[1].id]?.second?.tag

                if ((img1 != null && img2 != null) && (img1 == img2)) {
                    listener?.onCorrectPairSelected()
                } else {
                    listener?.onFailedToSolve("Wrong selection")
                }
            } else if (checkedToggles.size > 2) {
                mCountDownTimer?.cancel()
                listener?.onFailedToSolve("Wrong selection!")
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
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        mCountDownTimer?.cancel()
        listener = null
    }

    fun initCountDownTimer(
        duration: Long,
        interval: Long,
        syncLocker: Activity?,
        progressBar: SeekBar? = null,
        onFinishListener: FindPairInteractionListener? = null
    ): CountDownTimer {

        progressBar?.max = 100
        progressBar?.progress = 0

        return object : CountDownTimer(duration, interval) {
            var i = 0

            override fun onTick(millisUntilFinished: Long) {
                syncLocker?.let {
                    synchronized(it) {
                        i++
                        progressBar?.progress = i * 100 / (duration.toInt() / interval.toInt())
                    }
                }
            }

            override fun onFinish() {
                syncLocker?.let {
                    synchronized(it) {
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
    }
}