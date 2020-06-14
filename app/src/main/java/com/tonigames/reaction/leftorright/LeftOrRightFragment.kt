package com.tonigames.reaction.leftorright

import android.animation.Animator
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.view.MotionEvent.ACTION_UP
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.reaction.DefaultAnimatorListener
import com.tonigames.reaction.R
import kotlinx.android.synthetic.main.fragment_left_or_right.*


class LeftOrRightFragment : Fragment(R.layout.fragment_left_or_right), ILeftOrRight {
    private val roundArgument: String = "Round"
    private val extraArgument: String = "Extra"
    private var screenWidth = 0
    private var mLastState = ViewOutState.Inside

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_left_or_right, container, false).also {
            val imgContainer = it.findViewById<RelativeLayout>(R.id.imageContainer)

            val listener = FragmentGestureListener(imgContainer)

            GestureDetector(context!!, listener).also { detector ->
                it.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(view: View, event: MotionEvent): Boolean {
                        detector.onTouchEvent(event)

                        if (event.action == ACTION_UP) {
                            listener.handleActionUp(view)
                        }

                        return true
                    }
                })
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

            val xMoveBy = if (mDirectionX > 0) screenWidth / 2
            else -screenWidth / 2

            imgContainer.animate()
                .translationX((xMoveBy).toFloat())
                .setDuration(200L)
                .setListener(listener)
                .start()
        }

        private fun publishResult(): Boolean {
            val state = outScreenState(imgContainer)





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
            //            return viewPos.right <= mScreen.left || viewPos.left >= mScreen.right
        }


    }

    enum class ViewOutState {
        LeftOut,
        Inside,
        RightOut
    }

    override fun onResume() {
        super.onResume()

        screenWidth = resources.displayMetrics.widthPixels
        randomImage().also { imageBtn.setImageResource(it); imageBtn.tag = it }

        listOf(Techniques.RotateInUpRight, Techniques.RotateInUpLeft).random().also {
            YoYo.with(it).duration(800L).playOn(imageContainer)
        }

        tvRoundCnt.text = "test"
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