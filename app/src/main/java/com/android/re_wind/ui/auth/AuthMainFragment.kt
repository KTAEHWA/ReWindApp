package com.android.re_wind.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.re_wind.databinding.FragmentAuthMainBinding
import com.android.re_wind.ui.BaseFragment

class AuthMainFragment : BaseFragment() {
    private var _binding: FragmentAuthMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 회원가입 클릭 시 회원가입 페이지로 이동
        with(binding) {
            signUpButton.setOnClickListener {
                replaceFragment(SignUpFragment(), "sign_up")
            }

            // 로그인 클릭 시 로그인 페이지로 이동
            signInButton.setOnClickListener {
                replaceFragment(SignInFragment(), "sign_in")
            }
        }
    }
}