package com.android.re_wind.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.android.re_wind.data.repositories.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import java.util.Date

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ScheduleRepository.getInstance()

    /**
     * 오늘 해야할 일 리스트
     */
    val todayScheduleList = repository.getScheduleList(Date())

    /**
     * 선택된 날짜
     */
    val selectedDate = MutableStateFlow(Date())


    /**
     * 선택된 날짜의 할 일 리스트
     */
    val selectedDateScheduleList = selectedDate.flatMapLatest {
        repository.getScheduleList(it)
    }

    /**
     * 모든 일정 리스트
     */
    val allSchedules = repository.getAllSchedules()

    /**
     * 일정 생성
     *
     * @param date 날짜
     * @param message 할 일
     */
    suspend fun createSchedule(date: Date, message: String) =
        repository.createSchedule(date, message)

    /**
     * 일정 완료
     */
    suspend fun completeSchedule(documentId: String, complete: Boolean) =
        repository.completeSchedule(documentId, complete)

    /**
     * 일정을 수정
     *
     * @param documentId Firestore document ID
     * @param date 날짜
     * @param message 할 일
     */
    suspend fun editSchedule(documentId: String, date: Date, message: String) =
        repository.editSchedule(documentId, date, message)

    /**
     * 일정 삭제
     *
     * @param documentId Firestore document ID
     */
    suspend fun removeSchedule(documentId: String) =
        repository.removeSchedule(documentId)
}
