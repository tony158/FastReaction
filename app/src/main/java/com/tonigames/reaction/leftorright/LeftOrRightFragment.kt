package com.tonigames.reaction.leftorright

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.MotionEvent.ACTION_UP
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.reaction.DefaultAnimatorListener
import com.tonigames.reaction.R
import kotlinx.android.synthetic.main.fragment_left_or_right.*
import kotlinx.android.synthetic.main.fragment_left_or_right.progressBar
import kotlinx.android.synthetic.main.fragment_left_or_right.tvRoundCnt

private const val DURATION = 2000L

class LeftOrRightFragment : Fragment(R.layout.fragment_left_or_right), ILeftOrRight {
    private val roundArgument: String = "-1"
    private val extraArgument: String = "Extra"

    var paramRound: Int = -1
    var paramExtra: String? = null

    private var mFragmentListener: FragmentGestureListener? = null

    private var screenWidth = 0

    private var seekBarAnimator: Animator? = null
    private var mLeftRightResultListener: LeftRightResultListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            paramRound = it.getString(roundArgument)?.toInt() ?: -1
            paramExtra = it.getString(extraArgument)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_left_or_right, container, false).also {
            val imgContainer = it.findViewById<RelativeLayout>(R.id.imageContainer)

            mFragmentListener = FragmentGestureListener(imgContainer)

            GestureDetector(context!!, mFragmentListener).also { detector ->
                it.setOnTouchListener { view, event ->
                    detector.onTouchEvent(event)

                    if (event.action == ACTION_UP) {
                        mFragmentListener?.handleActionUp(view)
                    }

                    true
                }
            }
        }
    }

    private inner class FragmentGestureListener(private val imgContainer: RelativeLayout) :
        GestureDetector.SimpleOnGestureListener() {

        private val mScreen: Rect = Rect(
            0, 0, resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels
        )

        private var mDirectionX: Float = 0F

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            mDirectionX -= distanceX
            imgContainer.translationX -= distanceX

            val state = outScreenState(imgContainer)
            if (state != ViewOutState.Inside) {
                publishResult()
            }

            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            mDirectionX = 0F
            return true
        }

        //move the view to left or right side until out of screen
        fun handleActionUp(view: View) {
            val listener = object : DefaultAnimatorListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    publishResult()
                }
            }

            val xMoveBy = if (mDirectionX > 0) screenWidth / 2 else -screenWidth / 2

            imgContainer.animate()
                .translationX((xMoveBy).toFloat())
                .setDuration(200L)
                .setListener(listener)
                .start()
        }

        private fun publishResult(): Boolean {
            outScreenState(imgContainer).also { mLeftRightResultListener?.onResult(it) }
            return false
        }

        fun outScreenState(view: View): ViewOutState {
            if (view == null) return ViewOutState.Inside
            if (!view.isShown) return ViewOutState.Inside

            val viewPos = Rect().apply { view.getGlobalVisibleRect(this) }

            return when {
                viewPos.left == mScreen.left -> {
                    ViewOutState.LeftOut
                }
                viewPos.right == mScreen.right -> {
                    ViewOutState.RightOut
                }
                else -> {
                    ViewOutState.Inside
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is LeftRightResultListener) {
            mLeftRightResultListener = context
        }
    }

    override fun onResume() {
        super.onResume()
        tvRoundCnt.text = paramRound.toString()
        screenWidth = resources.displayMetrics.widthPixels

        randomImage().also { imageBtn.setImageResource(it); imageBtn.tag = it }

        listOf(
            Techniques.RotateInUpRight,
            Techniques.RotateInUpLeft,
            Techniques.RotateInDownRight,
            Techniques.RotateInDownLeft
        ).random().also {
            if (paramRound >= 0) {
                YoYo.with(it).duration(800L).playOn(imageContainer)
            } else {
                val endListener = object : DefaultAnimatorListener() {
                    override fun onAnimationEnd(animation: Animator?) {
                        mFragmentListener?.handleActionUp(imageContainer)
                    }
                }

                YoYo.with(it).duration(800L).withListener(endListener).playOn(imageContainer)
            }
        }

        if (paramRound >= 0) {
            seekBarAnimator = initSeekBarAnimator(
                DURATION,
                progressBar,
                mLeftRightResultListener
            ).also {
                it.start()
            }
        }
    }

    private fun initSeekBarAnimator(
        animationTime: Long,
        progressBar: SeekBar? = null,
        resultListener: LeftRightResultListener? = null
    ): Animator {
        progressBar?.max = 100
        progressBar?.progress = 0

        return ValueAnimator.ofInt(0, 100).apply {
            duration = animationTime
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                (animation.animatedValue as Int).also { progressBar?.progress = it }
            }

            addListener(object : DefaultAnimatorListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    try {
                        progressBar?.progress = 100
                        resultListener?.onTimeUp()
                    } catch (e: Exception) {
                        Log.wtf("LeftOrRightFragment", e.message ?: "exception")
                    }
                }
            })
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LeftOrRightFragment().apply {
                arguments = Bundle().apply {
                    putString(roundArgument, param1)
                    putString(extraArgument, param2)
                }
            }
    }
}