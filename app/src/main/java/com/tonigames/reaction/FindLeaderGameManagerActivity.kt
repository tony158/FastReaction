package com.tonigames.reaction

import android.os.Bundle
import com.tonigames.reaction.common.GameFinishListener
import com.tonigames.reaction.findleader.AbstractFindLeaderFragment
import com.tonigames.reaction.findleader.FindLeaderFragmentTwo

class FindLeaderGameManagerActivity : AbstractGameManagerActivity(), GameFinishListener {

    private var mCurrFragment: AbstractFindLeaderFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCurrFragment = FindLeaderFragmentTwo.newInstance(mRoundCnt.toString(), "Three")
            .also {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
    }

    override fun onCorrectAnswer() {
    }

    override fun onFailedToSolve(msg: String) {
    }
}