package com.android.re_wind.ui.home

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.android.re_wind.R
import com.android.re_wind.common.extensions.toLocal
import com.android.re_wind.common.extensions.toUtc
import com.android.re_wind.data.model.RwSchedule
import com.android.re_wind.data.model.ScheduleTime
import com.android.re_wind.databinding.DialogCreateScheduleBinding
import com.android.re_wind.databinding.DialogEditScheduleBinding
import com.android.re_wind.databinding.FragmentHomeBinding
import com.android.re_wind.ui.BaseFragment
import com.android.re_wind.ui.home.page.CalendarPage
import com.android.re_wind.ui.home.page.HomePage
import com.android.re_wind.ui.home.page.SchedulePage
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class HomeFragment : BaseFragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    // 메뉴바 정의
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
            viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    when (position) {
                        // 홈 화면
                        0 -> {
                            homeButton.isSelected = true
                            calendarButton.isSelected = false
                            listButton.isSelected = false
                            addButton.isVisible = false
                        }

                        // 캘린더 화면
                        1 -> {
                            homeButton.isSelected = false
                            calendarButton.isSelected = true
                            listButton.isSelected = false
                            addButton.isVisible = false
                        }

                        // 일정 목록 화면
                        else -> {
                            homeButton.isSelected = false
                            calendarButton.isSelected = false
                            listButton.isSelected = true
                            addButton.isVisible = true
                        }
                    }
                }
            })

            addButton.setOnClickListener {
                showCreateScheduleDialog()
            }

            homeButton.setOnClickListener { viewPager.setCurrentItem(0, true) }
            calendarButton.setOnClickListener { viewPager.setCurrentItem(1, true) }
            listButton.setOnClickListener { viewPager.setCurrentItem(2, true) }
        }

        lifecycleScope.launch {
            var isFirst = true

            // 캘린더에서 선택한 날짜의 일정 목록으로 이동
            viewModel.selectedDate.collectLatest {
                if (isFirst) {
                    isFirst = false
                    return@collectLatest
                }

                binding.viewPager.setCurrentItem(2, true)
            }
        }
    }

    /**
     * 일정 추가 다이얼로그를 보여준다.
     */
    private fun showCreateScheduleDialog() {
        var date = viewModel.selectedDate.value

        val inflater = LayoutInflater.from(requireContext())
        val binding = DialogCreateScheduleBinding.inflate(inflater)

        val dialog = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_MaterialAlertDialog_Rounded
        ).setView(binding.root)
            .show()

        with(binding) {
            val listener: (Editable?) -> Unit = {
                val date = dateEditText.text?.toString()?.trim() ?: ""
                val schedule = scheduleEditText.text?.toString()?.trim() ?: ""

                createTextView.isEnabled = date.isNotEmpty() && schedule.isNotEmpty()
            }

            dateEditText.doAfterTextChanged(listener)
            scheduleEditText.doAfterTextChanged(listener)

            dateEditText.setText(SimpleDateFormat("yyyy. MM. dd (E)", Locale.KOREA).format(date))

            datePickerButton.setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .setSelection(date.toUtc.time)
                    .build()

                datePicker.addOnPositiveButtonClickListener {
                    date = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                        this.timeInMillis = it
                    }.time.toLocal

                    dateEditText.setText(
                        SimpleDateFormat("yyyy. MM. dd (E)", Locale.KOREA).format(
                            date
                        )
                    )
                }

                datePicker.show(childFragmentManager, "date_picker")
            }

            // TimePicker 초기화
            timePicker.setIs24HourView(true)

            createButton.setOnClickListener {
                if (!createTextView.isEnabled) return@setOnClickListener

                val hour = if (Build.VERSION.SDK_INT >= 23) timePicker.hour else timePicker.currentHour
                val minute = if (Build.VERSION.SDK_INT >= 23) timePicker.minute else timePicker.currentMinute
                val alarmEnabled = alarmSwitch.isChecked

                lifecycleScope.launch {
                    viewModel.createSchedule(
                        date,
                        ScheduleTime(hour, minute),
                        scheduleEditText.text.toString().trim(),
                        alarmEnabled
                    )
                    Toast.makeText(requireContext(), "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
            }

            closeButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    /**
     * 일정 수정 다이얼로그를 보여준다.
     */
    fun showScheduleEditDialog(schedule: RwSchedule) {
        var date = schedule.date

        val inflater = LayoutInflater.from(requireContext())
        val binding = DialogEditScheduleBinding.inflate(inflater)

        val dialog = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_MaterialAlertDialog_Rounded
        ).setView(binding.root)
            .show()

        with(binding) {
            val listener: (Editable?) -> Unit = {
                val date = dateEditText.text?.toString()?.trim() ?: ""
                val schedule = scheduleEditText.text?.toString()?.trim() ?: ""

                editTextView.isEnabled = date.isNotEmpty() && schedule.isNotEmpty()
            }

            dateEditText.doAfterTextChanged(listener)
            scheduleEditText.doAfterTextChanged(listener)

            dateEditText.setText(SimpleDateFormat("yyyy. MM. dd (E)", Locale.KOREA).format(date))
            scheduleEditText.append(schedule.message)

            datePickerButton.setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .setSelection(date.toUtc.time)
                    .build()

                datePicker.addOnPositiveButtonClickListener {
                    date = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                        this.timeInMillis = it
                    }.time.toLocal

                    dateEditText.setText(
                        SimpleDateFormat("yyyy. MM. dd (E)", Locale.KOREA).format(
                            date
                        )
                    )
                }

                datePicker.show(childFragmentManager, "date_picker")
            }

            // TimePicker 초기화
            timePicker.setIs24HourView(true)
            timePicker.hour = schedule.time.hour
            timePicker.minute = schedule.time.minute
            alarmSwitch.isChecked = schedule.alarmEnabled

            removeButton.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.removeSchedule(schedule.documentId)
                    Toast.makeText(requireContext(), "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
            }

            editButton.setOnClickListener {
                if (!dateEditText.isEnabled) return@setOnClickListener

                val hour = if (Build.VERSION.SDK_INT >= 23) timePicker.hour else timePicker.currentHour
                val minute = if (Build.VERSION.SDK_INT >= 23) timePicker.minute else timePicker.currentMinute
                val alarmEnabled = alarmSwitch.isChecked

                lifecycleScope.launch {
                    viewModel.editSchedule(
                        schedule.documentId,
                        date,
                        ScheduleTime(hour, minute),
                        scheduleEditText.text.toString().trim(),
                        alarmEnabled
                    )
                    Toast.makeText(requireContext(), "일정이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
            }

            closeButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount() = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomePage()
                1 -> CalendarPage()
                else -> SchedulePage()
            }
        }
    }

}

