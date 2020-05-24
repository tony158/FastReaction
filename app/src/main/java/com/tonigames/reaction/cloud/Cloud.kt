package com.tonigames.reaction.cloud

import android.bluetooth.BluetoothAdapter
import android.icu.util.Calendar
import android.os.AsyncTask
import android.widget.TextView
import com.google.firebase.database.*
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.FIND_PAIR
import com.tonigames.reaction.MainMenuActivity.Constants.Companion.TAP_COLOR
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

private const val DEFAULT_TEXT = "......"

class RefreshRankingTask(
    private var android_id: String,
    private val view: TextView?
) : AsyncTask<DataSnapshot, Void, String>() {

    override fun onPreExecute() {
        super.onPreExecute()
        view?.text = DEFAULT_TEXT
    }

    override fun onCancelled() {
        super.onCancelled()
        view?.text = DEFAULT_TEXT
    }

    override fun doInBackground(vararg params: DataSnapshot?): String {
        if (params.isEmpty()) return "param is null"
        val sortedList = params[0]?.children?.toMutableList() ?: mutableListOf()
        if (sortedList.isEmpty()) return "param is null"

        var ranking = 0
        for (dataSnapshot in sortedList) {
            if (android_id == dataSnapshot.key) break
            ranking++
        }

        return "${sortedList.size - ranking} / ${sortedList.size}"
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        view?.text = result
    }
}

class FireBaseAccess(
    private val android_id: String,
    private val textView: TextView? = null
) {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val deviceName: String =
        BluetoothAdapter.getDefaultAdapter().name ?: android.os.Build.MODEL

    fun updateScore(gameType: Int, highestScore: Int) {
        textView?.text = DEFAULT_TEXT

        val refName = when (gameType) {
            TAP_COLOR -> TapColorDTO::class.java.simpleName
            FIND_PAIR -> FindPairDTO::class.java.simpleName
            else -> LeftRightDTO::class.java.simpleName
        }

        //first delete the record and then add
        database.getReference(refName).child(android_id).setValue(null).addOnCompleteListener {
            setValueAndRefreshRanking(refName, gameType, highestScore)
        }
    }

    private fun setValueAndRefreshRanking(refName: String, gameType: Int, score: Int) {

        val highScoreDto: Any = when (gameType) {
            TAP_COLOR -> TapColorDTO(android_id, deviceName, score, Calendar.getInstance().time)
            FIND_PAIR -> FindPairDTO(android_id, deviceName, score, Calendar.getInstance().time)
            else -> LeftRightDTO(android_id, deviceName, score, Calendar.getInstance().time)
        }

        database.getReference(refName).child(android_id).setValue(highScoreDto)
            .addOnCompleteListener {
                database.getReference(refName).orderByChild("score") // sorted here
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {}

                        override fun onDataChange(data: DataSnapshot) {
                            RefreshRankingTask(android_id, textView).execute(data)
                        }
                    })
            }
    }
}
