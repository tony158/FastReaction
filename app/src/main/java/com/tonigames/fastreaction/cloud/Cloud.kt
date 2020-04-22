package com.tonigames.fastreaction.cloud

import android.bluetooth.BluetoothAdapter
import android.icu.util.Calendar
import android.widget.TextView
import com.google.firebase.database.*
import com.tonigames.fastreaction.MainMenuActivity
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

class FireBaseAccess(
    private val database: FirebaseDatabase,
    private val android_id: String,
    private val textView: TextView? = null
) {
    private val deviceName: String =
        BluetoothAdapter.getDefaultAdapter().name ?: android.os.Build.MODEL

    fun updateScore(gameType: Int, highestScore: Int) {
        textView?.text = "......"

        val refName =
            if (gameType == MainMenuActivity.Constants.TAP_COLOR) {
                TapColorDTO::class.java.simpleName
            } else {
                FindPairDTO::class.java.simpleName
            }

        //first delete the record and then add
        database.getReference(refName).child(android_id).setValue(null).addOnCompleteListener {
            setValueAndRefreshRanking(refName, gameType, highestScore)
        }
    }

    private fun setValueAndRefreshRanking(refName: String, gameType: Int, score: Int) {

        val highScoreDto: Any = if (gameType == MainMenuActivity.Constants.TAP_COLOR) {
            TapColorDTO(android_id, deviceName, score, Calendar.getInstance().time)
        } else {
            FindPairDTO(android_id, deviceName, score, Calendar.getInstance().time)
        }

        database.getReference(refName).child(android_id).setValue(highScoreDto)
            .addOnCompleteListener {
                database.getReference(refName)
                    .orderByChild("score")
                    .startAt(score.toDouble()).apply {
                        this.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {}

                            override fun onDataChange(p0: DataSnapshot) {
                                textView?.text = p0.childrenCount.toString()
                            }
                        })
                    }
            }
    }
}