package com.tonigames.reaction

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.tonigames.reaction.rockpaper.RockPaperFragment

class RockPaperManagerActivity : AppCompatActivity() {

    private var mRoundCnt: Int = 0
    private var mCurrFragment: Fragment? = null
    private var mDialogPopup: MaterialDialog? = null

    private var soundBtnClick: MediaPlayer? = null
    private var soundNegative: MediaPlayer? = null
    private var soundPositive: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_rock_paper_manager)

        initMedia()

        mCurrFragment = RockPaperFragment.newInstance(mRoundCnt.toString(), "0")
            .also {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
    }

    private fun initMedia() {
        soundBtnClick = MediaPlayer.create(this, R.raw.button_click)
        soundPositive = MediaPlayer.create(this, R.raw.correct_beep)
        soundNegative = MediaPlayer.create(this, R.raw.negative_beeps)
    }
}