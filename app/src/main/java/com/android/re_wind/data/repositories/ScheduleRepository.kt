package com.android.re_wind.data.repositories

import com.android.re_wind.data.model.RwSchedule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

class ScheduleRepository private constructor() {
    companion object {
        private var instance: ScheduleRepository? = null

        fun getInstance(): ScheduleRepository {
            if (instance == null) {
                instance = ScheduleRepository()
            }

            return instance!!
        }
    }

    private val auth by lazy { Firebase.auth }
    private val db by lazy { Firebase.firestore }

    fun getScheduleList(of: Date) = callbackFlow {
        val listener =
            FirebaseAuth.AuthStateListener { p0 -> this@callbackFlow.trySend(p0.currentUser) }
        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }.flatMapLatest {
        if (it == null) {
            flow { emit(listOf()) }
        } else {
            val from = Calendar.getInstance().apply {
                time = of
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val to = Calendar.getInstance().apply {
                time = from.time
                add(Calendar.DATE, 1)
            }

            db.collection("users")
                .document(it.uid)
                .collection("schedules")
                .whereGreaterThanOrEqualTo("date", from.time)
                .whereLessThan("date", to.time)
                .orderBy("date")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .snapshots()
                .map {
                    it.documents.mapNotNull { it.toObject(RwSchedule::class.java) }
                }
        }
    }

    suspend fun createSchedule(date: Date, message: String) {
        val uid = auth.uid ?: return
        val schedule = RwSchedule("", date, message, null)

        db.collection("users")
            .document(uid)
            .collection("schedules")
            .document()
            .set(schedule)
            .await()
    }

    suspend fun completeSchedule(documentId: String, complete: Boolean) {
        val uid = auth.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("schedules")
            .document(documentId)
            .set(
                hashMapOf(
                    "completed" to complete,
                ), SetOptions.merge()
            )
            .await()
    }

    suspend fun editSchedule(documentId: String, date: Date, message: String) {
        val uid = auth.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("schedules")
            .document(documentId)
            .set(
                hashMapOf(
                    "date" to date,
                    "message" to message
                ), SetOptions.merge()
            )
            .await()
    }

    suspend fun removeSchedule(documentId: String) {
        val uid = auth.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("schedules")
            .document(documentId)
            .delete()
            .await()
    }
}