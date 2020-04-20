package com.tonigames.fastreaction.cloud

import android.icu.util.Calendar
import android.util.Log
import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
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

    fun updateScore(gameType: Int, highestScore: Int) {
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

    private fun setValueAndRefreshRanking(refName: String, gameType: Int, highestScore: Int) {

        val highScoreDto = if (gameType == MainMenuActivity.Constants.TAP_COLOR) {
            TapColorDTO(android_id, "toni san", highestScore, Calendar.getInstance().time)
        } else {
            FindPairDTO(android_id, "toni san", highestScore, Calendar.getInstance().time)
        }

        database.getReference(refName).child(android_id).setValue(highScoreDto)
            .addOnCompleteListener {
                database.getReference(refName)
                    .orderByChild("score")
                    .startAt(highestScore.toDouble()).apply {
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
