package com.tonigames.reaction.rockpaper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tonigames.reaction.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RockPaperFragment : Fragment(R.layout.fragment_rock_paper) {

    private var mRoundCnt: Int = Int.MIN_VALUE
    private var mLastImg: Int = Int.MIN_VALUE

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rock_paper, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RockPaperFragment().apply {
                arguments = Bundle().apply {
                    mRoundCnt = param1.toInt()
                    mLastImg = param2.toInt()
                }
            }
    }
}