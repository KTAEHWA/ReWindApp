package com.android.re_wind.ui.home.page

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.android.re_wind.data.model.RwSchedule
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class CustomCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val calendarView: MaterialCalendarView
    val dayViews = mutableMapOf<LocalDate, DayView>()
    private var onDateChangedListener: ((date: LocalDate) -> Unit)? = null

    init {
        calendarView = MaterialCalendarView(context, attrs)
        calendarView.setOnDateChangedListener { _, date, _ ->
            val localDate = LocalDate.of(date.year, date.month, date.day)
            onDateChangedListener?.invoke(localDate)
        }
        addView(calendarView)
    }

    fun setOnDateChangedListener(listener: (LocalDate) -> Unit) {
        onDateChangedListener = listener
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDayViewForSelectedDate(date: LocalDate) {
        removeAllViews()
        addView(calendarView)
        dayViews[date]?.let { dayView ->
            addView(dayView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addDayView(date: LocalDate, dayView: DayView) {
        dayViews[date] = dayView
        calendarView.invalidateDecorators()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setSelectedDate(date: LocalDate) {
        val calendarDay = CalendarDay.from(date.year, date.monthValue, date.dayOfMonth)
        calendarView.setDateSelected(calendarDay, true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addEventDecorator(dates: List<LocalDate>, text: String) {
        val calendarDays =
            dates.map { CalendarDay.from(it.year, it.monthValue, it.dayOfMonth) }.toHashSet()
        val decorator = EventDecorator(calendarDays, text)
        calendarView.addDecorator(decorator)
    }

    class DayView(context: Context) : LinearLayout(context) {
        private val scheduleListView: LinearLayout

        init {
            orientation = VERTICAL
            scheduleListView = LinearLayout(context).apply {
                setBackgroundColor(Color.BLACK) // 배경색을 블랙으로 설정
                orientation = VERTICAL
                layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT) // 크기 조정
            }
            addView(scheduleListView)
        }

        fun setSchedules(schedules: List<RwSchedule>) {
            scheduleListView.removeAllViews() // Ensure we start with a clean slate
            schedules.forEach { schedule ->
                val textView = TextView(context).apply {
                    text = schedule.message
                    textSize = 14f
                    setPadding(8, 8, 8, 8)
                }
                scheduleListView.addView(textView)
            }
        }
    }
}
