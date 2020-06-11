package com.tonigames.reaction.leftorright

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tonigames.reaction.R
import kotlinx.android.synthetic.main.fragment_left_or_right.*

class LeftOrRightFragment : Fragment(R.layout.fragment_left_or_right), ILeftOrRight {

    private val roundArgument: String = "Round"
    private val extraArgument: String = "Extra"

    override fun onResume() {
        super.onResume()

        randomImage().run {
            imageBtn.setImageResource(this)
            imageBtn.tag = this
        }

        resources.displayMetrics?.heightPixels?.also {
            imageContainer.animate().y((it / 2).toFloat()).setDuration(400L).start()
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