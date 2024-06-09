package com.android.re_wind.ui.home.page

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.re_wind.databinding.PageCalendarBinding
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.android.re_wind.ui.BaseFragment
import com.android.re_wind.ui.home.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class CalendarPage : BaseFragment() {
    private var _binding: PageCalendarBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = PageCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            calendarView.setOnDateChangedListener { date ->
                val y = date.year
                val m = date.monthValue - 1
                val d = date.dayOfMonth
                val prevSelectedDate = Calendar.getInstance().apply {
                    time = viewModel.selectedDate.value
                }
                if (prevSelectedDate.get(Calendar.YEAR) == y &&
                    prevSelectedDate.get(Calendar.MONTH) == m &&
                    prevSelectedDate.get(Calendar.DAY_OF_MONTH) == d) return@setOnDateChangedListener

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, y)
                    set(Calendar.MONTH, m)
                    set(Calendar.DAY_OF_MONTH, d)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                viewModel.selectedDate.value = calendar.time
            }

            val today = LocalDate.now()
            calendarView.setSelectedDate(today)
        }

        lifecycleScope.launch {
            viewModel.selectedDate.collectLatest {
                val localDate = it.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                binding.calendarView.showDayViewForSelectedDate(localDate)
            }
        }

        lifecycleScope.launch {
            viewModel.allSchedules.collectLatest { schedules ->
                val events = schedules.groupBy { it.date.toLocalDate() }
                events.forEach { (date, schedules) ->
                    val dayView = binding.calendarView.dayViews[date] ?: CustomCalendarView.DayView(requireContext())
                    dayView.setSchedules(schedules)
                    binding.calendarView.addDayView(date, dayView)
                }
            }
        }
    }
}

