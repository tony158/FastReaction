package com.tonigames.reaction.popups

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.annotation.LayoutRes
import com.tonigames.reaction.R

class BgImageSettingAdapter(
    context: Context, @LayoutRes private val layoutResource: Int,
    private val dataList: MutableList<BgImageModel> = mutableListOf()
) : ArrayAdapter<BgImageModel>(context, layoutResource, dataList) {

    init {
        dataList.addAll(initDataList())
    }

    override fun getView(position: Int, itemView: View?, parent: ViewGroup): View {
        var convertView = itemView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            convertView = inflater.inflate(R.layout.bg_image_setting_item, null, true)

            holder.countryFlag = convertView.findViewById(R.id.imgView1) as ImageView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            //holder = convertView.tag as ViewHolder
        }

        //holder.countryFlag?.setImageResource(languageDataList[position].countryFlag)

        return convertView!!
    }

    private inner class ViewHolder {
        var countryFlag: ImageView? = null
    }


    private fun initDataList(): MutableList<BgImageModel> {

        /*        var selectedIndex = context.getSharedPreferences(
                    MainMenuActivity.Constants.SELECTED_LANGUAGE,
                    Context.MODE_PRIVATE
                ).getInt(MainMenuActivity.Constants.SELECTED_LANGUAGE, 0)*/

        //selectedIndex = max(min(selectedIndex, 3), 0)

        val list = mutableListOf<BgImageModel>()

        list.add(BgImageModel(R.drawable.menu_uk))

        return list
    }
}