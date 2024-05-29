package com.android.re_wind.ui

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.android.re_wind.R

open class BaseFragment : Fragment() {
    /**
     * Progress view visibility
     */
    var isProgressVisible: Boolean?
        get() = activity?.findViewById<View>(R.id.progress_view)?.isVisible
        set(value) {
            if (value != null) {
                activity?.findViewById<View>(R.id.progress_view)?.isVisible = value
            }
        }

    /**
     * Fragment 교체 함수
     *
     * @param fragment 교체할 Fragment
     * @param tag Fragment tag
     */
    fun replaceFragment(fragment: Fragment, tag: String? = null) {
        val fragmentManager = requireActivity().supportFragmentManager

        if (tag != null) {
            if (fragmentManager.findFragmentByTag(tag) != null) return
        }

        isProgressVisible = false

        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }
}