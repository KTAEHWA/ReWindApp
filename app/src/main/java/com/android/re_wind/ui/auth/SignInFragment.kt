package com.android.re_wind.ui.auth

import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.re_wind.databinding.FragmentSignInBinding
import com.android.re_wind.ui.BaseFragment
import com.android.re_wind.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch

class SignInFragment : BaseFragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val listener: (Editable?) -> Unit = {
                val email = emailEditText.text?.toString()?.trim() ?: ""
                val password = passwordEditText.text?.toString()?.trim() ?: ""

                with(emailField) {
                    error = null
                    isErrorEnabled = false
                }

                with(passwordField) {
                    error = null
                    isErrorEnabled = false
                }

                signInButton.isEnabled =
                    Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.isNotEmpty()
            }

            emailEditText.doAfterTextChanged(listener)
            passwordEditText.doAfterTextChanged(listener)

            signInButton.setOnClickListener {
                val email = emailEditText.text?.toString()?.trim() ?: ""
                val password = passwordEditText.text?.toString()?.trim() ?: ""

                lifecycleScope.launch {
                    signIn(email, password)
                }
            }

            signUpButton.setOnClickListener {
                replaceFragment(SignUpFragment(), "sign_up")
            }
        }
    }

    /**
     * 로그인 함수
     *
     * @param email 이메일 주소
     * @param password 비밀번호
     */
    private suspend fun signIn(email: String, password: String) {
        isProgressVisible = true

        try {
            viewModel.signIn(email, password)
            replaceFragment(HomeFragment(), "main")
        } catch (e: Exception) {
            isProgressVisible = false

            when (e) {
                is FirebaseAuthInvalidUserException -> {
                    binding.emailField.error = "해당 이메일 주소로 가입된 계정이 없습니다."
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    binding.passwordField.error = "비밀번호가 틀렸습니다."
                }

                else -> {
                    e.printStackTrace()
                }
            }
        }
    }
}