package com.tonigames.reaction.popups

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView.CHOICE_MODE_SINGLE
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tonigames.reaction.DefaultAnimatorListener
import com.tonigames.reaction.IGameSettings
import com.tonigames.reaction.R

class LanguageSettingFragment(private val settingListener: IGameSettings) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setCanceledOnTouchOutside(true)

        val view = inflater.inflate(R.layout.language_setting_popup, container, false)

        view.findViewById<ListView>(R.id.listViewLanguage).apply {
            choiceMode = CHOICE_MODE_SINGLE
            adapter = LanguageSettingAdapter(this.context!!, R.layout.language_setting_popup)

            initItemSelectListener(this)
        }

        return view
    }

    private fun initItemSelectListener(listView: ListView) {
        listView.setOnItemClickListener { _, clickedItem, position, _ ->

            YoYo.with(Techniques.Swing).duration(1200).withListener(object :
                DefaultAnimatorListener() {
                override fun onAnimationStart(animation: Animator?) {
                    (listView.adapter as LanguageSettingAdapter).setSelectedLanguage(position)
                    settingListener.onLanguageChanged()
                }

                override fun onAnimationEnd(animation: Animator?) {
                    dialog?.dismiss()
                }
            }).playOn(clickedItem)
        }
    }

    override fun getTheme(): Int {
        return R.style.DialogShowingTheme
    }
}