package com.tonigames.reaction.leftorright

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tonigames.reaction.R
import kotlinx.android.synthetic.main.fragment_left_or_right.*


class LeftOrRightFragment : Fragment(R.layout.fragment_left_or_right), ILeftOrRight {

    protected val roundArgument: String = "Round"
    protected val extraArgument: String = "Extra"

    override fun onResume() {
        super.onResume()

        with(randomImage()) {
            imageBtn.setImageResource(this)
            imageBtn.tag = this
        }

        val height = DisplayMetrics().heightPixels
        imageContainer.animate().y(height.toFloat() / 2).setDuration(1000L).start()
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