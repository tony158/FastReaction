package com.tonigames.reaction.cloud

import android.bluetooth.BluetoothAdapter
import android.icu.util.Calendar
import android.os.AsyncTask
import android.widget.TextView
import com.google.firebase.database.*
import com.tonigames.reaction.Constants.Companion.FIND_PAIR
import com.tonigames.reaction.Constants.Companion.IMAGE_ANAGRAM
import com.tonigames.reaction.Constants.Companion.LEFT_RIGHT
import com.tonigames.reaction.Constants.Companion.ROCK_PAPER
import com.tonigames.reaction.Constants.Companion.TAP_COLOR

private const val DEFAULT_TEXT = "......"
private const val MINIMUM_RANKING = 500

class RefreshRankingTask(
    private var android_id: String,
    private val ranking: TextView?
) : AsyncTask<DataSnapshot, Void, String>() {

    override fun onPreExecute() {
        super.onPreExecute()
        ranking?.text = DEFAULT_TEXT
    }

    override fun onCancelled() {
        super.onCancelled()
        ranking?.text = DEFAULT_TEXT
    }

    override fun doInBackground(vararg params: DataSnapshot?): String {
        if (params.isEmpty()) return "param is null"
        val sortedList = params[0]?.children?.toList() ?: listOf()
        if (sortedList.isEmpty()) return "param is null"

        var ranking = 1
        var found = false

        for (i in (sortedList.size - 1) downTo 0) {
            if (android_id == sortedList[i].key) {
                found = true
                break
            }

            ranking++
        }

        return if (found) "$ranking" else "> $MINIMUM_RANKING"
    }

    override fun onPostExecute(rankingResult: String) {
        super.onPostExecute(rankingResult)
        ranking?.text = rankingResult
    }
}

class FireBaseAccess(
    private val android_id: String,
    private val textView: TextView? = null
) {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val deviceName: String =
        BluetoothAdapter.getDefaultAdapter()?.name ?: android.os.Build.MODEL

    fun updateScore(gameType: Int, highestScore: Int) {
        textView?.text = DEFAULT_TEXT

        val refName = when (gameType) {
            TAP_COLOR -> TapColorDTO::class.java.simpleName
            FIND_PAIR -> FindPairDTO::class.java.simpleName
            LEFT_RIGHT -> LeftRightDTO::class.java.simpleName
            ROCK_PAPER -> RockPaperDTO::class.java.simpleName
            IMAGE_ANAGRAM -> ImageAnagramDTO::class.java.simpleName
            else -> TapColorDTO::class.java.simpleName
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
            ROCK_PAPER -> RockPaperDTO(android_id, deviceName, score, Calendar.getInstance().time)
            IMAGE_ANAGRAM -> ImageAnagramDTO(android_id, deviceName, score, Calendar.getInstance().time)
            else -> LeftRightDTO(android_id, deviceName, score, Calendar.getInstance().time)
        }

        database.getReference(refName).child(android_id).setValue(highScoreDto)
            .addOnCompleteListener {
                database.getReference(refName)
                    .orderByChild("score")  // sorted here
                    .limitToLast(MINIMUM_RANKING)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {}

                        override fun onDataChange(data: DataSnapshot) {
                            RefreshRankingTask(android_id, textView).execute(data)
                        }
                    })
            }
    }
}
