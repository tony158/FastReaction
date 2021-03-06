package com.tonigames.reaction.leftorright

import android.animation.Animator
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.reaction.DefaultAnimatorListener
import com.tonigames.reaction.R
import kotlinx.android.synthetic.main.fragment_left_or_right.*

private const val DURATION = 1150L

class LeftOrRightFragment : Fragment(R.layout.fragment_left_or_right), ILeftOrRight {
    private var mRoundCnt: Int = Int.MIN_VALUE
    private var mCurrImage: Int = Int.MIN_VALUE
    private var mLastImg: Int = Int.MIN_VALUE

    private var mSeekBarAnimator: Animator? = null
    private var mResultListener: ResultListener? = null
    private var mSwipeListener: SwipeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_left_or_right, container, false).also { frag ->
            val imgContainer = frag.findViewById<RelativeLayout>(R.id.imageContainer)

            mSwipeListener = SwipeListener(context!!, imgContainer)
            frag.setOnTouchListener(mSwipeListener)
        }
    }

    private fun enableTouch(enabled: Boolean) {
        view?.isEnabled = enabled
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ResultListener) {
            mResultListener = context
        }
    }

    override fun onResume() {
        super.onResume()
        tvRoundCnt.text = mRoundCnt.toString()

        val image = if (mLastImg > 0) {
            listOf(randomImage(), mLastImg).shuffled()[0]
        } else {
            randomImage()
        }

        image.also {
            imageBtn.setImageResource(it)
            imageBtn.tag = it

            mCurrImage = it
        }

        listOf(
            Techniques.FadeIn,
            Techniques.FadeInLeft,
            Techniques.FadeInRight,
            Techniques.FadeInUp,
            Techniques.FadeInDown
        ).random().also {
            if (mRoundCnt >= 0) {
                YoYo.with(it).duration(80L).playOn(imageContainer)
            } else {
                val endListener = object : DefaultAnimatorListener() {
                    override fun onAnimationEnd(animation: Animator?) {
                        mSwipeListener?.moveHorizontal(true)
                    }
                }

                YoYo.with(it).duration(300L).withListener(endListener).playOn(imageContainer)
            }
        }

        if (mRoundCnt >= 0) {
            seekBarContainer.isVisible = true

            mSeekBarAnimator = initSeekBarAnimator(
                reduceDuration(DURATION, mRoundCnt),
                progressBar,
                mResultListener
            ).also { it.start() }
        }
    }

    private inner class SwipeListener(context: Context, val imgContainer: View) :
        OnSwipeTouchListener(context) {

        private val mScreen: Rect = Rect(
            0,
            0,
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels
        )

        override fun onSwipeLeft() {
            super.onSwipeLeft()
            moveHorizontal(true)
        }

        override fun onSwipeRight() {
            super.onSwipeRight()
            moveHorizontal(false)
        }

        override fun scroll(distanceX: Float) {
            imgContainer.translationX -= distanceX

            val state = outScreenState(imgContainer)
            if (state != ViewOutState.Inside) {
                mSeekBarAnimator?.pause()
                publishResult()
            }
        }

        override fun onFingerUp() {
            if (mAnimator == null) {
                val screenCenterX = resources.displayMetrics.widthPixels / 2

                val imgCenterX = IntArray(2).also { imgContainer.getLocationInWindow(it) }
                    .run { this[0] + imgContainer.width / 2 }

                if (imgCenterX >= screenCenterX)
                    moveHorizontal(false)
                else
                    moveHorizontal(true)
            }
        }

        private var mAnimator: ViewPropertyAnimator? = null

        fun moveHorizontal(isToLeft: Boolean) {
            val listener = object : DefaultAnimatorListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    mSeekBarAnimator?.pause()
                    publishResult()
                }
            }

            val xMoveBy = with(resources.displayMetrics.widthPixels) {
                if (isToLeft) -this / 2 else this / 2
            }

            mAnimator = imgContainer.animate().also {
                it.translationX((xMoveBy).toFloat())
                it.duration = 70L
                it.setListener(listener)
                it.start()
            }
        }

        private fun publishResult() {
            outScreenState(imgContainer).also {
                enableTouch(false)
                mResultListener?.onResult(mCurrImage, it)
            }
        }

        fun outScreenState(view: View): ViewOutState {
            if (!view.isShown) return ViewOutState.Inside

            val viewPos = Rect().apply { view.getGlobalVisibleRect(this) }

            return when {
                viewPos.left == mScreen.left -> ViewOutState.LeftOut
                viewPos.right == mScreen.right -> ViewOutState.RightOut
                else -> ViewOutState.Inside
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LeftOrRightFragment().apply {
                arguments = Bundle().apply {
                    mRoundCnt = param1.toInt()
                    mLastImg = param2.toInt()
                }
            }
    }
}