package com.tonigames.fastreaction.tapcolor

import android.content.res.Resources
import android.widget.Button
import com.tonigames.fastreaction.R
import com.tonigames.fastreaction.popups.MyLanguageEnum
import java.util.*

interface FragmentInteractionListener {
    fun onCorrectColorSelected()

    // wrong answer or time's up
    fun onFailedToSolve(msg: String = "")
}

interface IColorFragment {

    enum class Color(val cc: Int) {
        Red(android.graphics.Color.RED),
        Green(android.graphics.Color.parseColor("#008000")),
        Blue(android.graphics.Color.BLUE),
        Yellow(android.graphics.Color.YELLOW),
        Black(android.graphics.Color.BLACK),
        Gray(android.graphics.Color.LTGRAY);

        companion object {
            private val englishMap = mapOf(
                Red to R.string.color_txt_english_Red,
                Green to R.string.color_txt_english_Green,
                Blue to R.string.color_txt_english_Blue,
                Yellow to R.string.color_txt_english_Yellow,
                Black to R.string.color_txt_english_Black,
                Gray to R.string.color_txt_english_Gray
            )

            private val germanMap = mapOf(
                Red to R.string.color_txt_german_Red,
                Green to R.string.color_txt_german_Green,
                Blue to R.string.color_txt_german_Blue,
                Yellow to R.string.color_txt_german_Yellow,
                Black to R.string.color_txt_german_Black,
                Gray to R.string.color_txt_german_Gray
            )

            private val frenchMap = mapOf(
                Red to R.string.color_txt_french_Red,
                Green to R.string.color_txt_french_Green,
                Blue to R.string.color_txt_french_Blue,
                Yellow to R.string.color_txt_french_Yellow,
                Black to R.string.color_txt_french_Black,
                Gray to R.string.color_txt_french_Gray
            )

            private val chineseMap = mapOf(
                Red to R.string.color_txt_chinese_Red,
                Green to R.string.color_txt_chinese_Green,
                Blue to R.string.color_txt_chinese_Blue,
                Yellow to R.string.color_txt_chinese_Yellow,
                Black to R.string.color_txt_chinese_Black,
                Gray to R.string.color_txt_chinese_Gray
            )

            private val japaneseMap = mapOf(
                Red to R.string.color_txt_japanese_Red,
                Green to R.string.color_txt_japanese_Green,
                Blue to R.string.color_txt_japanese_Blue,
                Yellow to R.string.color_txt_japanese_Yellow,
                Black to R.string.color_txt_japanese_Black,
                Gray to R.string.color_txt_japanese_Gray
            )

            private val koreanMap = mapOf(
                Red to R.string.color_txt_korean_Red,
                Green to R.string.color_txt_korean_Green,
                Blue to R.string.color_txt_korean_Blue,
                Yellow to R.string.color_txt_korean_Yellow,
                Black to R.string.color_txt_korean_Black,
                Gray to R.string.color_txt_korean_Gray
            )

            private val theMap = mutableMapOf(
                MyLanguageEnum.English to englishMap,
                MyLanguageEnum.German to germanMap,
                MyLanguageEnum.French to frenchMap,
                MyLanguageEnum.Chinese to chineseMap,
                MyLanguageEnum.Japanese to japaneseMap,
                MyLanguageEnum.Korean to koreanMap
            )

            fun translatedName(
                res: Resources,
                language: MyLanguageEnum = MyLanguageEnum.English,
                color: Color = Red
            ): String {
                return with(
                    theMap.getOrDefault(language, englishMap)
                        .getOrDefault(color, R.string.color_txt_english_Red)
                ) {
                    res.getString(this)
                }
            }
        }
    }


    /** Returns a random element    */
    fun <E> List<E>.random(): E? = if (size > 0) get(Random().nextInt(size)) else null

    var correctColorButton: Pair<Color?, Button>?

    fun generateColor(colorCnt: Int): List<Color> {
        return with(Color.values().toMutableList(), {
            this.shuffle()
            this.take(if (colorCnt < this.size) colorCnt else this.size)
        })
    }

    fun reduceDuration(duration: Long, roundCnt: Int?): Long {
        var newDuration = duration
        roundCnt?.let {
            newDuration = when {
                it > 20 -> (duration * 0.9).toLong()
                it > 40 -> (duration * 0.85).toLong()
                else -> duration
            }
        }

        return newDuration
    }
}
