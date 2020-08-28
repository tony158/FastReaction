package com.tonigames.reaction.anagram

import android.animation.Animator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.ToggleButton
import com.tonigames.reaction.findpair.IImageFragment

abstract class AbstractAnagramFragment(contentLayoutId: Int) : Fragment(contentLayoutId), IImageFragment {
    protected val roundArgument: String = "Round"
    protected val extraArgument: String = "Extra"

    var paramRound: Int = 0
    var paramExtra: String? = null

    abstract var seekBarAnimator: Animator?
    private val checkedToggles: MutableList<ToggleButton> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramRound = it.getString(roundArgument)?.toInt() ?: 0
            paramExtra = it.getString(extraArgument)
        }
    }

    override fun onResume() {
        super.onResume()

        checkedToggles.forEach { if (it.isChecked) it.isChecked = false }   //uncheck all
        checkedToggles.clear()
    }

    fun clearAllToggles() = checkedToggles.run {
        this.forEach { it.isChecked = false }
        this.clear()
    }
}