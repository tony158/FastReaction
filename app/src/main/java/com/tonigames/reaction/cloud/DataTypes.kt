package com.tonigames.reaction.cloud

import java.util.*

data class TapColorDTO(
    val user_id: String,
    val user_name: String,
    val score: Int,
    val visit_at: Date? = null
)

data class FindPairDTO(
    val user_id: String,
    val user_name: String,
    val score: Int,
    val visit_at: Date? = null
)

data class LeftRightDTO(
    val user_id: String,
    val user_name: String,
    val score: Int,
    val visit_at: Date? = null
)

data class RockPaperDTO(
    val user_id: String,
    val user_name: String,
    val score: Int,
    val visit_at: Date? = null
)

data class ImageAnagramDTO(
    val user_id: String,
    val user_name: String,
    val score: Int,
    val visit_at: Date? = null
)

data class AdsEnabled(val enabled: Boolean = false)