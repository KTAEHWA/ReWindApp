package com.android.re_wind.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.android.re_wind.data.model.ScheduleTime
import com.android.re_wind.data.repositories.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import java.util.Date
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
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
     * 선택된 시간
     */
    private val _selectedTime = MutableStateFlow(ScheduleTime())
    val selectedTime: MutableStateFlow<ScheduleTime> get() = _selectedTime

    /**
     * 선택된 날짜의 할 일 리스트
     */
    val selectedDateScheduleList = selectedDate.flatMapLatest {
        repository.getScheduleList(it)
    }

    /**
     * 일정 생성
     *
     * @param date 날짜
     * @param time 시간
     * @param message 할 일
     * @param alarmEnabled 알람 설정 여부
     */
    suspend fun createSchedule(date: Date, time: ScheduleTime, message: String, alarmEnabled: Boolean) =
        repository.createSchedule(date, time, message, alarmEnabled)

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
     * @param time 시간
     * @param message 할 일
     * @param alarmEnabled 알람 설정 여부
     */
    suspend fun editSchedule(documentId: String, date: Date, time: ScheduleTime, message: String, alarmEnabled: Boolean) =
        repository.editSchedule(documentId, date, time, message, alarmEnabled)

    /**
     * 일정 삭제
     *
     * @param documentId Firestore document ID
     */
    suspend fun removeSchedule(documentId: String) =
        repository.removeSchedule(documentId)

    /**
     * 시간 설정
     *
     * @param hour 시간
     * @param minute 분
     */
    fun setTime(hour: Int, minute: Int) {
        _selectedTime.value = ScheduleTime(hour, minute)
    }
}
