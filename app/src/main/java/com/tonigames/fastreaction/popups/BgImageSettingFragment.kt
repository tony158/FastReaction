package com.tonigames.fastreaction.popups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.tonigames.fastreaction.ISettingChange

import com.tonigames.fastreaction.R

class BgImageSettingFragment(private val settingListener: ISettingChange) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setCanceledOnTouchOutside(true)

        var view = inflater.inflate(R.layout.bg_image_setting_popup, container, false)

        view.findViewById<ListView>(R.id.listViewBgImage).apply {
            this.choiceMode = AbsListView.CHOICE_MODE_SINGLE
            this.adapter = BgImageSettingAdapter(this.context!!, R.layout.bg_image_setting_popup)
            //initItemSelectListener(this)
        }

        return view
    }


}
