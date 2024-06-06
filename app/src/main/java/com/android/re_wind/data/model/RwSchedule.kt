package com.android.re_wind.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class RwSchedule(
    @DocumentId val documentId: String = "",
    val date: Date = Date(),
    val message: String = "",
    val completed: Boolean? = false,
    @ServerTimestamp val timestamp: Date? = null,
    val time: ScheduleTime = ScheduleTime() // 커스텀 클래스 사용
)
