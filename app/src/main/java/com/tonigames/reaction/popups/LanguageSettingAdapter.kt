package com.tonigames.reaction.popups

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.tonigames.reaction.Constants.Companion.SELECTED_LANGUAGE
import com.tonigames.reaction.MainMenuActivity
import com.tonigames.reaction.R

private const val MAX_LANGUAGE_COUNT = 5

class LanguageSettingAdapter(
    context: Context, @LayoutRes private val layoutResource: Int,
    private val languageDataList: MutableList<LanguageSettingModel> = mutableListOf()
) : ArrayAdapter<LanguageSettingModel>(context, layoutResource, languageDataList) {

    init {
        languageDataList.addAll(initDataList())
    }

    override fun getView(position: Int, itemView: View?, parent: ViewGroup): View {

        var convertView = itemView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            convertView = inflater.inflate(R.layout.language_setting_item, null, true)

            holder.languageName = convertView!!.findViewById(R.id.languageName) as TextView
            holder.countryFlag = convertView.findViewById(R.id.imgView) as ImageView
            holder.selectedLanguage = convertView.findViewById(R.id.selectedLanguage) as ImageView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        holder.languageName?.text = languageDataList[position].languageName
        holder.countryFlag?.setImageResource(languageDataList[position].countryFlag)
        holder.selectedLanguage?.setImageResource(languageDataList[position].checkedImage)

        return convertView
    }

    private inner class ViewHolder {
        var languageName: TextView? = null
        var countryFlag: ImageView? = null
        var selectedLanguage: ImageView? = null
    }

    fun setSelectedLanguage(selectedLanguageIndex: Int) {
        context.getSharedPreferences(
            SELECTED_LANGUAGE,
            Context.MODE_PRIVATE
        )?.edit()
            ?.putInt(SELECTED_LANGUAGE, selectedLanguageIndex)    //
            ?.commit()

        languageDataList.clear()
        languageDataList.addAll(initDataList())

        this.notifyDataSetChanged()
    }

    private fun initDataList(): MutableList<LanguageSettingModel> {

        var selectedIndex = context.getSharedPreferences(
            SELECTED_LANGUAGE,
            Context.MODE_PRIVATE
        )
            .getInt(SELECTED_LANGUAGE, 0)
            .run {
                this.coerceAtMost(MAX_LANGUAGE_COUNT).coerceAtLeast(0)
            }

        return mutableListOf<LanguageSettingModel>()
            .apply {
                add(LanguageSettingModel(MyLanguageEnum.English.name, R.drawable.menu_uk, 0))
                add(LanguageSettingModel(MyLanguageEnum.German.name, R.drawable.menu_germany, 0))
                add(LanguageSettingModel(MyLanguageEnum.French.name, R.drawable.menu_france, 0))
                add(LanguageSettingModel(MyLanguageEnum.Chinese.name, R.drawable.menu_china, 0))
                add(LanguageSettingModel(MyLanguageEnum.Spanish.name, R.drawable.menu_spain, 0))
                add(
                    LanguageSettingModel(
                        MyLanguageEnum.Portuguese.name,
                        R.drawable.menu_portugal,
                        0
                    )
                )

                this[selectedIndex].checkedImage = R.drawable.menu_correct
            }
    }
}