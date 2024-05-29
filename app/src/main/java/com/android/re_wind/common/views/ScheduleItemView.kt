package com.android.re_wind.common.views

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import com.android.re_wind.databinding.ItemScheduleBinding

class ScheduleItemView(context: Context) : FrameLayout(context) {
    private val binding: ItemScheduleBinding

    var isChecked: Boolean
        get() = binding.radioButton.isChecked
        set(value) {
            binding.radioButton.isChecked = value
        }

    var text: String
        get() = binding.radioButton.text.toString()
        set(value) {
            binding.radioButton.text = value
        }

    var onCheckedChangeListener: ((ScheduleItemView, Boolean) -> Unit)? = null
    var onClickListener: (() -> Unit)? = null

    init {
        binding = ItemScheduleBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding.root, MATCH_PARENT, WRAP_CONTENT)

        with(binding) {
            radioButton.setOnCheckedChangeListener { _, b ->
                container.isSelected = b
                onCheckedChangeListener?.invoke(this@ScheduleItemView, b)
            }

            radioButton.setOnClickListener {
                onClickListener?.invoke()
            }

//            container.setOnClickListener {
//                radioButton.isChecked = true
//                onClickListener?.invoke()
//            }
        }
    }
}