package com.tonigames.reaction.cloud

import android.bluetooth.BluetoothAdapter
import android.icu.util.Calendar
import android.os.AsyncTask
import android.widget.TextView
import com.google.firebase.database.*
import com.tonigames.reaction.MainMenuActivity
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

class RefreshRankingTask(
    private var android_id: String,
    private val view: TextView?
) : AsyncTask<DataSnapshot, Void, String>() {

    private val defaultText = "......"

    override fun onPreExecute() {
        super.onPreExecute()
        view?.text = defaultText
    }

    override fun onCancelled() {
        super.onCancelled()
        view?.text = defaultText
    }

    override fun doInBackground(vararg params: DataSnapshot?): String {
        if (params.isEmpty()) return "param is null"
        val listData = params[0]!!.children.toMutableList()
        if (listData.isEmpty()) return "param is null"

        val sortedList = listData.sortedByDescending { it.child("score").value.toString().toInt() }

        var ranking = 1
        for (dataSnapshot in sortedList) {
            if (android_id == dataSnapshot.key) break
            ranking++
        }

        return "$ranking / ${sortedList.size}"
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
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {}

                        override fun onDataChange(data: DataSnapshot) {
                            RefreshRankingTask(android_id, textView).execute(data)
                        }
                    })
            }
    }
}
