package com.android.re_wind.ui.home.page

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.re_wind.R
import com.android.re_wind.common.extensions.toLocal
import com.android.re_wind.common.extensions.toUtc
import com.android.re_wind.databinding.PageScheduleBinding
import com.android.re_wind.ui.BaseFragment
import com.android.re_wind.ui.home.HomeFragment
import com.android.re_wind.ui.home.HomeViewModel
import com.android.re_wind.ui.home.adapter.ScheduleAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class SchedulePage : BaseFragment() {
    private var _binding: PageScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>(ownerProducer = { requireParentFragment() })
    private val adapter by lazy { ScheduleAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PageScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            datePickerButton.setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .setSelection(viewModel.selectedDate.value.toUtc.time)
                    .build()

                datePicker.addOnPositiveButtonClickListener {
                    val selectedDate = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                        this.timeInMillis = it
                    }.time.toLocal
                    viewModel.selectedDate.value = selectedDate

                    // 날짜를 선택한 후 시간 선택 다이얼로그 표시
                    showCreateScheduleDialog()
                }

                datePicker.show(childFragmentManager, "date_picker")
            }

            adapter.onItemClickListener = {
                lifecycleScope.launch {
                    (parentFragment as HomeFragment).showScheduleEditDialog(it)
                }
            }

            adapter.onRadioButtonClickListener = {
                lifecycleScope.launch {
                    viewModel.completeSchedule(
                        it.documentId,
                        it.completed != true
                    )
                }
            }

            recyclerView.adapter = adapter
        }

        lifecycleScope.launch {
            viewModel.selectedDate.collectLatest {
                binding.dateTextView.text = SimpleDateFormat(
                    "M월 d일 EEEE",
                    Locale.KOREA
                ).format(viewModel.selectedDate.value)
            }
        }

        lifecycleScope.launch {
            viewModel.selectedDateScheduleList.collectLatest {
                adapter.submitList(it)
            }
        }
    }

    private fun showCreateScheduleDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_schedule, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
        val createButton = dialogView.findViewById<View>(R.id.create_button)
        val dateEditText = dialogView.findViewById<TextView>(R.id.date_edit_text) // TextView로 캐스팅
        val alarmSwitch = dialogView.findViewById<Switch>(R.id.alarmSwitch)

        timePicker.setIs24HourView(true)

        dateEditText.text =
            SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(viewModel.selectedDate.value)

        createButton.setOnClickListener {
            val hour = if (Build.VERSION.SDK_INT >= 23) timePicker.hour else timePicker.currentHour
            val minute =
                if (Build.VERSION.SDK_INT >= 23) timePicker.minute else timePicker.currentMinute
            viewModel.setTime(hour, minute)

            val selectedDate = viewModel.selectedDate.value
            val selectedTime = viewModel.selectedTime.value
            val message = dialogView.findViewById<TextView>(R.id.schedule_edit_text).text.toString()
            val alarmEnabled = alarmSwitch.isChecked

            lifecycleScope.launch {
                viewModel.createSchedule(selectedDate, selectedTime, message, alarmEnabled)
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}