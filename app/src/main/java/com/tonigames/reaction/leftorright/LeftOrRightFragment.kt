package com.tonigames.reaction.leftorright

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tonigames.reaction.R
import kotlinx.android.synthetic.main.fragment_left_or_right.*


class LeftOrRightFragment : Fragment(R.layout.fragment_left_or_right), ILeftOrRight {

    private val roundArgument: String = "Round"
    private val extraArgument: String = "Extra"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_left_or_right, container, false).also {
            it.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    toggleBtn.isChecked = true
                } else if (event.action == MotionEvent.ACTION_UP) {
                    toggleBtn.isChecked = false
                }

                true
            }
        }
    }

    override fun onResume() {
        super.onResume()

        randomImage().run {
            imageBtn.setImageResource(this)
            imageBtn.tag = this
        }

        resources.displayMetrics?.heightPixels?.also {
            imageContainer.animate().y((it / 2).toFloat()).setDuration(500L).start()
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