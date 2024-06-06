package com.android.re_wind.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.re_wind.data.model.RwSchedule
import com.android.re_wind.databinding.ItemScheduleBinding

class ScheduleAdapter : ListAdapter<RwSchedule, ScheduleAdapter.ScheduleItemViewHolder>(diffUtil) {
    var onRadioButtonClickListener: ((RwSchedule) -> Unit)? = null
    var onItemClickListener: ((RwSchedule) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemScheduleBinding.inflate(inflater, parent, false)
        return ScheduleItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleItemViewHolder, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            radioButton.isChecked = item.completed ?: false

            radioButton.setOnClickListener {
                onRadioButtonClickListener?.invoke(item)
            }

            if (item.completed == true) {
                container.setOnClickListener(null)
            } else {
                container.setOnClickListener {
                    onItemClickListener?.invoke(item)
                }
            }

            messageTextView.text = item.message
            // 시간 정보 표시 (예시: "HH:mm" 형식)
            val timeText = "${item.time.hour}:${item.time.minute.toString().padStart(2, '0')}"
            timeTextView.text = timeText // timeTextView는 ItemScheduleBinding에 있는 시간 표시 TextView로 가정
        }
    }

    class ScheduleItemViewHolder(val binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<RwSchedule>() {
            override fun areItemsTheSame(oldItem: RwSchedule, newItem: RwSchedule): Boolean {
                return oldItem.documentId == newItem.documentId
            }

            override fun areContentsTheSame(oldItem: RwSchedule, newItem: RwSchedule): Boolean {
                return oldItem.date == newItem.date &&
                        oldItem.completed == newItem.completed &&
                        oldItem.message == newItem.message &&
                        oldItem.time == newItem.time // 시간 비교 추가
            }

            override fun getChangePayload(oldItem: RwSchedule, newItem: RwSchedule): Any {
                return Any()
            }
        }
    }
}
