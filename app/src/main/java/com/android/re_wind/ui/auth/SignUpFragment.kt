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
import com.android.re_wind.databinding.FragmentSignUpBinding
import com.android.re_wind.ui.BaseFragment
import com.android.re_wind.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.launch

class SignUpFragment : BaseFragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
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
                val nick = nickEditText.text?.toString()?.trim() ?: ""
                val email = emailEditText.text?.toString()?.trim() ?: ""
                val password = passwordEditText.text?.toString()?.trim() ?: ""

                with(nickField) {
                    error = null
                    isErrorEnabled = false
                }

                with(emailField) {
                    error = null
                    isErrorEnabled = false
                }

                with(passwordField) {
                    error = null
                    isErrorEnabled = false
                }

                signUpButton.isEnabled = nick.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches() && password.isNotEmpty()
            }

            nickEditText.doAfterTextChanged(listener)
            emailEditText.doAfterTextChanged(listener)
            passwordEditText.doAfterTextChanged(listener)

            signUpButton.setOnClickListener {
                val nick = nickEditText.text?.toString()?.trim() ?: ""
                val email = emailEditText.text?.toString()?.trim() ?: ""
                val password = passwordEditText.text?.toString()?.trim() ?: ""

                lifecycleScope.launch {
                    signUp(nick, email, password)
                }
            }

            signInButton.setOnClickListener {
                replaceFragment(SignInFragment(), "sign_in")
            }
        }
    }

    /**
     * 회원 가입 함수
     *
     * @param nick 닉네임
     * @param email 이메일 주소
     * @param password 비밀번호
     */
    private suspend fun signUp(nick: String, email: String, password: String) {
        isProgressVisible = true

        try {
            viewModel.signUp(nick, email, password)
            replaceFragment(HomeFragment(), "main")
        } catch (e: Exception) {
            isProgressVisible = false

            when (e) {
                is FirebaseAuthWeakPasswordException -> {
                    binding.passwordField.error = "숫자, 문자를 조합하여 8자 이상으로 입력해 주세요."
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    binding.emailField.error = "이메일 주소를 올바르게 입력해 주세요."
                }

                is FirebaseAuthUserCollisionException -> {
                    binding.emailField.error = "사용중인 이메일 주소 입니다."
                }

                else -> {
                    e.printStackTrace()
                }
            }
        }
    }
}