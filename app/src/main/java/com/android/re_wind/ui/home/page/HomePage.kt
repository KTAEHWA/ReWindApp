package com.android.re_wind.ui.home.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.re_wind.databinding.PageHomeBinding
import com.android.re_wind.ui.BaseFragment
import com.android.re_wind.ui.home.HomeFragment
import com.android.re_wind.ui.home.HomeViewModel
import com.android.re_wind.ui.home.adapter.ScheduleAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomePage : BaseFragment() {
    private var _binding: PageHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>(ownerProducer = { requireParentFragment() })
    private val adapter by lazy { ScheduleAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PageHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
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
            viewModel.todayScheduleList.collectLatest {
                adapter.submitList(it)
            }
        }
    }
}