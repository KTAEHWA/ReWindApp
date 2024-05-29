package com.android.re_wind.ui.home.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.re_wind.databinding.PageCalendarBinding
import com.android.re_wind.ui.BaseFragment
import com.android.re_wind.ui.home.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarPage : BaseFragment() {
    private var _binding: PageCalendarBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PageCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            calendarView.setOnDateChangeListener { _, y, m, d ->
                val prevSelectedDate = Calendar.getInstance().apply {
                    time = viewModel.selectedDate.value
                }

                if (prevSelectedDate.get(Calendar.YEAR) == y &&
                    prevSelectedDate.get(Calendar.MONTH) == m &&
                    prevSelectedDate.get(Calendar.DAY_OF_MONTH) == d
                ) return@setOnDateChangeListener

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
        }

        lifecycleScope.launch {
            viewModel.selectedDate.collectLatest {
                binding.calendarView.date = viewModel.selectedDate.value.time
            }
        }
    }
}