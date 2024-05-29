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
                return oldItem.date.time == newItem.date.time &&
                        oldItem.completed == newItem.completed &&
                        oldItem.message == newItem.message
            }

            override fun getChangePayload(oldItem: RwSchedule, newItem: RwSchedule): Any {
                return Object()
            }
        }
    }
}