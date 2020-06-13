package com.tonigames.reaction.leftorright

import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.reaction.R
import kotlinx.android.synthetic.main.fragment_left_or_right.*


class LeftOrRightFragment : Fragment(R.layout.fragment_left_or_right), ILeftOrRight {
    private var mToggleButton: ToggleButton? = null
    private val roundArgument: String = "Round"
    private val extraArgument: String = "Extra"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_left_or_right, container, false).also {
            mToggleButton = it.findViewById<ToggleButton>(R.id.toggleBtn)
            val imgContainer = it.findViewById<RelativeLayout>(R.id.imageContainer)

            GestureDetector(context!!, FragmentGestureListener(imgContainer)).also { detector ->
                it.setOnTouchListener { _, event ->
                    detector.onTouchEvent(event)
                }
            }
        }
    }


    private inner class FragmentGestureListener(private val imgContainer: RelativeLayout) :
        GestureDetector.SimpleOnGestureListener() {

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            imgContainer.translationX -= distanceX
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (velocityX > 0) {
                YoYo.with(Techniques.SlideOutRight).duration(500L).playOn(imgContainer)
            } else {
                YoYo.with(Techniques.SlideOutLeft).duration(500L).playOn(imgContainer)
            }

            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }
    }

    override fun onResume() {
        super.onResume()

        randomImage().also { imageBtn.setImageResource(it); imageBtn.tag = it }

//        listOf(Techniques.FadeInUp, Techniques.FadeInDown).random().also {
//            YoYo.with(it).duration(1000L).playOn(imageContainer)
//        }

//        Render(context!!).apply {
//            setAnimation(Slide().InDown(imageContainer))
//            start()
//        }


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